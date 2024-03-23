package ru.ancap.states.wars.spoonfeeding;

import org.bukkit.entity.Player;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.player.Warrior;
import ru.ancap.states.wars.plugin.executor.PersonalCommandToggler;

public class UnprotectedHexagonsNotificationToggler extends PersonalCommandToggler {
    
    public UnprotectedHexagonsNotificationToggler() {
        super(
            new LAPIMessage(AncapWars.class, "barrier.lack.notification.toggler-value"),
            "notifyAboutBarrierLack",
            true,
            sender -> Warrior.get((Player) sender).database()
        );
    }
    
}