package ru.ancap.states.wars.plugin.executor.executors;

import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.command.api.commands.operator.delegate.Delegate;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.SubCommand;
import ru.ancap.framework.plugin.api.language.locale.loader.LocaleHandle;
import ru.ancap.framework.plugin.api.language.locale.loader.LocaleReloadInput;

public class AncapWarsInput extends CommandTarget {
    
    public AncapWarsInput(LocaleHandle localeHandle) {
        super(new Delegate(
            new SubCommand("reload", new Delegate(
                new SubCommand("locales", new LocaleReloadInput(localeHandle))
            ))
        ));
    }
    
}