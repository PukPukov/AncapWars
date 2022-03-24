package Wars.Listeners.WarEventsListeners;

import Wars.Battle.FieldConflict.FieldConflict;
import Wars.Events.*;
import Wars.WarHexagons.WarHexagon;
import Wars.WarHexagons.WarHexagonStatus;
import Wars.WarPlayers.AncapWarrior;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.PluginManager;

public class WarEventsListener implements Listener {

    @EventHandler
    public void onWarriorHeartbeat(WarriorHeartbeatEvent e) {
        this.callSubEvent(e);
    }

    private void callSubEvent(WarriorHeartbeatEvent e) {
        AncapWarrior warrior = e.getWarrior();
        WarHexagon hexagon = warrior.getHexagon();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(new WarHexagonStayEvent(warrior, hexagon));
    }

    @EventHandler
    public void onHexagonStay(WarHexagonStayEvent e) {
        switch (e.getHexagon().getStatus()) {
            case PEACE -> this.onPeaceStay(e);
            case FIELD_CONFLICT -> this.onFieldConflictStay(e);
            case SIEGE_PREPARE -> this.onSiegePrepareStay(e);
            case SIEGE -> this.onSiegeStay(e);
        }
    }

    private void onPeaceStay(WarHexagonStayEvent e) {
        WarHexagon hexagon = e.getHexagon();
        AncapWarrior warrior = e.getWarrior();
        if (warrior.canAttack(hexagon)) {
            hexagon.startFieldConflict(warrior.getHighestWarState());
        }
    }

    private void onFieldConflictStay(WarHexagonStayEvent e) {
        WarHexagon hexagon = e.getHexagon();
        AncapWarrior warrior = e.getWarrior();
        FieldConflict conflict = hexagon.getFieldConflict();
        conflict.getHealth().pushBy(warrior);
    }

    private void onSiegePrepareStay(WarHexagonStayEvent e) {
        AncapWarrior warrior = e.getWarrior();
        WarHexagon hexagon = e.getHexagon();
        if (!warrior.canEnter(hexagon)) {
            warrior.pushOut();
        }
    }

    private void onSiegeStay(WarHexagonStayEvent e) {
        AncapWarrior warrior = e.getWarrior();
        WarHexagon hexagon = e.getHexagon();
        warrior.prepareToWar();
    }

    @EventHandler
    public void onWarHexagonInteract(WarWorldInteractEvent e) {
        switch (e.getLocation().getHexagon().getStatus()) {
            case PEACE -> this.onPeaceInteract(e);
            case FIELD_CONFLICT -> this.onFieldConflictInteract(e);
            case SIEGE_PREPARE -> this.onSiegePrepareInteract(e);
            case SIEGE -> this.onSiegeInteract(e);
        }
    }

    private void onPeaceInteract(WarWorldInteractEvent e) {
    }

    private void onFieldConflictInteract(WarWorldInteractEvent e) {
        e.setIntercepted(true);
    }

    private void onSiegePrepareInteract(WarWorldInteractEvent e) {
    }

    private void onSiegeInteract(WarWorldInteractEvent e) {
        e.setIntercepted(true);
        this.handleSiegeEvent(e);
    }

    private void handleSiegeEvent(WarWorldInteractEvent e) {
        if (this.isBlockPlaceEvent(e)) {
            this.limitBuildHeight(e);
        }
    }

    private boolean isBlockPlaceEvent(WarWorldInteractEvent e) {
        return e.getAncapEvent().getBukkitEvent() instanceof BlockPlaceEvent;
    }

    private void limitBuildHeight(WarWorldInteractEvent e) {
        if (this.isIllegalHeight(e)) {
            e.setCancelled(true);
        }
    }

    private boolean isIllegalHeight(WarWorldInteractEvent e) {
        return e.getLocation().getY() > 100;
    }

    @EventHandler
    public void onSiegeStart(CastleSiegeStartedEvent e) {
        e.getCastle().spawnCore();
        e.getCastle().getLocation().getHexagon().setStatus(WarHexagonStatus.SIEGE);
    }

    @EventHandler
    public void onCastleCoreBreak(CastleCoreBreakEvent e) {
        e.getCastle().getHealth().damage();
    }

}
