package ru.ancap.states.wars.api.player;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ancap.commons.debug.AncapDebug;
import ru.ancap.framework.communicate.communicator.Communicator;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.hexagon.Hexagon;
import ru.ancap.library.Balance;
import ru.ancap.misc.money.exception.NotEnoughMoneyException;
import ru.ancap.states.dynmap.DynmapDrawer;
import ru.ancap.states.id.ID;
import ru.ancap.states.player.AncapStatesPlayer;
import ru.ancap.states.player.PlayerNotFoundException;
import ru.ancap.states.states.Nation.Nation;
import ru.ancap.states.states.city.City;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.castle.BuiltCastle;
import ru.ancap.states.wars.api.castle.exception.CastleNameOccupiedException;
import ru.ancap.states.wars.api.castle.exception.HexagonAlreadyHaveCastleException;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.hexagon.exception.NotDevastatedException;
import ru.ancap.states.wars.api.request.system.Path;
import ru.ancap.states.wars.api.request.system.exception.AlreadySentException;
import ru.ancap.states.wars.api.request.system.exception.NoRequestException;
import ru.ancap.states.wars.api.state.Barrier;
import ru.ancap.states.wars.api.state.CityState;
import ru.ancap.states.wars.api.state.NationState;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.api.state.exception.DevastatedException;
import ru.ancap.states.wars.api.war.WarData;
import ru.ancap.states.wars.debug.Debugger;
import ru.ancap.states.wars.id.WarID;
import ru.ancap.states.wars.plugin.executor.exception.*;
import ru.ancap.states.wars.plugin.listener.AssaultRuntimeType;
import ru.ancap.states.wars.utils.NPathDatabase;

import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

@RequiredArgsConstructor
public class Warrior {

    @Delegate
    private final AncapStatesPlayer player;
    private final PathDatabase database;
    
    @NotNull
    public static Warrior get(Player player) {
        AncapStatesPlayer states = AncapStatesPlayer.get(player);
        return new Warrior(states, Warrior.databaseFor(states));
    }
    
    @NotNull
    public static Warrior link(String id) {
        AncapStatesPlayer states = AncapStatesPlayer.link(id);
        return new Warrior(states, Warrior.databaseFor(states));
    }
    
    @NotNull
    public static Warrior findByName(String name) throws PlayerNotFoundException {
        AncapStatesPlayer states = AncapStatesPlayer.findByName(name);
        return new Warrior(states, Warrior.databaseFor(states));
    }
    
    @NotNull
    public static Warrior findByNameFor(String name, Warrior caller) {
        AncapStatesPlayer states = AncapStatesPlayer.findByNameFor(name, caller.player);
        return new Warrior(states, Warrior.databaseFor(states));
    }
    
    @NotNull
    public static Warrior findByID(String id) {
        AncapStatesPlayer states = AncapStatesPlayer.findByID(id);
        return new Warrior(states, Warrior.databaseFor(states));
    }
    
    private static PathDatabase databaseFor(AncapStatesPlayer player) {
        return AncapWars.database().inner(Path.dot("warriors", player.getID()));
    }

    @Nullable
    public CityState getWarCity() {
        City city = this.player.getCity();
        Debugger.mark("getWarCity", "city", city);
        if (city == null) return null;
        return new CityState(city.getID());
    }

    public void buildCastle(String name) throws
            NoPermissionException,
            CastleNameOccupiedException,
            HexagonAlreadyHaveCastleException,
            InvalidNameException,
            CantBuildInUnstableException,
            DevastatedException,
            NotEnoughMoneyException {
        CityState city = this.getWarCity();
        if (city == null || !this.haveAssistantPermsTo(city)) throw new NoPermissionException();
        city.buildCastle(this.online(), name, this.online().getLocation());
    }

    public boolean haveAssistantPermsTo(CityState cityApi) {
        return cityApi.getAssistants().stream().anyMatch(player -> player.getID().equals(this.getID().toLowerCase())) || 
            cityApi.getMayor().getID().equalsIgnoreCase(this.getID());
    }

    @Nullable
    public WarState state() {
        CityState city = this.getWarCity();
        if (city == null) return null;
        WarState nation = city.getWarNation();
        if (nation == null) return city;
        return nation;
    }

    public void declareWarTo(WarState api, WarData data) throws 
            NoPermissionException, 
            StateIsNeutralException, 
            AlreadyInWarException, 
            IdiotException, 
            AllyDeclareException,
            NameAlreadyBoundException {
        if (!this.isWarStateLeader()) throw new NoPermissionException();
        this.state().declareWar(api, data);
    }

    public void requestPeaceTo(WarState stateAPI, String terms) throws NoPermissionException, NotInWarException, AlreadySentException {
        if (!this.isWarStateLeader()) throw new NoPermissionException();
        this.state().requestPeaceTo(stateAPI, terms);
    }

    public void revokePeaceRequestTo(WarState stateAPI) throws NoPermissionException, NotInWarException, NoRequestException {
        if (!this.isWarStateLeader()) throw new NoPermissionException();
        this.state().revokePeaceRequestTo(stateAPI);
    }

