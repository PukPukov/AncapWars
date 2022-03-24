package Wars.WarStates.WarNations;

import Wars.AncapWars.WarObject;
import Wars.WarPlayers.AncapWarrior;
import Wars.WarStates.WarCities.WarCity;
import Wars.WarStates.WarState;
import Wars.WarStates.WarStateType;
import states.States.City.City;
import states.States.Nation.Nation;

import java.util.ArrayList;
import java.util.List;

public class WarNation extends Nation implements WarState {

    @Deprecated
    public WarNation(String id) {
        super(id);
    }

    @Override
    public void declareWar(WarState state) {

    }

    @Override
    public void offerPeace(WarState state) {

    }

    @Override
    public AncapWarrior getLeader() {
        return new AncapWarrior(this.getCapital().getMayor());
    }

    @Override
    public boolean isNeutral() {
        return false;
    }

    @Override
    public boolean isMinister(AncapWarrior ancapWarrior) {
        return ancapWarrior.isMinister();
    }

    @Override
    public WarStateType getType() {
        return WarStateType.NATION;
    }

    // Переименовать в getCities после выхода AncapStates 3 (и переписать тоже)
    public List<WarCity> getWarCities() {
        List<WarCity> warCities = new ArrayList<>();
        City[] cities = super.getCities();
        for (City city : cities) {
            warCities.add(new WarCity(city.getID()));
        }
        return warCities;
    }

    @Override
    public int getRang() {
        return 2;
    }

    @Override
    public List<WarObject> getLowerRangObjects() {
        return new ArrayList<>(this.getWarCities());
    }

    @Override
    public WarObject getParentObject() {
        return null;
    }
}
