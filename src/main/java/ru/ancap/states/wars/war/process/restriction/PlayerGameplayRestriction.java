package ru.ancap.states.wars.war.process.restriction;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.ancap.states.wars.war.process.restriction.tester.item.ItemStackPredicate;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@AllArgsConstructor
public class PlayerGameplayRestriction implements Runnable {

    private final ItemStackPredicate itemStackTester;
    private final Predicate<PotionEffect> potionEffectTester;

    private final Player restricted;

    /**
     * Оставляет у игрока только те предметы и эффекты, которые были допущены тестерами
     */
    @Override
    public void run() {
        this.restricted.getInventory().setStorageContents(Arrays.stream(this.restricted.getInventory().getStorageContents())
            .filter(this.itemStackTester)
            .toList().toArray(new ItemStack[0]));
        List<PotionEffectType> illegal = this.restricted.getActivePotionEffects().stream()
            .filter(this.potionEffectTester.negate())
            .map(PotionEffect::getType).toList();
        illegal.forEach(this.restricted :: removePotionEffect);
    }
    
}