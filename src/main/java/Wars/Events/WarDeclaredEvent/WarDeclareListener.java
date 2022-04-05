package Wars.Events.WarDeclaredEvent;

import Wars.AncapWars.AncapWars;
import Wars.WarStates.WarState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import states.Main.AncapStates;

public class WarDeclareListener implements Listener {

    @EventHandler
    public void onWarDeclare(WarDeclaredEvent e) {
        WarState attacker = e.getAttacker();
        WarState attacked = e.getAttacked();
        AncapStates.sendMessage(AncapWars.getConfiguration().getWarDeclareMessage(attacker.getName(), attacked.getName()));
        AncapWars.sendSound(AncapWars.getConfiguration().getWarDeclareSound());
        attacker.addEnemy(attacked);
        attacked.addEnemy(attacker);
    }
}
