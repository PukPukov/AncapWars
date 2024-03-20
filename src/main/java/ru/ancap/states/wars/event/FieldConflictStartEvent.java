package ru.ancap.states.wars.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.ancap.states.wars.api.hexagon.WarHexagon;

@Getter
@RequiredArgsConstructor
public class FieldConflictStartEvent extends AncapEvent {

    @Setter
    private boolean cancelled;
    private final WarHexagon hexagon;

    private static final HandlerList handlers = new HandlerList();
    public @NotNull HandlerList getHandlers() {return getHandlerList();}
    public static @NotNull HandlerList getHandlerList() {return handlers;}

}
