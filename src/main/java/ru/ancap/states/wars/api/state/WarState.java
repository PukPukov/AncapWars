package ru.ancap.states.wars.api.state;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.ancap.commons.aware.Aware;
import ru.ancap.commons.aware.ContextAware;
import ru.ancap.commons.aware.InsecureContextHandle;
import ru.ancap.commons.debug.AncapDebug;
import ru.ancap.framework.communicate.communicator.Communicator;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.library.Balance;
import ru.ancap.misc.money.exception.NotEnoughMoneyException;
import ru.ancap.states.AncapStates;
import ru.ancap.states.id.ID;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.castle.Castle;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.player.Warrior;
import ru.ancap.states.wars.api.request.system.*;
import ru.ancap.states.wars.api.request.system.exception.AlreadySentException;
import ru.ancap.states.wars.api.request.system.exception.NoRequestException;
import ru.ancap.states.wars.api.war.War;
import ru.ancap.states.wars.api.war.WarData;
import ru.ancap.states.wars.api.war.WarView;
import ru.ancap.states.wars.connector.StateType;
import ru.ancap.states.wars.event.BarrierAttackDeclareEvent;
import ru.ancap.states.wars.event.WarDeclareEvent;
import ru.ancap.states.wars.id.WarID;
import ru.ancap.states.wars.messaging.Message;
import ru.ancap.states.wars.plugin.executor.exception.*;
import ru.ancap.states.wars.plugin.listener.AssaultRuntime;
import ru.ancap.states.wars.plugin.listener.AssaultStatus;
import ru.ancap.states.wars.utils.LAPIReceiver;

import javax.annotation.Nullable;
import javax.naming.NameAlreadyBoundException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@EqualsAndHashCode @ToString
public abstract class WarState {

    @NotNull
    public static WarState of(@NotNull String warId) {
        Validate.notNull(warId, "You can't find state by null id");
        PathDatabase database = AncapWars.database().inner("states." + warId);
        String type = database.readString("type");
        if (type == null) throw new IllegalStateException("State can not hold null type, but requested state ("+warId+") has null type");
        return switch (StateType.valueOf(type.toUpperCase())) {
            case CITY -> new CityState(warId);
            case NATION -> new NationState(warId);
        };
    }

    @Nullable
    public static WarState ofName(@NotNull StateType type, @NotNull String name) {
        PathDatabase database = AncapWars.database().inner("states");
        AncapDebug.debug("ofName", type, name);
        return switch (type) {
            case CITY -> {
                String id = ID.getCityID(name);
                if (!database.isSet(id + ".type")) yield null;
                yield new CityState(id);
            }
            case NATION -> {
                String id = ID.getNationID(name);
                if (!database.isSet(id + ".type")) yield null;
                yield new NationState(id);
            }
        };
    }

    public static void initialize(@NotNull String id, @NotNull StateType type) {
        PathDatabase database = AncapWars.database().inner("states." + id);
        database.write("type", type.name());
    }

    public abstract Balance getBalance();
    public abstract void setBalance(Balance balance);
    public abstract void remove();
    public abstract WarState warActor();
    public abstract boolean containsIn(WarState stateAPI);
    public abstract PathDatabase getDatabase();
    public abstract String id();
    public abstract String name();
    @Deprecated
    public String getName() {
        return this.name();
    }
    public abstract boolean exists();
    public abstract Warrior leader();
    public abstract void claimHexagon(WarHexagon hexagon);
    public abstract void unclaimHexagon(WarHexagon hexagon);
    public abstract List<Warrior> getPersons();
    public abstract List<Warrior> getManagers();
    public abstract Collection<WarHexagon> territories();
    
    public DuoRequester asDuoRequester(String type, WarState target) {
        return new BaseDuoRequester(this.asRequester(type), target.asRequester(type));
    }
    
    public Requester asRequester(String type) {
        return new DatabaseRequester(this.id(), type, this.getDatabase().inner("requests"));
    }

    public List<Player> getOnlinePlayers() {
        return this.getPersons().stream()
            .filter(person -> Bukkit.getPlayer(person.getName()) != null)
            .map(person -> Bukkit.getPlayer(person.getName()))
            .toList();
    }

    public List<String> getManagerNames() {
        return this.getManagers().stream().map(Warrior::getName).toList();
    }

    public void declareAttack(Barrier barrier) throws HexagonIsProtectedException, NotInWarException, NotEnoughMoneyException, PenaltyException {
        WarState target = barrier.hexagon().getOwner();
        if (target == null || !this.atWarWith(target)) throw new NotInWarException();
        WarView warView = this.warWith(target);
        if (warView.atPenalty()) throw new PenaltyException();
        Balance attackFee = barrier.attackFee();
        if (!this.getBalance().have(attackFee)) throw new NotEnoughMoneyException();
        Balance newBalance = this.getBalance();
        newBalance.remove(attackFee);
        this.setBalance(newBalance);
        if (!barrier.canBeAttackedBy(this)) throw new HexagonIsProtectedException();
        AncapDebug.debug("Бросаем BarrierAttackDeclareEvent c "+barrier+", "+this.name());
        Bukkit.getOnlinePlayers().stream().map(Communicator::of)
            .forEach(communicator -> communicator.message(barrier.attackMessage(this, target)));
        new BarrierAttackDeclareEvent(barrier, this, warView.war()).callEvent();
    }

