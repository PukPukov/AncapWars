package Wars.Listeners.AncapListeners;

import AncapLibrary.AncapEvents.AncapWorldInteractEvent;
import AncapLibrary.Player.AncapPlayer;
import Wars.Events.WarWorldInteractEvent;
import Wars.Location.WarLocation;
import Wars.WarPlayers.AncapWarrior;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class AncapEventsListener implements Listener {

    @EventHandler
    public void onWorldInteract(AncapWorldInteractEvent e) {
        this.callSubEvents(e);
    }

    private void callSubEvents(AncapWorldInteractEvent e) {
        this.callWarWorldInteractEvent(e);
    }

    private void callWarWorldInteractEvent(AncapWorldInteractEvent e) {
        AncapPlayer player = e.getPlayer();
        AncapWarrior warrior = new AncapWarrior(player.getName());
        WarLocation location = warrior.getWarLocation();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(new WarWorldInteractEvent(e, warrior, location));
    }
}
