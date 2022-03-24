package Wars.WarStates.WarCities;

import AncapLibrary.Economy.Balance;
import Wars.AncapWars.AncapWars;
import Wars.AncapWars.WarObject;
import Wars.Building.Castle.Castle;
import Wars.Location.WarLocation;
import Wars.WarHexagons.WarHexagon;
import Wars.WarPlayers.AncapWarrior;
import Wars.WarStates.WarNations.WarNation;
import Wars.WarStates.WarState;
import Wars.WarStates.WarStateType;
import library.Hexagon;
import states.Player.AncapStatesPlayer;
import states.States.City.City;
import states.States.Nation.Nation;

import java.util.ArrayList;
import java.util.List;

public class WarCity extends City implements WarState {

    @Deprecated
    public WarCity(String id) {
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
        return new AncapWarrior(this.getMayor());
    }

    @Override
    public boolean isNeutral() {
        return false;
    }

    @Override
    public boolean isMinister(AncapWarrior ancapWarrior) {
        return ancapWarrior.isAssistant();
    }

    @Override
    public WarStateType getType() {
        return WarStateType.CITY;
    }

    public boolean haveCastleCreationFee() {
        return this.getBalance().have(this.getCastleCreationFee());
    }

    public void createCastle(WarLocation location, String name) {
        location.createCastle(name);
        this.takeCastleCreationFee();
    }

    private void takeCastleCreationFee() {
        Balance balance = this.getBalance();
        balance.remove(this.getCastleCreationFee());
        this.setBalance(balance);
    }

    private Balance getCastleCreationFee() {
        Balance fee = AncapWars.getConfiguration().getStartCastleCreationFee();
        Balance feeRaising = AncapWars.getConfiguration().getCastleCreationFeeRaising();
        feeRaising.multiply(this.getCastles().size());
        fee.add(feeRaising);
        return fee;
    }

    // Переписать после AncapStates 3 (переименовать в getTerritories)
    @Deprecated(forRemoval = true)
    public List<WarHexagon> getHexagons() {
        List<WarHexagon> hexagons = new ArrayList<>();
        for (Hexagon hexagon : super.getTerritories()) {
            hexagons.add(new WarHexagon(hexagon));
        }
        return hexagons;
    }

    public List<Castle> getCastles() {
        List<WarHexagon> hexagons = this.getHexagons();
        List<Castle> castles = new ArrayList<>();
        for (WarHexagon hexagon : hexagons) {
            if (hexagon.getCastle() != null) {
                castles.add(hexagon.getCastle());
            }
        }
        return castles;
    }

    // Тоже переписать после выхода AncapStates 3
    public WarNation getWarNation() {
        Nation nation = super.getNation();
        if (nation == null) {
            return  null;
        }
        return (WarNation) AncapWars.getWarStateMap().findWarState(WarStateType.NATION, nation.getName());
    }

    // Переименовать в getResidents после выхода AncapStates 3 (и переписать тоже)
    public List<AncapWarrior> getWarriors() {
        List<AncapWarrior> warriors = new ArrayList<>();
        AncapStatesPlayer[] players = super.getResidents();
        for (AncapStatesPlayer player : players) {
            warriors.add(new AncapWarrior(player));
        }
        return warriors;
    }

    @Override
    public int getRang() {
        return 1;
    }

    @Override
    public List<WarObject> getLowerRangObjects() {
        return new ArrayList<>(this.getWarriors());
    }

    @Override
    public WarObject getParentObject() {
        return this.getWarNation();
    }
}
