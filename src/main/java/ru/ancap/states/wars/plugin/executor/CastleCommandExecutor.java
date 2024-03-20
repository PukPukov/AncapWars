package ru.ancap.states.wars.plugin.executor;

import org.bukkit.entity.Player;
import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.command.api.commands.operator.arguments.Accept;
import ru.ancap.framework.command.api.commands.operator.arguments.Argument;
import ru.ancap.framework.command.api.commands.operator.arguments.Arguments;
import ru.ancap.framework.command.api.commands.operator.arguments.extractor.basic.Self;
import ru.ancap.framework.command.api.commands.operator.delegate.Delegate;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.SubCommand;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.rule.delegate.StringDelegatePattern;
import ru.ancap.framework.communicate.communicator.Communicator;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.misc.money.exception.NotEnoughMoneyException;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.castle.exception.CastleNameOccupiedException;
import ru.ancap.states.wars.api.castle.exception.HexagonAlreadyHaveCastleException;
import ru.ancap.states.wars.api.player.Warrior;
import ru.ancap.states.wars.api.state.exception.DevastatedException;
import ru.ancap.states.wars.plugin.executor.exception.*;

import javax.naming.InvalidNameException;
import javax.naming.NoPermissionException;

public class CastleCommandExecutor extends CommandTarget {
    
    public CastleCommandExecutor() {
        super(new Delegate(
            new SubCommand(
                new StringDelegatePattern("found", "new", "create"),
                new Arguments(
                    new Accept(
                        new Argument("name", new Self())
                    ),
                    dispatch -> {
                        try {
                            Player player = (Player) dispatch.source().sender(); 
                            Warrior.get(player).buildCastle(dispatch.arguments().get("name", String.class));
                        } catch (NoPermissionException e) {
                            Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.permission.lack"));
                        } catch (NotEnoughMoneyException e) {
                            Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.state.not-enough-money"));
                        } catch (CastleNameOccupiedException e) {
                            Communicator.of(dispatch.source().sender()).message(new LAPIMessage(
                                AncapWars.class, "messages.minecraft.errors.castle.name.occupied",
                                new Placeholder("name", e.getName())
                            ));
                        } catch (HexagonAlreadyHaveCastleException e) {
                            Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.castle.hexagon.already-built"));
                        } catch (InvalidNameException e) {
                            Communicator.of(dispatch.source().sender()).message(new LAPIMessage(
                                AncapWars.class, "messages.minecraft.errors.castle.name.invalid",
                                new Placeholder("name", dispatch.arguments().get("name", String.class))
                            ));
                        } catch (CantBuildInUnstableException exception) {
                            Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "castle.unstable-build"));
                        } catch (DevastatedException e) {
                            Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "hexagon.devastated"));
                        }
                    }
                )
            ),
            new SubCommand(
                new StringDelegatePattern("rename"),
                new Arguments(
                    new Accept(
                        new Argument("old", new Self()),
                        new Argument("new", new Self())
                    ),
                    dispatch -> {
                        try {
                            Player player = (Player) dispatch.source().sender(); 
                            Warrior.get(player).renameCastle(
                                dispatch.arguments().get("old", String.class),
                                dispatch.arguments().get("new", String.class)
                            );
                        } catch (NotFoundException e) {
                            Communicator.of(dispatch.source().sender()).message(new LAPIMessage(
                                AncapWars.class, "messages.minecraft.errors.castle.name.not-found",
                                new Placeholder("name", dispatch.arguments().get("name", String.class))
                            ));
                        } catch (NoPermissionException e) {
                            Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.permission.lack"));
                        } catch (CastleNameOccupiedException e) {
                            Communicator.of(dispatch.source().sender()).message(new LAPIMessage(
                                AncapWars.class, "messages.minecraft.errors.castle.name.occupied"
                            ));
                        } catch (InvalidNameException e) {
                            Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.castle.name.invalid"));
                        }
                    }
                )
            )
        ));
    }
    
}
