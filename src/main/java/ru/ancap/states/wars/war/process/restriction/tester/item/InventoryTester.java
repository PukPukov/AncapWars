package ru.ancap.states.wars.war.process.restriction.tester.item;

import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class InventoryTester implements ItemStackPredicate {

    private final List<ItemStackPredicate> directTesters;

    public InventoryTester(ItemStackPredicate... predicates) {
        this(List.of(predicates));
    }

    @Override
    public boolean test(ItemStack itemStack) {
        for (ItemStackPredicate predicate : directTesters) {
            if (itemStack == null) return true;
            if (!predicate.test(itemStack)) return false;
        }
        return true;
    }

}
