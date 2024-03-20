package ru.ancap.states.wars.api.hexagon;

import lombok.*;
import lombok.experimental.Delegate;
import org.bukkit.Location;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.framework.database.nosql.SerializeWorker;
import ru.ancap.hexagon.Hexagon;
import ru.ancap.hexagon.common.Point;
import ru.ancap.library.LocationSerializeWorker;
import ru.ancap.states.AncapStates;
import ru.ancap.states.states.city.City;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.castle.BuiltCastle;
import ru.ancap.states.wars.api.castle.Castle;
import ru.ancap.states.wars.api.hexagon.exception.AlreadyDevastatedException;
import ru.ancap.states.wars.api.hexagon.exception.NotDevastatedException;
import ru.ancap.states.wars.api.state.Barrier;
import ru.ancap.states.wars.api.state.CityState;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.api.war.WarView;
import ru.ancap.states.wars.lombok.Exclude;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
@EqualsAndHashCode @ToString
public class WarHexagon {

    @Delegate(excludes = Exclude.GetNeighbors.class)
    private final Hexagon hexagon;
    @EqualsAndHashCode.Exclude @ToString.Exclude
    private final PathDatabase database;

    public WarHexagon(long code) {
        this.hexagon = AncapStates.grid.hexagon(code);
        this.database = AncapWars.database().inner("hexagons."+code);
    }
    
    public DevastationStatus devastation() {
        @Nullable Boolean devastatedObject = this.database.read("devastated.status", SerializeWorker.BOOLEAN);
        boolean devastated = devastatedObject == null ? false : devastatedObject;
        return !devastated ? 
            new DevastationStatus.Normal() : 
            new DevastationStatus.Devastated(this.database.read("devastated.location", LocationSerializeWorker.INSTANCE));
    }
    
    public void devastate(Location brokenCastleLocation) throws AlreadyDevastatedException {
        if (this.devastation() instanceof DevastationStatus.Devastated) throw new AlreadyDevastatedException();
        this.database.write("devastated.status", true);
        this.database.write("devastated.location", brokenCastleLocation, LocationSerializeWorker.INSTANCE);
    }
    
    public void restore() throws NotDevastatedException {
        if (this.devastation() instanceof DevastationStatus.Normal) throw new NotDevastatedException();
        this.database.write("devastated.status", false);
    }

    public @Nullable Castle castle() {
        String castleId = this.database.readString("castle");
        if (castleId != null) return new BuiltCastle(castleId);
        return null;
    }

    public sealed interface DevastationStatus permits DevastationStatus.Normal, DevastationStatus.Devastated {

        final class Normal implements DevastationStatus { }
        record Devastated(Location brokenCastleLocation) implements DevastationStatus { }
        
    }
    
    public @Nullable CityState getOwner() {
        City city = AncapStates.getCityMap().getCity(this.hexagon);
        if (city == null) return null;
        return new CityState(city.getID());
    }
    
    public @Nullable Barrier barrier() {
        Castle castle = this.castle();
        if (castle != null) return castle;
        City city = AncapStates.getCityMap().getCity(this.hexagon);
        if (city == null) return null;
        CityState api = new CityState(AncapStates.getCityMap().getCity(this.hexagon).getID());
        if (api.getHomeHexagon().code() == this.hexagon.code()) return api.coreBarrier();
        return null;
    }

    public boolean canBeAttackedBy(@NonNull WarState attacker) {
        WarState owner = this.getOwner();
        if (owner == null) return false;
        if (attacker.containsHex(this)) return false;
        List<WarHexagon> apis = this.getNeighbors().stream()
            .map(hex -> new WarHexagon(hex.code())).toList();
        int enemies = 0;
        for (WarHexagon neighbourHexagonApi : apis) {
            CityState neighbourStateApi = neighbourHexagonApi.getOwner();
            if (neighbourStateApi == null) continue;
            if (neighbourStateApi.containsIn(attacker)) enemies++;
            else {
                Barrier api = neighbourHexagonApi.barrier();
                if (api != null && api.protectLevel() > 1) return false;
            }
        }
        if (enemies<2) return false;
        if (this.barrier() != null) return false;
        WarView defenderApi = attacker.warWith(owner);
        WarView attackerApi = defenderApi.invert();
        if (attackerApi.atPenalty()) return false;
        return true;
    }

    public List<WarHexagon> getPathsTo(WarState state) {
        CityState owner = this.getOwner();
        if (owner == null) throw new IllegalStateException();
        WarState container = owner.warActor();
        if (state.containsHex(this)) throw new IllegalStateException();
        Set<Hexagon> hexagons = this.hexagon.neighbors(1);
        return hexagons.stream()
            .map(hex -> new WarHexagon(hex.code()))
            .filter(hex -> hex.barrier() == null && container.containsHex(hex))
            .filter(hex -> hex.neighborOf(state)).toList();
    }

    public boolean neighborOf(WarState state) {
        for (WarHexagon hex : this.getNeighbors()) if (state.containsHex(hex)) return true;
        return false;
    }

    public List<WarHexagon> getNeighbors() {
        return this.hexagon.neighbors(1).stream()
                .map(hex -> new WarHexagon(hex.code()))
                .toList();
    }
    
    public String getReadableBlockCoordinates() {
        Point center = this.hexagon.center();
        return "X: "+ (int) center.x()+"; "+
               "Z: "+ (int) center.y();
    }
    
    public String getReadableHexagonCoordinates() {
        return this.q() +"; "+
               this.r();
    }
    
    public void setCastle(@Nullable BuiltCastle api) {
        if (api == null) {
            this.database.delete("castle");
            return;
        }
        this.database.write("castle", api.getId());
    }
    
}
