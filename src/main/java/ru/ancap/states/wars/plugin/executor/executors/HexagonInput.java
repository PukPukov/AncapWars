package ru.ancap.states.wars.plugin.executor.executors;

import org.bukkit.entity.Player;
import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.command.api.commands.operator.delegate.Delegate;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.SubCommand;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.rule.delegate.StringDelegatePattern;
import ru.ancap.framework.communicate.communicator.Communicator;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.misc.money.exception.NotEnoughMoneyException;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.hexagon.exception.NotDevastatedException;
import ru.ancap.states.wars.api.player.Warrior;

public class HexagonInput extends CommandTarget {
    
    public HexagonInput() {
        super(new Delegate(
            new SubCommand(
                new StringDelegatePattern("rebuild"),
                /*new Handle<>(WarsCommandCore.INSTANCE).simple(dispatch -> dispatch.source().warrior().rebuildHexagon())
                                                   // OR
                                                   .of(new ArgumentsMethod<>() {
                                                       public void run(
                                                           ArgumentDispatch<WarsCommandCore> dispatch,
                                                           @LineArgument String arg,
                                                           @LineArgument(extractor = MyExtractor.class) MyObject myObject,
                                                           @LineArgument(insertHandle = ) MyOtherObject otherObject,
                                                           @Question(new LAPIMessage(AncapWars.class, "example-question")) ObjectFromAnswer object
                                                       ) throws Exception {
                                                           dispatch.source().warrior().rebuildCastle();
                                                       }
                                                   } )
                    .andSpecific(new TypeExceptionHandle(
                        id -> dispatch.source().communicator().message(new LAPIMessage(AncapWars.class, id)),
                        new TypeExceptionHandle.Types(
                            new TypeExceptionHandle.Type(NotEnoughMoneyException.class, "messages.minecraft.errors.command.not-enough-money"),
                            new TypeExceptionHandle.Type(NotDevastatedException.class, "hexagon.not-devastated"))))
                    .andSpecific( something ),*/
                dispatch -> {
                    Player player = (Player) dispatch.source().sender(); 
                    Warrior warrior = Warrior.get(player);
                    try {
                        warrior.rebuildHexagon();
                    } catch (NotEnoughMoneyException e) {
                        Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.command.not-enough-money"));
                    } catch (NotDevastatedException e) {
                        Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "hexagon.not-devastated"));
                    }
                }
            )
        ));
    }
    
}