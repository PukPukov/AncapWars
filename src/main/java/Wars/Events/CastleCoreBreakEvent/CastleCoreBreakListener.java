package Wars.Events.CastleCoreBreakEvent;

import Wars.AncapWars.AncapWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import states.Main.AncapStates;

public class CastleCoreBreakListener implements Listener {

    //Говнокод, исправить
    @EventHandler
    public void onCoreBreak(CastleCoreBreakEvent e) {
        AncapStates.sendMessage(AncapWars.getConfiguration().getBattleLoseMessage());
        AncapWars.sendSound(AncapWars.getConfiguration().getCastleBreakSound());
    }
}
