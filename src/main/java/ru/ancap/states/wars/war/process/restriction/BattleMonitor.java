package ru.ancap.states.wars.war.process.restriction;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.ancap.states.wars.war.process.restriction.safer.item.EnchantmentSafer;
import ru.ancap.states.wars.war.process.restriction.safer.item.ItemBanner;
import ru.ancap.states.wars.war.process.restriction.safer.item.ItemStackSafer;
import ru.ancap.states.wars.war.process.restriction.safer.potion.PotionAllowance;
import ru.ancap.states.wars.war.process.restriction.safer.potion.PotionEffectTester;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class BattleMonitor implements Consumer<Player> {

    private final Plugin plugin;

    private final List<ItemStackSafer> safers = List.of(
        new EnchantmentSafer(Map.of(
            Enchantment.RIPTIDE, 3,
            Enchantment.CHANNELING, 1
        )),
        new ItemBanner()//,
        //new PotionTester(PotionEffectType.INSTANT_HEALTH)
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
            this.safers,
            //this.effectsTester,
            (__) -> true,
            player
        ));
    }
    
}