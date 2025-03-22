package ru.ancap.states.wars.war.process.restriction.safer.potion;

import lombok.AllArgsConstructor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.function.Predicate;

@AllArgsConstructor
public class PotionEffectTester implements Predicate<PotionEffect> {

    private final Map<PotionEffectType, PotionAllowance> allowances;

    @Override
    public boolean test(PotionEffect potionEffect) {
        if (this.fullyBanned(potionEffect.getType())) return false;
        if (this.allowedLimitOverflowed(
                potionEffect.getType(),
                potionEffect.getDuration(),
                potionEffect.getAmplifier()
        )) return false;
        return true;
    }

    private boolean allowedLimitOverflowed(PotionEffectType type, int potionDuration, int potionAmplifier) {
        PotionAllowance allowance = allowances.get(type);
        return allowance.maxAmplifier() < potionAmplifier ||
                allowance.maxDuration() < potionDuration;
    }

    private boolean fullyBanned(PotionEffectType type) {
        return !allowances.containsKey(type);
    }
}