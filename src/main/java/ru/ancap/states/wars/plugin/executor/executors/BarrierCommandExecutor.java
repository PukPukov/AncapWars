package ru.ancap.states.wars.plugin.executor.executors;

import org.bukkit.entity.Player;
import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.command.api.commands.operator.communicate.ChatBook;
import ru.ancap.framework.command.api.commands.operator.delegate.Delegate;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.Raw;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.SubCommand;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.rule.delegate.StringDelegatePattern;
import ru.ancap.framework.communicate.communicator.Communicator;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.misc.money.exception.NotEnoughMoneyException;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.player.Warrior;
import ru.ancap.states.wars.plugin.executor.util.HexagonCoordinates;
import ru.ancap.states.wars.plugin.executor.exception.*;
import ru.ancap.states.wars.spoonfeeding.UnprotectedHexagonsNotificationToggler;

import javax.naming.NoPermissionException;

public class BarrierCommandExecutor extends CommandTarget {
    
    public BarrierCommandExecutor() {
        super(new Delegate(
            new SubCommand(
                new StringDelegatePattern("attack"),
                dispatch -> {
                    try {
                        Player player = (Player) dispatch.source().sender();
                        Warrior.get(player).attackBarrier();
                    } catch (NoPermissionException e) {
                        Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.permission.lack"));
                    } catch (NotFoundException e) {
                        Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.barrier.hexagon.no-barrier"));
                    } catch (HexagonIsProtectedException e) {
                        Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.barrier.hexagon.protected"));
                    } catch (PenaltyException exception) {
                        Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.barrier.penalty"));
                    } catch (InvalidTimeException e) {
                        Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.barrier.attack.time.invalid"));
                    } catch (NotInWarException e) {
                        Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.state.not-in-war"));
                    } catch (AttackAttackedHexagonException e) {
                        Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.barrier.attack.attacked"));
                    } catch (NotEnoughMoneyException exception) {
                        Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.state.not-enough-money"));
                    }
                }
            ),
            new SubCommand(
                new StringDelegatePattern("lack"),
                new Delegate(
                    new Raw(dispatch -> {
                        var warrior = Warrior.get((Player) dispatch.source().sender());
                        var communicator = Communicator.of(dispatch.source().sender());
                        if (warrior.isFree()) {
                            communicator.message(new LAPIMessage(AncapWars.class, "state.you-are-free"));
                            return;
                        }
                        var state = warrior.state();
                        var unprotected = state.unprotectedBorderHexagons();
                        if (!unprotected.isEmpty()) {
                            communicator.message(new LAPIMessage(
                                AncapWars.class, "barrier.lack.amount",
                                new Placeholder("amount", unprotected.size()),
                                new Placeholder("coordinates", new ChatBook<>(unprotected, hex -> new HexagonCoordinates(hex.getHexagon())))
                            ));
                        } else {
                            communicator.message(new LAPIMessage(AncapWars.class, "barrier.lack.zero"));
                        }
                    }),
                    new SubCommand(
                        new StringDelegatePattern("notifications"),
                        new UnprotectedHexagonsNotificationToggler()
                    )
                )
            )
        ));
    }
    
}