package ru.ancap.states.wars.war.process.restriction.safer.item;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RequiredArgsConstructor
@Accessors(fluent = true) @Getter
@ToString @EqualsAndHashCode
public class EnchantmentSafer implements ItemStackSafer {

    private final Map<Enchantment, Integer> allowedEnchantments;
    
    @Override
    public ItemStack safe(ItemStack itemStack) {
        Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
        for (var entry : enchantments.entrySet()) {
            @Nullable Integer allowState = this.allowedEnchantments.get(entry.getKey());
            if (allowState == null) {
                itemStack.removeEnchantment(entry.getKey());
            }
            else if (allowState < entry.getValue()) {
                itemStack.removeEnchantment(entry.getKey());
                itemStack.addEnchantment(entry.getKey(), allowState);
            }
        }
        return itemStack;
    }

}