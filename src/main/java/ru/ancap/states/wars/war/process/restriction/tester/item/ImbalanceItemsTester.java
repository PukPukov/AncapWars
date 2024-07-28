package ru.ancap.states.wars.war.process.restriction.tester.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ImbalanceItemsTester implements ItemStackPredicate {

    private final List<Material> banned = List.of(
        Material.END_CRYSTAL,
        Material.RESPAWN_ANCHOR,
        Material.TNT_MINECART
    );

    @Override
    public boolean test(ItemStack itemStack) {
        return !this.banned.contains(itemStack.getType());
    }

}