package Wars.Battle.FieldConflict;

import Wars.Battle.Battle;
import Wars.WarHexagons.WarHexagon;
import Wars.WarStates.WarState;

public class FieldConflict implements Battle {

    private WarHexagon hexagon;
    private WarState attacker;
    private FieldConflictHealth health;

    public FieldConflict(WarHexagon hexagon, WarState attacker) {
        this.hexagon = hexagon;
        this.attacker = attacker;
        this.health = new FieldConflictHealth();
    }

    public FieldConflictHealth getHealth() {
        return this.health;
    }
}
