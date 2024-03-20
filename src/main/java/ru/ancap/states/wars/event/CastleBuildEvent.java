package ru.ancap.states.wars.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.ancap.states.wars.api.castle.BuiltCastle;

@RequiredArgsConstructor
@Getter
public class CastleBuildEvent extends AncapEvent implements Cancellable {

    @Setter
    private boolean cancelled;
    private final BuiltCastle createdCastle;
    private final Player creator;
    private final Location location;
    private final String name;

    private static final HandlerList handlers = new HandlerList();
    public @NotNull HandlerList getHandlers() {return getHandlerList();}
    public static @NotNull HandlerList getHandlerList() {return handlers;}
}
