package Wars.WarStates;

import Wars.AncapWars.WarObject;
import Wars.WarPlayers.AncapWarrior;
import states.States.AncapState;

public interface WarState extends AncapState, WarObject {

    void declareWar(WarState state);
    void offerPeace(WarState state);

    String getName();

    AncapWarrior getLeader();

    boolean isNeutral();

    boolean isMinister(AncapWarrior ancapWarrior);

    WarStateType getType();
}
