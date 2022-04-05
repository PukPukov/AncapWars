package Wars.Events.CastleCoreDamageEvent;

import Wars.Building.Castle.CastleCore;
import Wars.Building.Castle.CoreDeadException;
import Wars.Events.CastleCoreBreakEvent.CastleCoreBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CastleCoreDamageListener implements Listener {

    @EventHandler
    public void onCoreDamage(CastleCoreDamageEvent e) {
        CastleCore core = e.getCore();
        try {
            core.damage();
            core.place();
        } catch (CoreDeadException ex) {
            CastleCoreBreakEvent event = new CastleCoreBreakEvent(core);
            event.call();
        }
    }
}
