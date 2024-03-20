package ru.ancap.states.wars.api.field;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.ancap.commons.aware.Aware;
import ru.ancap.commons.aware.ContextAware;
import ru.ancap.commons.aware.InsecureContextHandle;
import ru.ancap.commons.debug.AncapDebug;
import ru.ancap.commons.map.GuaranteedMap;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.player.Warrior;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.relations.RelateStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@AllArgsConstructor
public class FieldConflicts {
    
    private final long hexagonMaxHealth;
    private final Map<Long, FieldConflict> conflictMap = new HashMap<>();
    private final Map<WarState, Set<FieldConflict>> attackCache  = new GuaranteedMap<>(ConcurrentHashMap::newKeySet); 
    private final Map<WarState, Set<FieldConflict>> defenceCache = new GuaranteedMap<>(ConcurrentHashMap::newKeySet);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    @ContextAware(awareOf = Aware.THREAD, handle = InsecureContextHandle.NO_HANDLE)
    public void acceptExistence(Player player,
                                long code,
                                TriConsumer<WarState, WarState, WarHexagon> onOccupy,
                                BiConsumer<WarHexagon, WarState> onHit) {
        WarHexagon hexagon = new WarHexagon(code);
        WarState defender = hexagon.getOwner();
        WarState attacker = Warrior.get(player).state();
            /* две дорогостоящие и *неоптимизированные* операции, если будет лагать можно 
               заменить на ручное объявление атаки, оптимизировать или добавить кэши, но лучше просто 
               отказаться от лагучей конфиг-бд */
        RelateStatus status = this.getRelations(player, defender);
        if (this.mayAttack(attacker, status, code)) this.startAttack(new FieldConflict(hexagon, attacker, defender, this.hexagonMaxHealth));
        if (this.atFieldConflict(code)) {
            switch (status) {
                case ALLY -> {
                    this.lock.readLock().lock();
                    FieldConflict conflict = this.conflictMap.get(code);
                    this.lock.readLock().unlock();
                    conflict.heal();
                    if (conflict.health() > conflict.maxHealth()) this.removeConflict(code);
                }
                case ENEMY -> {
                    this.hit(code, player, onHit);
                    this.lock.readLock().lock();
                    FieldConflict conflict = this.conflictMap.get(code);
                    this.lock.readLock().unlock();
                    if (conflict.health() <= 0) {
                        AncapDebug.debug("HEALTH LOW");
                        this.occupy(hexagon.getOwner(), attacker, hexagon, onOccupy);
                    }
                }
            }
        }
    }
    
    @ContextAware(awareOf = Aware.THREAD, handle = InsecureContextHandle.NO_HANDLE)
    private void hit(long code, Player player, BiConsumer<WarHexagon, WarState> onHit) {
        this.conflictMap.get(code).hit();
        onHit.accept(new WarHexagon(code), Warrior.get(player).state());
    }
    
    public Iterable<FieldConflict> all() {
        this.lock.readLock().lock();
        List<FieldConflict> list = new ArrayList<>(50);
        list.addAll(this.conflictMap.values());
        this.lock.readLock().unlock();
        return list;
    }
    
    public Set<WarHexagon> getFieldConflicts() {
        this.lock.readLock().lock();
        try     { return this.conflictMap.keySet().stream()
                      .map(WarHexagon::new)
                      .collect(Collectors.toSet());         } 
        finally { this.lock.readLock().unlock();            }
    }
    
    public boolean atFieldConflict(long code) {
        this.lock.readLock().lock();
        try     { return this.conflictMap.containsKey(code); }
        finally { this.lock.readLock().unlock();             }
    }
    
    public double occupyLevel(long code) {
        this.lock.readLock().lock();
        try     { FieldConflict conflict = this.conflictMap.get(code);
                  if (conflict == null) throw new IllegalStateException();
                  return 1D-((double) conflict.health()) / ((double) this.hexagonMaxHealth); }
        finally { this.lock.readLock().unlock();                                             }
    }
    
    private void startAttack(FieldConflict conflict) {
        this.lock.writeLock().lock();
        this.conflictMap.put(conflict.hexagon().code(), conflict);
        this.attackCache.get(conflict.attacker()).add(conflict);
        this.defenceCache.get(conflict.defender()).add(conflict);
        this.lock.writeLock().unlock();
    }
    
    public void removeConflict(long code) {
        this.lock.writeLock().lock();
        FieldConflict conflict = this.conflictMap.get(code);
        this.conflictMap.remove(code);
        this.attackCache.get(conflict.attacker()).remove(conflict);
        this.defenceCache.get(conflict.defender()).remove(conflict);
        this.lock.writeLock().unlock();
    }
    
    private void occupy(WarState loser, WarState occupier, WarHexagon hexagon, TriConsumer<WarState, WarState, WarHexagon> onOccupy) {
        AncapDebug.debug("OCCUPY (METHOD) CALLED", loser, occupier, hexagon, onOccupy);
        this.removeConflict(hexagon.code());
        onOccupy.accept(loser, occupier, hexagon);
        AncapDebug.debug("OCCUPY (METHOD) ENDED");
    }
    
    public void occupy(long code) {
        if (this.atFieldConflict(code)) throw new IllegalStateException();
        this.lock.readLock().lock();
        FieldConflict conflict = this.conflictMap.get(code);
        this.lock.readLock().unlock();
        this.occupy(conflict.defender(), conflict.attacker(), conflict.hexagon(), AncapWars.warListener.onOccupy);
    }
    
    private RelateStatus getRelations(Player player, WarState stateAPI) {
        Warrior api = Warrior.get(player);
        if (api.isFree()) return RelateStatus.ALLY;
        if (stateAPI == null) return RelateStatus.ALLY;
        if (!api.state().atWarWith(stateAPI)) return RelateStatus.ALLY;
        return RelateStatus.ENEMY;
    }
    
    private boolean mayAttack(@NotNull WarState attacker, RelateStatus status, long code) {
        if (this.atFieldConflict(code)) return false;
        if (status != RelateStatus.ENEMY) return false;
        WarHexagon hexagonAPI = new WarHexagon(code);
        if (!hexagonAPI.canBeAttackedBy(attacker)) return false;
        return true;
    }
    
    public void makeIncorporation(WarState inner, WarState outer) {
        this.lock.readLock().lock();
        this.makeIncorporation(inner, outer, this.attackCache);
        this.makeIncorporation(inner, outer, this.defenceCache);
        this.lock.readLock().unlock();
    }

    private void makeIncorporation(WarState inner, WarState outer, Map<WarState, Set<FieldConflict>> cache) {
        Set<FieldConflict> toAdd = cache.get(inner);
        Set<FieldConflict> addTo = cache.get(outer);
        cache.remove(inner);
        addTo.addAll(toAdd);
    }

    public Set<FieldConflict> attacksTo(@NonNull WarState warState) {
        this.lock.readLock().lock();
        try     { return this.defenceCache.get(warState); }
        finally { this.lock.readLock().unlock();          }
    }
    
    public void setPeace(long code) {
        if (this.atFieldConflict(code)) this.removeConflict(code);
    }
    
}
