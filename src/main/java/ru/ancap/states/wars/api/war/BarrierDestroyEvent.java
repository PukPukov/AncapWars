package ru.ancap.states.wars.api.war;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.ancap.states.wars.api.state.Barrier;
import ru.ancap.states.wars.api.state.WarState;

@Getter
@AllArgsConstructor
public class BarrierDestroyEvent extends Event {
    
    @NotNull
    private final Barrier barrier;
    private final WarState destroyer;

    private static final HandlerList handlers = new HandlerList();
    public @NotNull HandlerList getHandlers() {return getHandlerList();}
    public static @NotNull HandlerList getHandlerList() {return handlers;}

}
