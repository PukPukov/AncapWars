package Wars.WarHexagons;

import AncapLibrary.Library.AncapObject;
import Wars.AncapWars.AncapWars;
import Wars.Battle.FieldConflict.FieldConflict;
import Wars.Building.Castle.Castle;
import Wars.Commands.WarCommand.Command.SubCommands.WarDeclareCommand.govno.BukkitTimer;
import Wars.Location.WarLocation;
import Wars.WarStates.WarState;
import Wars.WarStates.WarStateMap.WarStateMap;
import library.Hexagon;
import states.Hexagons.AncapHexagon;
import states.Main.AncapStates;

public class WarHexagon extends AncapHexagon implements AncapObject {

    public WarHexagon(Hexagon hexagon) {
        super(hexagon);
    }

    public WarHexagonStatus getStatus() {
        return WarHexagonStatus.getStatusForName(this.getMeta("status"));
    }

    public void setStatus(WarHexagonStatus status) {
        this.setMeta("status", status.name());
    }

    public void startFieldConflict(WarState attacker) {
        FieldConflict conflict = new FieldConflict(this, attacker);
        this.setFieldConflict(conflict);
    }

    private void setFieldConflict(FieldConflict fieldConflict) {
    }

    public FieldConflict getFieldConflict() {
        return null;
    }

    public void setCastle(Castle castle) {
    }

    public Castle getCastle() {
        return null;
    }

    @Override
    public void setMeta(String s, String s1) {
        AncapStates.getMainDatabase().write("states.hexagon."+this.toString()+"."+s, s1);
    }

    @Override
    public String getMeta(String s) {
        return AncapStates.getMainDatabase().getString("states.hexagon."+this.toString()+"."+s);
    }

    public void startKostilBattle(WarLocation loc) {
        this.setStatus(WarHexagonStatus.KOSTIL_PREBATTLE);
        new BukkitTimer(this, loc) {
            @Override
            public void run() {
                this.getHexagon().setStatus(WarHexagonStatus.SIEGE);
            }
        }.runTaskLater(AncapWars.getInstance(), 15*60*20);
        new BukkitTimer(this, loc) {
            @Override
            public void run() {
                this.getHexagon().spawnCore(loc);
            }
        }.runTaskLater(AncapWars.getInstance(), 45*60*20);
        new BukkitTimer(this, loc) {
            @Override
            public void run() {
                if (this.getHexagon().getStatus() == WarHexagonStatus.SIEGE) {
                    this.getHexagon().setStatus(WarHexagonStatus.PEACE);
                    AncapStates.sendMessage(AncapWars.getConfiguration().getAttackRepulsedMessage());
                    AncapWars.sendSound(AncapWars.getConfiguration().getAttackRepulsedSound());
                }
            }
        }.runTaskLater(AncapWars.getInstance(), 105*60*20);
    }

    public void spawnCore(WarLocation location) {
        WarStateMap map = AncapWars.getWarStateMap();
        map.createCore(location);
    }
}
