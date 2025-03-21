package ru.ancap.states.wars.war.process.restriction;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.ancap.states.wars.war.process.restriction.tester.item.*;
import ru.ancap.states.wars.war.process.restriction.tester.potion.PotionAllowance;
import ru.ancap.states.wars.war.process.restriction.tester.potion.PotionEffectTester;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class BattleMonitor implements Consumer<Player> {

    private final Plugin plugin;

    private final ItemStackPredicate itemsTester = new InventoryTester(
        new EnchantmentTester(Map.of(
            Enchantment.RIPTIDE, 3,
            Enchantment.CHANNELING, 1
        )),
        new ImbalanceItemsTester(),
        new HelmetTester(),
        new PotionTester(PotionEffectType.INSTANT_HEALTH)
    );

    private final Predicate<PotionEffect> effectsTester = new PotionEffectTester(Map.of(
        PotionEffectType.ABSORPTION,        new PotionAllowance(2400, 3),
        PotionEffectType.REGENERATION,      new PotionAllowance(400,  1),
        PotionEffectType.FIRE_RESISTANCE,   new PotionAllowance(6000, 0),
        PotionEffectType.RESISTANCE,        new PotionAllowance(6000, 0)
    ));

    @Override
    public void accept(Player player) {
        Bukkit.getScheduler().runTask(this.plugin, new PlayerGameplayRestriction(
            this.itemsTester,
            this.effectsTester,
            player
        ));
    }
    
}