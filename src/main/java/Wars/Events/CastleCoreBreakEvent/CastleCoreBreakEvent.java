package Wars.Events.CastleCoreBreakEvent;

import AncapLibrary.AncapEvents.AncapEvent;
import Wars.Building.Castle.CastleCore;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CastleCoreBreakEvent extends AncapEvent {

    public static final HandlerList handlers = new HandlerList();

    private CastleCore core;

    public CastleCoreBreakEvent(CastleCore core) {
        this.core = core;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CastleCore getCore() {
        return this.core;
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }
}
