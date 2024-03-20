package ru.ancap.states.wars.war.process.restriction;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.ancap.states.wars.war.process.restriction.tester.item.*;
import ru.ancap.states.wars.war.process.restriction.tester.potion.PotionAllowance;
import ru.ancap.states.wars.war.process.restriction.tester.potion.PotionEffectTester;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WarMonitor implements Consumer<Player> {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ItemStackPredicate itemsTester = new InventoryTester(
            new EnchantmentTester(),
            new ImbalanceItemsTester(),
            new HelmetTester(),
            new PotionTester(PotionEffectType.HEAL)
    );

    private final Predicate<PotionEffect> effectsTester = new PotionEffectTester(Map.of(
        PotionEffectType.ABSORPTION,        new PotionAllowance(2400, 3),
        PotionEffectType.REGENERATION,      new PotionAllowance(400,  1),
        PotionEffectType.FIRE_RESISTANCE,   new PotionAllowance(6000, 0),
        PotionEffectType.DAMAGE_RESISTANCE, new PotionAllowance(6000, 0)
    ));

    @Override
    public void accept(Player player) {
        this.executor.execute(new PlayerGameplayRestriction(
            this.itemsTester,
            this.effectsTester,
            player
        ));
    }
    
}
