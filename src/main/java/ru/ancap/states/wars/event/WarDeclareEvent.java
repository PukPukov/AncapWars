package ru.ancap.states.wars.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.api.war.WarData;

@Getter
@RequiredArgsConstructor
public class WarDeclareEvent extends AncapEvent implements Cancellable {

    @Setter
    private boolean cancelled;
    private final WarState attacker;
    private final WarState target;
    private final WarData data;

    private static final HandlerList handlers = new HandlerList();
    public @NotNull HandlerList getHandlers() {return getHandlerList();}
    public static @NotNull HandlerList getHandlerList() {return handlers;}
}
