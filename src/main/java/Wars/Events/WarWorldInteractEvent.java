package Wars.Events;

import AncapLibrary.AncapEvents.AncapWorldInteractEvent;
import AncapLibrary.Library.Interceptable;
import Wars.Location.WarLocation;
import Wars.WarPlayers.AncapWarrior;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WarWorldInteractEvent extends Event implements Cancellable, Interceptable {

    private final AncapWorldInteractEvent event;
    private final AncapWarrior warrior;
    private final WarLocation loc;
    public static final HandlerList handlers = new HandlerList();

    public WarWorldInteractEvent(AncapWorldInteractEvent event, AncapWarrior warrior, WarLocation loc) {
        this.event = event;
        this.warrior = warrior;
        this.loc = loc;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.event.isCancelled();
    }

    public void setCancelled(boolean b) {
        this.event.setCancelled(b);
    }

    public AncapWorldInteractEvent getAncapEvent() {
        return this.event;
    }

    public AncapWarrior getWarrior() {
        return this.warrior;
    }

    public WarLocation getLocation() {
        return this.loc;
    }

    public void setIntercepted(boolean b) {
        this.event.setIntercepted(b);
    }

    public boolean isIntercepted() {
        return this.event.isIntercepted();
    }
}
