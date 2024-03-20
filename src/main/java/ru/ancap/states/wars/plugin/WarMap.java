package ru.ancap.states.wars.plugin;

import lombok.AllArgsConstructor;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.states.AncapStates;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.castle.BuiltCastle;
import ru.ancap.states.wars.api.castle.Castle;
import ru.ancap.states.wars.api.state.Barrier;
import ru.ancap.states.wars.api.state.CityState;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class WarMap {

    private final PathDatabase database;

    public WarMap() {
        this(AncapWars.database());
    }

    public List<Castle> getCastles() {
        return this.database.inner("castles").keys().stream()
            .map(BuiltCastle::new)
            .map(Castle.class :: cast).toList();
    }

    public List<CityState> cities() {
        return AncapStates.getCityMap().getCities().stream()
            .map(city -> new CityState(city.getID())).toList();
    }

    public List<Barrier> barriers() {
        return this.cities().stream()
            .flatMap(city -> city.barriers().stream())
            .filter(Objects::nonNull).toList();
    }
    
}
