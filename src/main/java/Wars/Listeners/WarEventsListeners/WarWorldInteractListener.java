package Wars.Listeners.WarEventsListeners;

import Wars.AncapWars.AncapWars;
import Wars.Building.Castle.CastleCore;
import Wars.Events.CastleCoreDamageEvent.CastleCoreDamageEvent;
import Wars.Events.WarWorldInteractEvent;
import Wars.Location.WarLocation;
import Wars.WarHexagons.WarHexagonStatus;
import Wars.WarStates.WarStateMap.UnknownWarStateException;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WarWorldInteractListener implements Listener {

    @EventHandler
    public void onWorldInteract(WarWorldInteractEvent e) {
        this.callSubEvents(e);
    }

    private void callSubEvents(WarWorldInteractEvent e) {
        WarLocation location = e.getLocation();
        if (this.isCoreBreakEvent(e)) {
            CastleCore core = AncapWars.getWarStateMap().findCastleCore(location);
            CastleCoreDamageEvent event = new CastleCoreDamageEvent(core, e.getWarrior());
            event.call();
            return;
        }
    }

    private boolean isCoreBreakEvent(WarWorldInteractEvent e) {
        if (e.getLocation().getBlockAt().getType() == Material.OBSIDIAN) {
            return false;
        }
        if (e.getLocation().getHexagon().getStatus() != WarHexagonStatus.SIEGE) {
            return false;
        }
        try {
            AncapWars.getWarStateMap().findCastleCore(e.getLocation());
        } catch (UnknownWarStateException ex) {
            return false;
        }
        return true;
    }
}
