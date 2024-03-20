package ru.ancap.states.wars.war.process.restriction.tester.item;

import org.bukkit.inventory.ItemStack;

public class EnchantmentTester implements ItemStackPredicate {

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack.getEnchantments().isEmpty();
    }

}
