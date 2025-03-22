package ru.ancap.states.wars.war.process.restriction;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.ancap.states.wars.war.process.restriction.safer.item.ItemStackSafer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@AllArgsConstructor
public class PlayerGameplayRestriction implements Runnable {
    
    private final List<ItemStackSafer> safers;
    private final Predicate<PotionEffect> potionEffectTester;
    
    private final Player restricted;
    
    /**
     * Оставляет у игрока только те предметы и эффекты, которые были допущены тестерами
     */
    @Override
    public void run() {
        this.restricted.getInventory().setStorageContents(safe(this.restricted.getInventory().getStorageContents()));
        this.restricted.getInventory().setArmorContents(safe(this.restricted.getInventory().getArmorContents()));
        this.restricted.getInventory().setExtraContents(safe(this.restricted.getInventory().getExtraContents()));
        List<PotionEffectType> illegal = this.restricted.getActivePotionEffects().stream()
            .filter(this.potionEffectTester.negate())
            .map(PotionEffect::getType).toList();
        illegal.forEach(this.restricted :: removePotionEffect);
    }
    
    private ItemStack[] safe(ItemStack[] contents) {
        return Arrays.stream(contents)
            .map(itemStack -> {
                for (var safer : this.safers) {
                    if (itemStack == null) return null; // need to place it in loop so subsequent safers wouldnt crash after null return
                    itemStack = safer.safe(itemStack);
                }
                return itemStack;
            })
            .toArray(ItemStack[]::new);
    }
    
}