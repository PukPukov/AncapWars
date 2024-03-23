package ru.ancap.states.wars.plugin.executor;

import org.bukkit.command.CommandSender;
import ru.ancap.commons.debug.AncapDebug;
import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.command.api.commands.operator.delegate.Delegate;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.Raw;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.SubCommand;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.rule.delegate.StringDelegatePattern;
import ru.ancap.framework.communicate.message.CallableMessage;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.states.wars.AncapWars;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class CommandToggler extends CommandTarget {
    
    public CommandToggler(CallableMessage valueName, Function<CommandSender, Boolean> statusProvider, BiConsumer<CommandSender, Boolean> toggleSetupper) {
        super(new Delegate(
            new Raw(dispatch -> dispatch.source().communicator().message(new LAPIMessage(
                AncapWars.class, "toggler.represent",
                new Placeholder("value", valueName),
                new Placeholder("status", statusProvider.apply(dispatch.source().sender()))
            ))),
            new SubCommand(
                new StringDelegatePattern("toggle"),
                dispatch -> {
                    boolean current = statusProvider.apply(dispatch.source().sender());
                    AncapDebug.debug("current", current);
                    toggleSetupper.accept(dispatch.source().sender(), !current);
                    dispatch.source().communicator().message(new LAPIMessage(
                        AncapWars.class, "toggler.toggled",
                        new Placeholder("value", valueName),
                        new Placeholder("status", !current)
                    ));
                }
            )
        ));
    }
    
}