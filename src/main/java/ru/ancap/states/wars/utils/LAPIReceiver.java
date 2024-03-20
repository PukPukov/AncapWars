package ru.ancap.states.wars.utils;

import org.bukkit.command.CommandSender;
import ru.ancap.framework.communicate.communicator.Communicator;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.language.additional.LAPIMessage;

public class LAPIReceiver {

    public static void send(String id, CommandSender sender) {
        Communicator.of(sender).message(new LAPIMessage(id));
    }

    public static void send(String id, CommandSender sender, String placeholder, String replace) {
        Communicator.of(sender).message(new LAPIMessage(
            id,
            new Placeholder(placeholder, replace)
        ));
    }

    public static void send(String id, CommandSender sender, String placeholder, String replace, String placeholder1, String replace1) {
        Communicator.of(sender).message(new LAPIMessage(
            id,
            new Placeholder(placeholder, replace),
            new Placeholder(placeholder1, replace1)
        ));
    }

    public static void send(String id, CommandSender sender, String placeholder, String replace, String placeholder1, String replace1, String placeholder2, String replace2) {
        Communicator.of(sender).message(new LAPIMessage(
            id,
            new Placeholder(placeholder, replace),
            new Placeholder(placeholder1, replace1),
            new Placeholder(placeholder2, replace2)
        ));
    }

}
