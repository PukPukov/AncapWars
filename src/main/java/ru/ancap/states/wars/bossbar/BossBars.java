package ru.ancap.states.wars.bossbar;

import lombok.AllArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;
import ru.ancap.commons.map.GuaranteedMap;
import ru.ancap.framework.identifier.Identifier;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class BossBars {

    private static final Map<String, Map<Integer, BossBar>> barPool = new GuaranteedMap<>(HashMap::new);

    public static void send(Player target, int slot, BossBar bossBar) {
        hide(target, slot);
        target.showBossBar(bossBar);
        barPool.get(Identifier.of(target)).put(slot, bossBar);
    }

    public static void hide(Player target, int slot) {
        if (barPool.get(Identifier.of(target)).containsKey(slot)) {
            target.hideBossBar(barPool.get(Identifier.of(target)).get(slot));
            barPool.get(Identifier.of(target)).remove(slot);
        }
    }
    
    public static void hideAll(Player target) {
        for (Integer slot : barPool.get(Identifier.of(target)).keySet()) {
            target.hideBossBar(barPool.get(Identifier.of(target)).get(slot));
            barPool.get(Identifier.of(target)).remove(slot);
        }
    }

}
