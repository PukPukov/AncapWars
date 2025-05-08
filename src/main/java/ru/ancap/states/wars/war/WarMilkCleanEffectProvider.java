package ru.ancap.states.wars.war;

import com.etsuni.milksplash.cleaneffectprovider.MilkCleanEffectProvider;
import com.etsuni.milksplash.cleaneffectprovider.TweakedMilkCleanEffectProvider;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import ru.ancap.states.hexagons.Codeficator;
import ru.ancap.states.wars.AncapWars;

import java.util.Set;

public class WarMilkCleanEffectProvider extends MilkCleanEffectProvider {
    
    private final MilkCleanEffectProvider default_ = new TweakedMilkCleanEffectProvider(false, true);
    private final MilkCleanEffectProvider battle = new TweakedMilkCleanEffectProvider(false, false);
    
    @Override
    public Set<PotionEffectType> shouldBeCleanedFor(LivingEntity thrower, LivingEntity receiver) {
        if (AncapWars.level1BattleGameplayModificationIn(AncapWars.assaultStatus(Codeficator.hexagon(receiver.getLocation())))) {
            return this.battle.shouldBeCleanedFor(thrower, receiver);
        } else return this.default_.shouldBeCleanedFor(thrower, receiver);
    }
    
}