package Wars.WarStates.WarStateMap;

import Database.Database;
import Wars.AncapWars.WarAncapStatesIncompatibleError;
import Wars.Building.Castle.CastleCore;
import Wars.Location.WarLocation;
import Wars.WarHexagons.WarHexagon;
import Wars.WarStates.WarCities.WarCity;
import Wars.WarStates.WarNations.WarNation;
import Wars.WarStates.WarState;
import Wars.WarStates.WarStateType;
import states.Main.AncapStates;

import java.util.ArrayList;
import java.util.List;

public class WarStateMap {

    private List<WarState> stateList;

    @Deprecated(forRemoval = true) // Удалить это после выпуска AncapStates 3
    private List<WarHexagon> hexagonList;

    private List<CastleCore> coreList;

    public WarStateMap() {
        this.stateList = new ArrayList<>();
        this.hexagonList = new ArrayList<>();
    }

    // Переписать на загрузку из сериализованного вида после завершения разработки AncapStates 3
    public void loadFromDb(Database database) {
        this.clear();
        this.loadWarStates(database);
    }


    @Deprecated (forRemoval = true)
    private void loadHexagons(Database database) {
        String[] hexagons = database.getKeys("states.hexagons");
        for (String hexagon : hexagons) {
            hexagonList.add(new WarHexagon(AncapStates.getInstance().getGrid().getHexagon(hexagon)));
        }
    }

    @Deprecated (forRemoval = true)
    private void loadWarStates(Database database) {
        String[] cityIDs = database.getKeys("states.city");
        String[] nationIDs = database.getKeys("states.nation");
        for (String cityID : cityIDs) {
            stateList.add(new WarCity(cityID));
        }
        for (String nationID : nationIDs) {
            stateList.add(new WarNation(nationID));
        }
    }

    private void clear() {
        stateList = new ArrayList<>();
    }

    public void saveToDb(Database database) {
        throw new WarAncapStatesIncompatibleError("this method is reserved for AncapStates 3");
    }

    public WarHexagon findWarHexagon(long q, long r) {
        for (WarHexagon hexagon : this.hexagonList) {
            if (hexagon.getQ() == q && hexagon.getR() == r) {
                return hexagon;
            }
        }
        throw new UnknownHexagonException("Unknown hexagon");
    }

    public WarState findWarState(WarStateType type, String name) throws UnknownWarStateException {
        for (WarState state : this.stateList) {
            if (state.getName().equalsIgnoreCase(name) && state.getType() == type) {
                return state;
            }
        }
        throw new UnknownWarStateException();
    }

    public WarStateType findWarStateType(String typeName) throws UnknownWarStateTypeException {
        if (typeName.equals("city")) {
            return WarStateType.CITY;
        }
        if (typeName.equals("nation")) {
            return WarStateType.NATION;
        }
        throw new UnknownWarStateTypeException();
    }

    public List<WarState> getStateList() {
        return this.stateList;
    }

    public List<WarHexagon> getHexagonList() {
        return this.hexagonList;
    }

    public List<CastleCore> getCoreList() {
        return this.coreList;
    }

    public CastleCore findCastleCore(WarLocation location) throws UnknownWarStateException {
        for (CastleCore core : this.coreList) {
            if (core.getLocation().blockEquals(location)) {
                return core;
            }
        }
        throw new UnknownWarStateException();
    }

    public void createCore(WarLocation location) {
        CastleCore core = new CastleCore(location);
        this.coreList.add(core);
        core.place();
    }
}