    private boolean isAtPenalty() {
        return false;
    }

    public void declareWar(WarState state, WarData data) throws AlreadyInWarException, IdiotException, AllyDeclareException, StateIsNeutralException, NameAlreadyBoundException {
        if (this.id().equals(state.id()))     throw new IdiotException();
        if (this.isAllyOf(state))             throw new AllyDeclareException();
        if (this.atWarWith(state))            throw new AlreadyInWarException();
        if (state.neutral())                  throw new StateIsNeutralException(state.name());
        if (WarID.war().isBound(data.name())) throw new NameAlreadyBoundException();

        new WarDeclareEvent(this, state, data).callEvent();
    }

    private boolean isAllyOf(WarState state) {
        return this.containsIn(state) || state.containsIn(this);
    }

    public void requestPeaceTo(WarState enemy, String terms) throws NotInWarException, AlreadySentException {
        enemy = enemy.warActor();
        WarView warView = this.warWith(enemy);
        if (warView == null || !warView.war().active()) throw new NotInWarException();
        DuoRequester requester = new BaseDuoRequester(this.asRequester("peace"), enemy.asRequester("peace"));
        RequestState request = requester.send(terms);
        WarState finalEnemy = enemy;
        this.getOnlinePlayers().forEach(player -> LAPIReceiver.send(
            Message.Minecraft.Notify.War.Peace.Request.Offer.YOU, player,
            "%STATE%", finalEnemy.name(),
            "%TERMS%", request.terms()
        ));
        enemy.getOnlinePlayers().forEach(player -> LAPIReceiver.send(
            Message.Minecraft.Notify.War.Peace.Request.Offer.TO_YOU, player,
            "%STATE%", this.name(),
            "%TERMS%", request.terms()
        ));
    }


    public void revokePeaceRequestTo(WarState enemy) throws NoRequestException {
        enemy = enemy.warActor();
        DuoRequester requester = new BaseDuoRequester(this.asRequester("peace"), enemy.asRequester("peace"));
        RequestState state = requester.unsend();
        WarState finalEnemy = enemy;
        this.getOnlinePlayers().forEach(player -> LAPIReceiver.send(
            Message.Minecraft.Notify.War.Peace.Request.Revoke.YOU, player,
            "%STATE%", finalEnemy.name(),
            "%TERMS%", state.terms()
        ));
    }

    public void acceptPeaceRequestFrom(WarState enemy) throws NotInWarException, NoRequestException {
        enemy = enemy.warActor();
        WarView warView = this.warWith(enemy);
        if (warView == null || !warView.war().active()) throw new NotInWarException();
        DuoRequester baseRequester = new BaseDuoRequester(this.asRequester("peace"), enemy.asRequester("peace"));
        RequestState state = baseRequester.unreceive();
        WarState finalEnemy = enemy;
        this.getOnlinePlayers().forEach(player -> LAPIReceiver.send(
            Message.Minecraft.Notify.War.Stop.PEACE, player,
            "%STATE%", finalEnemy.name(),
            "%TERMS%", state.terms()
        ));
        enemy.getOnlinePlayers().forEach(player -> LAPIReceiver.send(
            Message.Minecraft.Notify.War.Stop.PEACE, player,
            "%STATE%", this.name(),
            "%TERMS%", state.terms()
        ));

        warView.war().stop(new War.PeaceStrategy(enemy, state.terms()));
    }

    public void cancelPeaceRequestFrom(WarState enemy) throws NoRequestException {
        enemy = enemy.warActor();
        DuoRequester requester = new BaseDuoRequester(this.asRequester("peace"), enemy.asRequester("peace"));
        RequestState state = requester.unreceive();
        WarState finalEnemy = enemy;
        this.getOnlinePlayers().forEach(player -> LAPIReceiver.send(
            Message.Minecraft.Notify.War.Peace.Request.Reject.YOU, player,
            "%STATE%", finalEnemy.name(),
            "%TERMS%", state.terms()
        ));

        enemy.getOnlinePlayers().forEach(player -> LAPIReceiver.send(
            Message.Minecraft.Notify.War.Peace.Request.Reject.YOURS, player,
            "%STATE%", this.name(),
            "%TERMS%", state.terms()
        ));
    }

    public void setNeutralityFor(long duration, TimeUnit timeUnit) {
        this.setNeutralUntil(System.currentTimeMillis() + timeUnit.toMillis(duration));
    }

    private void setNeutralUntil(long millis) {
        this.getDatabase().write("neutral-until", String.valueOf(millis));
    }

    public boolean neutral() {
        return this.warActor().name().equalsIgnoreCase("ANCAP_LTD");
    }

    public boolean atWarWith(WarState state) {
        return this.warWith(state) != null;
    }

    @Nullable
    public WarView warWith(WarState state) {
        return this.viewActiveWars().stream()
            .filter(warView -> warView.opponent().id().equals(state.warActor().id()))
            .findAny().orElse(null);
    }

