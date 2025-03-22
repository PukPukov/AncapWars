package ru.ancap.states.wars.war.process.restriction.safer.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemBanner implements ItemStackSafer {

    private final List<Material> banned = List.of(
        Material.END_CRYSTAL,
        Material.RESPAWN_ANCHOR,
        Material.TNT_MINECART,
        Material.COBWEB,
        Material.LAVA_BUCKET,
        Material.NETHERITE_HELMET,
        Material.DIAMOND_HELMET,
        Material.IRON_HELMET,
        Material.CHAINMAIL_HELMET
    );

    @Override
    public ItemStack safe(ItemStack itemStack) {
        if (this.banned.contains(itemStack.getType())) return null;
        else return itemStack;
    }

}