package ru.ancap.states.wars.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.ancap.states.wars.api.state.Barrier;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.api.war.War;

@Getter
@RequiredArgsConstructor
public class BarrierAttackDeclareEvent extends AncapEvent {

    @Setter
    private boolean cancelled;
    private final Barrier barrier;
    private final WarState attacker;
    private final War war;

    private static final HandlerList handlers = new HandlerList();
    public @NotNull HandlerList getHandlers() {return getHandlerList();}
    public static @NotNull HandlerList getHandlerList() {return handlers;}

}