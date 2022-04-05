package Wars.WarStates;

import AncapLibrary.API.SMassiveAPI;
import Wars.AncapWars.WarObject;
import Wars.Events.WarDeclaredEvent.WarDeclaredEvent;
import Wars.WarPlayers.AncapWarrior;
import states.States.AncapState;

public interface WarState extends AncapState, WarObject {

    default void declareWar(WarState state) {
        WarDeclaredEvent event = new WarDeclaredEvent(this, state);
        event.call();
    }

    void offerPeace(WarState state);

    String getName();

    AncapWarrior getLeader();

    boolean isNeutral();

    boolean isMinister(AncapWarrior ancapWarrior);

    WarStateType getType();

    default boolean isInWarWith(WarState highestWarState) {
        String enemiesStr = this.getMeta("enemies");
        String[] enemies = SMassiveAPI.toMassive(enemiesStr);
        for (String enemy : enemies) {
            if (enemy.equals(highestWarState.getName())) {
                return true;
            }
        }
        return false;
    }

    default void addEnemy(WarState state) {
        String enemies = this.getMeta("enemies");
        this.setMeta("enemies", SMassiveAPI.add(enemies, state.getName()));
    }
}
