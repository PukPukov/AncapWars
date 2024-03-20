package ru.ancap.states.wars.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.assault.AttackWait;
import ru.ancap.states.wars.plugin.listener.AssaultRuntime;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AssaultStartEvent extends AncapEvent implements Cancellable {

    @Setter
    private boolean cancelled;
    private final AttackWait attackWait;
    private final AssaultRuntime prepareRuntime;
    private final List<WarHexagon> toSet;

    private static final HandlerList handlers = new HandlerList();
    public @NotNull HandlerList getHandlers() {return getHandlerList();}
    public static @NotNull HandlerList getHandlerList() {return handlers;}

}
