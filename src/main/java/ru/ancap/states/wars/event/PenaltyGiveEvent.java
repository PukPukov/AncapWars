package ru.ancap.states.wars.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.ancap.states.wars.api.war.WarView;

@AllArgsConstructor
@Getter
public class PenaltyGiveEvent extends AncapEvent {

    private final WarView penalted;
    private final int hours;

    private static final HandlerList handlers = new HandlerList();
    public @NotNull HandlerList getHandlers() {return getHandlerList();}
    public static @NotNull HandlerList getHandlerList() {return handlers;}

}