    public List<WarView> viewWars() {
        return this.wars().stream()
            .map(war -> war.viewAs(this.warActor().id())).toList();
    }
    
    public List<War> wars() {
        return this.warActor().selfWars();
    }
    
    public List<War> selfWars() {
        return this.getDatabase().readStrings("wars", true).stream()
            .map(War::new).toList();
    }
    
    public List<WarView> viewActiveWars() {
        return this.viewWars().stream().filter(warView -> warView.war().active()).toList();
    }

    public void transferHexagonFrom(WarHexagon hexagon, WarState loser) {
        if (true) {
            AncapDebug.debug("TRANSFER HEXAGONS STARTED");
            loser.unclaimHexagon(hexagon);
            AncapDebug.debug("TRANSFER HEXAGONS CONTINUED");
            this.claimHexagon(hexagon);
            AncapDebug.debug("TRANSFER HEXAGONS ENDED");
        } else {
            try {
                AncapDebug.debug("TRANSFER HEXAGONS STARTED");
                loser.unclaimHexagon(hexagon);
                AncapDebug.debug("TRANSFER HEXAGONS CONTINUED");
                this.claimHexagon(hexagon);
                AncapDebug.debug("TRANSFER HEXAGONS ENDED");
            } catch (Throwable throwable) {
                AncapDebug.debug("EXCEPTION CATCHED!!!!!!!!!!!!!!");
                throwable.printStackTrace();
                throw new IllegalStateException("EXCEPTION CATCHED");
            }
        }
    }

    public void delete() {
        this.getDatabase().nullify();
    }

    public void prepareToDelete() {
        this.failTerritorialDefence();
        for (WarView war : this.viewActiveWars()) {
            war.war().stop(new War.DefeatStrategy(war.opponent()));
            war.opponent().getOnlinePlayers().forEach(player -> LAPIReceiver.send(
                Message.Minecraft.Notify.War.Stop.DEATH, player,
                "%STATE%", this.name()
            ));
        }
    }

    public void addWar(String id) {
        this.getDatabase().add("wars", id);
    }

    public void cancelPeaceRequests(String warID) {
        try { this.asDuoRequester("peace", new War(warID).viewAs(this.id()).opponent()).unsend(); }
        catch (NoRequestException ignored) { }
    }

    public boolean containsHex(WarHexagon warHexagon) {
        WarState owner = warHexagon.getOwner();
        if (owner == null) return false;
        return owner.containsIn(this);
    }
    
    public List<Barrier> barriers() {
        return Stream.concat(
            this.getCastles().stream(),
            AncapStates.cityMap().cities().stream()
                .map(city -> new CityState(city.id()))
                .map(CityState::coreBarrier)
                .filter(Objects::nonNull)
        ).toList();
    }

    public List<Castle> getCastles() {
        return this.territories().stream()
            .map(WarHexagon::castle)
            .filter(Objects::nonNull).toList();
    }
    
    public void failTerritorialDefence() {
        for (WarHexagon hexagon : this.territories()) {
            AssaultRuntime assault = AncapWars.assaults().assault(hexagon.code());
            if (assault.status().politicalState() != AssaultStatus.PoliticalState.PEACE) AncapWars.assaults().breakCastle(assault.barrier(), assault.attacker(), assault);
            if (AncapWars.fieldConflicts().atFieldConflict(hexagon.code())) AncapWars.fieldConflicts().occupy(hexagon.code());
        }
    }
    
    public void warIncorporate(WarState affiliate) {
        List<String> warIds = this.getDatabase().readStrings("wars", true);
        // переносим вью
        for (War war : this.selfWars()) {
            WarView warView = war.viewAs(this.id());
            WarView affiliateWarWithOpponent = affiliate.warWith(warView.opponent());
            if (affiliateWarWithOpponent != null) war.stop(new War.MergeStrategy(affiliateWarWithOpponent.war()));
            else warView.transferTo(affiliate);
        }
        // переносим принадлежности к войнам
        this.getDatabase().write("wars", List.of());
        for (String id : warIds) affiliate.addWar(id);
        AncapWars.assaults().makeIncorporation(this, affiliate);
        AncapWars.fieldConflicts().makeIncorporation(this, affiliate);
    }
    
    /**
     * Should be called only on war actors.
     */
    @ContextAware(awareOf = Aware.STATE, handle = InsecureContextHandle.NO_HANDLE)
    public List<WarHexagon> unprotectedBorderHexagons() {
        return this.territories().stream()
            .filter(hex -> hex.getNeighbours().stream().anyMatch(neighbour -> {
                var neighbourOwner = neighbour.getOwner();
                if (neighbourOwner == null) return true;
                return !this.equals(neighbourOwner.warActor());
            }))
            .filter(hex -> hex.barrier() == null)
            .filter(hex -> hex.getNeighbours().stream().noneMatch(neighbour -> (this.containsHex(neighbour) && neighbour.castle() != null)))
            .toList();
    }
    
    public String debugIdentifier() {
        return this.id() + " ("+this.name()+")";
    }
    
}