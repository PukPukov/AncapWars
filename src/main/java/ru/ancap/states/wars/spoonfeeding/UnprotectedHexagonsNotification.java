package ru.ancap.states.wars.spoonfeeding;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.ancap.framework.communicate.communicator.Communicator;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.player.Warrior;

public class UnprotectedHexagonsNotification implements Listener {
    
    @EventHandler
    public void on(PlayerJoinEvent event) {
        Warrior warrior = Warrior.get(event.getPlayer());
        var communicator = Communicator.of(event.getPlayer());
        var state = warrior.state();
        var unprotected = state.unprotectedBorderHexagons();
        if (!unprotected.isEmpty()) Bukkit.getScheduler().runTaskLater(AncapWars.loaded(), () -> {
            if (warrior.ndatabase().readBoolean("notifyAboutBarrierLack").orElse(true)) communicator.message(new LAPIMessage(
                AncapWars.class, "barrier.lack.notification.text",
                new Placeholder("amount", unprotected.size())
            ));
        }, 30 * 20);
    }
    
}