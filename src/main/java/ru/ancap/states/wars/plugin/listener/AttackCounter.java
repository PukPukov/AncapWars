package ru.ancap.states.wars.plugin.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.ancap.commons.Cutoffs;
import ru.ancap.commons.Pair;
import ru.ancap.commons.exception.UnsafeThread;
import ru.ancap.commons.map.MapGC;
import ru.ancap.commons.map.SafeMap;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.identifier.Identifier;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.framework.plugin.api.Ancap;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.player.Warrior;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.plugin.executor.util.HexagonCoordinates;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AttackCounter {
    
    private final Map<String, Cutoffs<Pair<WarState, WarHexagon>>> attackHistory;
    
    public AttackCounter(Ancap ancap) {
        this(SafeMap.<String, Cutoffs<Pair<WarState, WarHexagon>>>builder(new ConcurrentHashMap<>())
            .guaranteed(() -> new Cutoffs<>(10))
            .collectGarbage(ancap.playerLeaveInstructor()
                .map(Player::getName)
                .as(MapGC::new)
            ).build()
        );
    }
    
    public List<Pair<WarState, WarHexagon>> lastAttacksOf(Player player) {
        return this.attackHistory.get(Identifier.of(player)).from(System.currentTimeMillis() - 5000).stream()
            .collect(Collectors.toMap(Pair::getValue, Function.identity(), (f, s) -> f))
            .values().stream().toList();
    }
    
    public void notifyAboutAttack(Player player, WarState attacker, WarHexagon hexagon) {
        var nafc = Warrior.get(player).database().readBoolean("notifyAboutFieldConflicts");
        if (nafc != null && !nafc) return;
        this.attackHistory.get(Identifier.of(player)).mark(new Pair<>(attacker, hexagon));
    }
    
    public AttackCounter start() {
        UnsafeThread.start(() -> {
            while (true) {
                this.attackHistory.forEach((name, attacks) -> {
                    Player player = Bukkit.getPlayer(name);
                    if (player == null) this.attackHistory.remove(name);
                    var lastAttacks = this.lastAttacksOf(player);
                    this.displayAttacks(player, lastAttacks);
                });
                Thread.sleep(1000);
            }
        });
        return this;
    }

    private void displayAttacks(Player target, List<Pair<WarState, WarHexagon>> lastAttacks) {
        ActionBarCommunicator communicator = new ActionBarCommunicator(target);
        if      (lastAttacks.size() == 0) return;
        else if (lastAttacks.size() == 1) communicator.send(new LAPIMessage(
            AncapWars.class, "messages.minecraft.attention.field.attack.single",
            new Placeholder("coordinates", new HexagonCoordinates(lastAttacks.get(0).getValue().getHexagon())),
            new Placeholder("state",       lastAttacks.get(0).getKey().getName())
        ));
        else communicator.send(new LAPIMessage(AncapWars.class, "messages.minecraft.attention.field.attack.multiple"));
    }

}