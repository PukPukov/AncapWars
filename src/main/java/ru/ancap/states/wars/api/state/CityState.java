package ru.ancap.states.wars.api.state;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Delegate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ancap.commons.null_.SafeNull;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.hexagon.Hexagon;
import ru.ancap.library.Balance;
import ru.ancap.misc.money.exception.NotEnoughMoneyException;
import ru.ancap.states.AncapStates;
import ru.ancap.states.event.events.CityDeleteEvent;
import ru.ancap.states.name.Name;
import ru.ancap.states.states.Nation.Nation;
import ru.ancap.states.states.city.City;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.castle.BuiltCastle;
import ru.ancap.states.wars.api.castle.CoreBarrier;
import ru.ancap.states.wars.api.castle.exception.CastleNameOccupiedException;
import ru.ancap.states.wars.api.castle.exception.HexagonAlreadyHaveCastleException;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.player.Warrior;
import ru.ancap.states.wars.api.state.exception.DevastatedException;
import ru.ancap.states.wars.event.CastleBuildEvent;
import ru.ancap.states.wars.fees.WarFees;
import ru.ancap.states.wars.id.WarID;
import ru.ancap.states.wars.lombok.Exclude;
import ru.ancap.states.wars.plugin.executor.exception.CantBuildInUnstableException;

import javax.naming.InvalidNameException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter 
/*@EqualsAndHashCode уже реализовано*/ @ToString(callSuper = true)
// нереализованные методы это баг IDE
public class CityState extends WarState {

    @Delegate
    private final City city;
    private final PathDatabase database;

    public CityState(String id) {
        this.city = new City(id);
        this.database = AncapWars.database().inner("states."+id);
    }

    @Override
    public void remove() {
        new CityDeleteEvent(this.city).callEvent();
    }
    
    /**
     * Previously highestWarState()
     */
    @Override
    public WarState warActor() {
        WarState nation = this.getWarNation();
        if (nation == null) return this;
        return nation.warActor();
    }

    @NotNull
    public Balance getCastleCreationFee() {
        return WarFees.CASTLE_CREATION;
    }

    @Override
    public Warrior leader() {
        return Warrior.findByID(this.city.getMayor().getID());
    }

    @Override
    public void claimHexagon(WarHexagon hexagon) {
        this.city.addHexagon(hexagon.getHexagon());
    }

    @Override
    public void unclaimHexagon(WarHexagon hexagon) {
        this.getCity().removeHexagon(hexagon.getHexagon());
    }

    @Override
    public List<Warrior> getPersons() {
        return this.city.getResidents().stream()
                .map(resident -> Warrior.findByID(resident.getID()))
                .toList();
    }

    @Override
    public List<Warrior> getManagers() {
        return this.getPersons()
                .stream()
                .filter(Warrior::hasAssistantPerms)
                .toList();
    }
    
    @Override
    public Collection<WarHexagon> territories() {
        return this.getTerritories().stream()
            .map(Hexagon::code)
            .map(WarHexagon::new).toList();
    }

    public boolean isCapital() {
        return this.getNation().getCapital().getID().equals(this.city.getID());
    }

    public void buildCastle(Player creator, String name, Location location) throws
            CastleNameOccupiedException,
            HexagonAlreadyHaveCastleException,
            InvalidNameException,
            CantBuildInUnstableException,
            NotEnoughMoneyException, 
            DevastatedException {
        if (!Name.canBeDefinedWith(name)) throw new InvalidNameException();
        BuiltCastle api = new BuiltCastle(WarID.castle().get(name));
        if (api.exists()) throw new CastleNameOccupiedException(name);
        Hexagon hexagon = AncapStates.grid.hexagon(location);
        WarHexagon warHexagon = new WarHexagon(hexagon.code());
        if (warHexagon.castle() != null) throw new HexagonAlreadyHaveCastleException();
        if (warHexagon.devastation() instanceof WarHexagon.DevastationStatus.Devastated) throw new DevastatedException();
        if (AncapWars.isUnstable(hexagon)) throw new CantBuildInUnstableException();
        Warrior warriorCreator = Warrior.get(creator);
        Balance fee = this.getCastleCreationFee();
        if (!warriorCreator.getBalance().have(fee)) throw new NotEnoughMoneyException();
        Balance balance = warriorCreator.getBalance();
        balance.remove(fee);
        warriorCreator.setBalance(balance);
        new CastleBuildEvent(api, creator, location, name).callEvent();
    }

    public boolean containsIn(WarState stateAPI) {
        if (this.getID().equals(stateAPI.id())) return true;
        Nation nation = this.getNation();
        if (nation == null) return false;
        
        return nation.getID().equals(stateAPI.id());
    }
    
    @Override
    public String name() {
        return this.city.getName();
    }
    
    @Nullable
    public NationState getWarNation() {
        return SafeNull.function(this.getNation(), notNull -> new NationState(notNull.getID()));
    }

    @Nullable
    public Barrier coreBarrier() {
        return CoreBarrier.of(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        return Objects.equals(this.city.getID(), ((CityState) object).city.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.city.getID());
    }
    
}