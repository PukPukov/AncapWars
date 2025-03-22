package ru.ancap.states.wars.war.process.restriction.safer.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ItemStackSafer {
    
    @Nullable ItemStack safe(ItemStack itemStack);

}