package ru.ancap.states.wars.war.process.restriction.tester.item;

import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Запрещает все зелья, кроме зелий из вайтлиста и зелий без эффектов
 */
@AllArgsConstructor
public class PotionTester implements ItemStackPredicate {

    private final List<PotionEffectType> whitelist;

    public PotionTester(PotionEffectType... whitelist) {
        this(List.of(whitelist));
    }

    @Override
    public boolean test(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return true;
        if (!(itemMeta instanceof PotionMeta meta)) return true;
        if (!meta.getCustomEffects().isEmpty()) return false;
        PotionEffectType effectType = meta.getBasePotionData().getType().getEffectType();
        if (effectType == null || whitelist.contains(effectType)) return true;
        return false;
    }

}
