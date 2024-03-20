package ru.ancap.states.wars.api.war;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.ancap.states.wars.plugin.listener.AssaultRuntime;

@Getter
@AllArgsConstructor
public class AssaultRepulseEvent extends Event {

    private final AssaultRuntime runtime;

    private static final HandlerList handlers = new HandlerList();
    public @NotNull HandlerList getHandlers() {return getHandlerList();}
    public static @NotNull HandlerList getHandlerList() {return handlers;}

}
