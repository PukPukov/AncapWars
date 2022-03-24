package Wars.WarStates.WarStateMap;

import Database.Database;
import Wars.AncapWars.WarAncapStatesIncompatibleError;
import Wars.Building.Castle.Castle;
import Wars.Location.StringPreWarLocation;
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

    public WarStateMap() {
        this.stateList = new ArrayList<>();
    }

    // Переписать на загрузку из сериализованного вида после завершения разработки AncapStates 3
    public void loadFromDb(Database database) {
        this.clear();
        this.loadWarStates(database);
        this.loadHexagons(database);
        this.loadCastles(database);
    }

    @Deprecated (forRemoval = true)
    private void loadCastles(Database database) {
        String[] castles = database.getKeys("states.castle");
        for (String castleName : castles) {
            WarLocation location = new StringPreWarLocation(database.getString("states.castle."+castleName+".location")).getPreparedLocation();
            String name = database.getString("states.castle."+castleName+".name");
            Castle castle = new Castle(location, name);
            WarHexagon hexagon = this.findWarHexagon(castle.getLocation().getHexagon().getQ(), castle.getLocation().getHexagon().getR());
            hexagon.setCastle(castle);
        }
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

    public WarState findWarState(WarStateType type, String name) {
        for (WarState state : this.stateList) {
            if (state.getName().equalsIgnoreCase(name) && state.getType() == type) {
                return state;
            }
        }
        throw new UnknownWarStateException("Unknown "+type.name()+": "+name);
    }

    public WarStateType findWarStateType(String typeName) {
        if (typeName.equals("city")) {
            return WarStateType.CITY;
        }
        if (typeName.equals("nation")) {
            return WarStateType.NATION;
        }
        throw new UnknownWarStateTypeException("Cant find WarStateType for "+typeName);
    }

    public List<WarState> getStateList() {
        return this.stateList;
    }

    public List<WarHexagon> getHexagonList() {
        return this.hexagonList;
    }
}
