package ru.ancap.states.wars.api.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Delegate;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.hexagon.Hexagon;
import ru.ancap.states.event.events.NationDeleteEvent;
import ru.ancap.states.states.Nation.Nation;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.player.Warrior;
import ru.ancap.states.wars.lombok.Exclude;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor @Getter
/*@EqualsAndHashCode уже реализовано*/ @ToString(callSuper = true)
public class NationState extends WarState {
    
    @Delegate(excludes = {Exclude.Remove.class, Exclude.PrepareToDelete.class, Exclude.Delete.class, Exclude.GetDatabase.class})
    private final Nation nation;
    private final PathDatabase database;
    
    public NationState(String id) {
        this.nation = new Nation(id);
        this.database = AncapWars.database().inner("states."+id);
    }
    
    @Override
    public void remove() {
        new NationDeleteEvent(nation).callEvent();
    }
    
    @Override
    public WarState warActor() {
        return this;
    }
    
    @Override
    public boolean containsIn(WarState stateAPI) {
        return this.id().equals(stateAPI.id());
    }
    
    @Override
    public Warrior leader() {
        return Warrior.findByID(this.nation.getCapital().getMayor().getID());
    }
    
    @Override
    public void claimHexagon(WarHexagon hexagon) {
        for (Hexagon hex : hexagon.neighbors(1)) {
            WarHexagon warHex = new WarHexagon(hex.code());
            CityState owner = warHex.getOwner();
            if (owner != null && owner.containsIn(this)) {
                owner.claimHexagon(hexagon);
            }
        }
        new CityState(this.getCapital().getID()).claimHexagon(hexagon);
    }
    
    @Override
    public void unclaimHexagon(WarHexagon hexagon) {
        CityState owner = hexagon.getOwner();
        if (owner == null || !owner.containsIn(this)) throw new IllegalStateException();
        owner.unclaimHexagon(hexagon);
    }
    
    @Override
    public List<Warrior> getPersons() {
        return this.nation.getResidents().stream()
            .map(resident -> Warrior.findByID(resident.getID())).toList();
    }
    
    @Override
    public List<Warrior> getManagers() {
        return this.getPersons().stream()
            .filter(Warrior::hasMinisterPerms).toList();
    }
    
    @Override
    public Collection<WarHexagon> territories() {
        return this.getTerritories().stream()
            .map(Hexagon::code)
            .map(WarHexagon::new)
            .toList();
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        return Objects.equals(this.nation.getID(), ((NationState) object).nation.getID());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.nation.getID());
    }
    
}
