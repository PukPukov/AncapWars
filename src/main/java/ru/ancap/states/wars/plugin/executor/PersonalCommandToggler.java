package ru.ancap.states.wars.plugin.executor;

import org.bukkit.command.CommandSender;
import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.communicate.message.CallableMessage;
import ru.ancap.framework.database.nosql.PathDatabase;

import java.util.function.Function;

public class PersonalCommandToggler extends CommandTarget {
    
    public PersonalCommandToggler(CallableMessage valueName, String storageKey, boolean default_, Function<CommandSender, PathDatabase> storageProvider) {
        super(new CommandToggler(
            valueName,
            (sender) -> {
                var database = storageProvider.apply(sender);
                var boolean_ = database.readBoolean(storageKey);
                if (boolean_ == null) return default_;
                else return boolean_;
            },
            (sender, value) -> storageProvider.apply(sender).write(storageKey, value)
        ));
    }
    
}