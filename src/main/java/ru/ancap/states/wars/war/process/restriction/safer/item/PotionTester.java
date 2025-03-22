package ru.ancap.states.wars.war.process.restriction.safer.item;

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
public class PotionTester implements ItemStackSafer {

    private final List<PotionEffectType> whitelist;

    public PotionTester(PotionEffectType... whitelist) {
        this(List.of(whitelist));
    }

    @Override
    public ItemStack safe(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return itemStack;
        if (!(itemMeta instanceof PotionMeta meta)) return itemStack;
        if (meta.hasCustomEffects()) return null;
        if (!meta.hasBasePotionType()) return itemStack;
        PotionEffectType effectType = meta.getBasePotionType().getEffectType();
        if (effectType == null || whitelist.contains(effectType)) return itemStack;
        else return null;
    }

}