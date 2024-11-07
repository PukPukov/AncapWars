package ru.ancap.states.wars.integration;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import ru.ancap.framework.identifier.Identifier;
import ru.ancap.framework.language.LAPI;
import ru.ancap.states.wars.messaging.Message;

public class BedrockIntegration {

    public static boolean bcheck(Player player) {
        if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            player.kick(Component.text(LAPI.localized(Message.Minecraft.Error.War.BEDROCK, Identifier.of(player))));
            return false;
        }
        return true;
    }

}