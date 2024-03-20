package ru.ancap.states.wars.plugin.listener;

import lombok.AllArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import ru.ancap.framework.communicate.message.CallableMessage;
import ru.ancap.framework.identifier.Identifier;

@AllArgsConstructor
public class ActionBarCommunicator {
    
    private final Player target;

    public void send(CallableMessage message) {
        this.target.sendActionBar(MiniMessage.miniMessage().deserialize(message.call(Identifier.of(this.target))));
    }
    
}
