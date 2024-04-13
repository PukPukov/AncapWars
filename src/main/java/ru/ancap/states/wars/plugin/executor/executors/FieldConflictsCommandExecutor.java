package ru.ancap.states.wars.plugin.executor.executors;

import org.bukkit.entity.Player;
import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.command.api.commands.operator.delegate.Delegate;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.SubCommand;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.rule.delegate.StringDelegatePattern;
import ru.ancap.framework.communicate.communicator.Communicator;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.field.FieldConflict;
import ru.ancap.states.wars.api.field.FieldConflicts;
import ru.ancap.states.wars.api.player.Warrior;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.plugin.executor.util.HexagonCoordinates;
import ru.ancap.states.wars.plugin.executor.util.Indicator;
import ru.ancap.states.wars.plugin.executor.PersonalCommandToggler;
import ru.ancap.states.wars.plugin.executor.util.ProgressBar;
import ru.ancap.states.wars.plugin.listener.ChatBook;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

public class FieldConflictsCommandExecutor extends CommandTarget {

    public FieldConflictsCommandExecutor(FieldConflicts fieldConflicts) {
        super(new Delegate(
            new SubCommand(
                new StringDelegatePattern("status"),
                dispatch -> {
                    Communicator communicator = Communicator.of(dispatch.source().sender());
                    Player player = (Player) dispatch.source().sender();
                    Warrior warrior = Warrior.get(player);
                    WarState state = warrior.state();
                    if (state == null) {
                        Communicator.of(player).message(new LAPIMessage(AncapWars.class, "state.you-are-free"));
                        return;
                    }
                    Set<FieldConflict> attacks = fieldConflicts.attacksTo(warrior.state());
                    if (attacks.size() == 0) communicator.message(new LAPIMessage(AncapWars.class, "messages.minecraft.attention.field.attack.status.no-attack"));
                    else                     communicator.message(new LAPIMessage(
                        AncapWars.class, "messages.minecraft.attention.field.attack.status.some-under-attack",
                        new Placeholder("attacked list", new ChatBook<>(
                            new ArrayList<>(attacks),
                            (attack) -> new LAPIMessage(
                                AncapWars.class, "messages.minecraft.attention.field.attack.status.attacked-format",
                                new Placeholder("attacker", attack.attacker().getName()),
                                new Placeholder("attacked hexagon coordinates", new HexagonCoordinates(attack.hexagon().getHexagon())),
                                new Placeholder("progress bar", new ProgressBar(
                                    attack.conqueredPart(),
                                    10,
                                    "\uD83D\uDDE1", //🗡
                                    "<red>%s</red>",
                                    "<dark_gray>%s</dark_gray>"
                                )),
                                new Placeholder("currently under attack", new Indicator("<red>[!]</red>", attack.currentlyUnderAttack()))
                            ),
                            ((Comparator<FieldConflict>) (conflict_1, conflict_2) -> {
                                int result = Boolean.compare(conflict_1.currentlyUnderAttack(), conflict_2.currentlyUnderAttack());
                                if (result == 0) result = Long.compare(conflict_1.conquered(), conflict_2.conquered());
                                return result;
                            }).reversed()
                        ))
                    ));
                }
            ),
            new SubCommand(
                new StringDelegatePattern("notifications"),
                new PersonalCommandToggler(
                    new LAPIMessage(AncapWars.class, "field-conflicts.toggler.notify-status"),
                    "notifyAboutFieldConflicts",
                    true,
                    sender -> Warrior.get((Player) sender).database()
                )
            )
        ));
    }
}