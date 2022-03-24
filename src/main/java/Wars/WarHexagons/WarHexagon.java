package Wars.WarHexagons;

import Wars.Battle.FieldConflict.FieldConflict;
import Wars.Building.Castle.Castle;
import Wars.WarStates.WarState;
import library.Hexagon;
import states.Hexagons.AncapHexagon;

public class WarHexagon extends AncapHexagon {

    private WarHexagonStatus status;
    private FieldConflict fieldConflict;
    private Castle castle;

    public WarHexagon(Hexagon hexagon) {
        super(hexagon);
    }

    public WarHexagonStatus getStatus() {
        return this.status;
    }

    public void setStatus(WarHexagonStatus status) {
        this.status = status;
    }

    public void startFieldConflict(WarState attacker) {
        FieldConflict conflict = new FieldConflict(this, attacker);
        this.setFieldConflict(conflict);
    }

    private void setFieldConflict(FieldConflict fieldConflict) {
        this.fieldConflict = fieldConflict;
    }

    public FieldConflict getFieldConflict() {
        return this.fieldConflict;
    }

    public void setCastle(Castle castle) {
        this.castle = castle;
    }

    public Castle getCastle() {
        return this.castle;
    }
}