    public void acceptPeaceRequestFrom(WarState stateAPI) throws NoPermissionException, NotInWarException, NoRequestException {
        if (!this.isWarStateLeader()) throw new NoPermissionException();
        this.state().acceptPeaceRequestFrom(stateAPI);
    }

    public void cancelPeaceRequestFrom(WarState stateAPI) throws NoPermissionException, NotInWarException, NoRequestException {
        if (!this.isWarStateLeader()) throw new NoPermissionException();
        this.state().cancelPeaceRequestFrom(stateAPI);
    }

    private boolean isWarStateLeader() {
        if (this.isFree()) {
            AncapDebug.debug("free");
            return false;
        } else {
            WarState state = this.state();
            AncapDebug.debug("this.state() = ", state);
            Warrior leader = state.leader();
            AncapDebug.debug("state.leader() = ", leader);
            if (!leader.equals(this)) return false;
        }
        return true;
    }

    public boolean equals(Warrior warrior) {
        return warrior.getID().equals(this.getID());
    }

    public void renameCastle(String oldName, String newName) throws NotFoundException, NoPermissionException, CastleNameOccupiedException, InvalidNameException {
        BuiltCastle castleAPI = new BuiltCastle(WarID.castle().get(oldName));
        if (!castleAPI.exists()) {
            throw new NotFoundException();
        }
        if (!castleAPI.canBeManagedBy(this)) {
            throw new NoPermissionException();
        }
        castleAPI.rename(newName);
    }

    public boolean hasAssistantPerms() {
        if (this.isFree()) return false;
        if (this.player.isAssistant()) return true;
        else if (this.player.isMayor()) return true;
        return false;
    }

    public boolean hasMinisterPerms() {
        if (this.isFree()) return false;
        CityState api = this.getWarCity();
        if (api.isFree()) return false;
        if (this.player.isMinister()) return true;
        else if (this.player.isLeader()) return true;
        return false;
    }

    public boolean hasManagerPerms() {
        if (this.isFree()) return false;
        WarState api = this.state();
        return api.getManagerNames().contains(this.getName());
    }
    
    public WarHexagon getWarHexagon() {
        return new WarHexagon(this.getHexagon().code());
    }
    
    public void attackBarrier() throws 
            NoPermissionException, 
            NotFoundException, 
            HexagonIsProtectedException, 
            InvalidTimeException, 
            NotInWarException, 
            AttackAttackedHexagonException, 
            NotEnoughMoneyException,
            PenaltyException {
        if (!this.hasAssistantPerms()) throw new NoPermissionException();
        if (AncapWars.assaults().assault(this.getHexagon().code()).type() != AssaultRuntimeType.PEACE) throw new AttackAttackedHexagonException();
        Barrier barrier = this.getWarHexagon().barrier();
        if (barrier == null) throw new NotFoundException();
        WarState warState = this.state();
        int hour = LocalDateTime.now().atZone(ZoneOffset.of("+03:00")).getHour();
        time: if (hour < 8 || hour > 18) {
            if (AncapWars.debug()) {
                AncapDebug.debug("Объявляем атаку позже 18:00 по МСК по причине отладки");
                break time;
            }
            if (this.online().hasPermission("ru.ancap.states.wars.barrier.attack.at-invalid-time")) {
                Communicator.of(this.online()).message(new LAPIMessage(AncapWars.class, "barrier.attack.at-invalid-time"));
                break time;
            }
            throw new InvalidTimeException();
        }
        warState.declareAttack(barrier);
    }
    
    public CityState initializeTestCity(Function<Hexagon, Iterable<Hexagon>> territories) {
        Balance bigBalance = new Balance(10000, 10000, 10000);
        this.setBalance(bigBalance);
        String cityName = this.getName() + "City";
        City city = new City(ID.getCityID(cityName));
        city.create(this.player, cityName);
        territories.apply(city.getHomeHexagon()).forEach(city::addHexagon);
        city.setBalance(bigBalance);
        DynmapDrawer.redrawDynmap();
        return new CityState(city.getID());
    }
    
    public NationState initializeTestNation(Function<Hexagon, Iterable<Hexagon>> territories) {
        CityState cityState = this.initializeTestCity(territories);
        String nationName = this.getName() + "Nation";
        Nation nation = new Nation(ID.getNationID(nationName));
        nation.create(cityState.getCity(), nationName);
        nation.setBalance(new Balance(10000, 10000, 10000));
        return new NationState(nation.getID());
    }

    public void rebuildHexagon() throws 
            NotEnoughMoneyException,
            NotDevastatedException {
        Balance rebuildFee = new Balance(5, 5, 5);
        if (!this.getBalance().have(rebuildFee)) throw new NotEnoughMoneyException();
        Balance balance = this.getBalance();
        balance.remove(rebuildFee);
        this.setBalance(balance);
        this.getWarHexagon().restore();
    }

    public boolean canManageWarsOf(WarState warState) {
        return warState.leader().equals(this);
    }
    
    public NPathDatabase ndatabase() {
        return NPathDatabase.of(this.database());
    }
    
}