package ru.ancap.states.wars.war.info;

import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.state.Barrier;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.api.war.War;
import ru.ancap.states.wars.assault.AttackWait;
import ru.ancap.states.wars.plugin.listener.AssaultRuntime;

import java.util.List;
import java.util.Set;

public interface AssaultExecutor {

    void acceptDeclareLoad(AttackWait attackWait);
    void acceptAssault(AttackWait attackWait, AssaultRuntime runtime, List<WarHexagon> toSet);
    boolean isActiveCastleHeart(Location location);
    void hitHeart(BlockBreakEvent event, Location location);

    void breakCastle(Barrier barrier, WarState warState, AssaultRuntime runtime);

    @NotNull AssaultRuntime assault(long code);
    void remove(AssaultRuntime runtime);
    void putRuntime(Set<Long> hexagons, War war, AssaultRuntime runtime);
    void setPeace(long code);

    void makeIncorporation(WarState inner, WarState outer);

    long maximumCastleHealth();
    
}
