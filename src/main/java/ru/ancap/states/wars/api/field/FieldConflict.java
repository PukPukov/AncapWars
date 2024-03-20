package ru.ancap.states.wars.api.field;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.plugin.executor.Part;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@EqualsAndHashCode(onlyExplicitlyIncluded = true) @ToString
public class FieldConflict {
    
    @EqualsAndHashCode.Include
    private final String id;
    private final WarHexagon hexagon;
    private final WarState attacker;
    private final WarState defender;
    private final long maxHealth;
    
    public FieldConflict(WarHexagon hexagon, WarState attacker, WarState defender, long maxHealth) {
        this.id = UUID.randomUUID().toString();
        this.hexagon = hexagon;
        this.attacker = attacker;
        this.defender = defender;
        this.maxHealth = maxHealth;
        this.health.set(maxHealth);
        this.lastAttackTime.set(0);
    }

    private final AtomicLong health         = new AtomicLong();
    private final AtomicLong lastAttackTime = new AtomicLong();
    
    public WarHexagon hexagon() { return this.hexagon; }
    public WarState attacker() { return this.attacker.warActor(); }
    public WarState defender() { return this.defender.warActor(); }
    public long maxHealth() { return this.maxHealth; }
    public long health() { return this.health.get(); }
    public void heal() { this.health.getAndIncrement(); }
    public void hit() {
        this.health.getAndDecrement();
        this.lastAttackTime.set(System.currentTimeMillis());
    }
    
    public boolean currentlyUnderAttack() {
        return this.lastAttackTime.get() >= System.currentTimeMillis() - 7500;
    }

    public long conquered() {
        return this.maxHealth - this.health.get();
    }

    public double conqueredPart() {
        return Part.of(this.conquered(), this.maxHealth());
    }
    
}
