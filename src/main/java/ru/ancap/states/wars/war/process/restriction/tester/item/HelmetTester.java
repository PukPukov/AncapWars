package ru.ancap.states.wars.war.process.restriction.tester.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HelmetTester implements ItemStackPredicate {

    private final List<Material> banned = List.of(
            Material.NETHERITE_HELMET,
            Material.DIAMOND_HELMET,
            Material.IRON_HELMET,
            Material.CHAINMAIL_HELMET
    );

    @Override
    public boolean test(ItemStack itemStack) {
        return !banned.contains(itemStack.getType());
    }

}
