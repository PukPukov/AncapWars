package Wars.Listeners.TimerListeners;

import AncapLibrary.Library.AncapLibrary;
import AncapLibrary.Timer.Heartbeat.AncapHeartbeatEvent;
import AncapLibrary.Timer.TimerEvents.FastTimerTenSecondEvent;
import Wars.AncapWars.AncapWars;
import Wars.Events.WarriorHeartbeatEvent;
import Wars.WarPlayers.AncapWarrior;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WarHeartbeatListener implements Listener {

    @EventHandler
    public void onHeartbeat(AncapHeartbeatEvent e) {
        this.callHeartbeatEvents();
    }

    private void callHeartbeatEvents() {
        AncapWarrior[] warriors = AncapWars.getOnlinePlayers();
        for (AncapWarrior warrior : warriors) {
            WarriorHeartbeatEvent event = new WarriorHeartbeatEvent(warrior);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    @EventHandler
    public void onTenSeconds(FastTimerTenSecondEvent e) {
        AncapLibrary.getConfiguredDatabase().save();
    }
}
