package Wars.Events.WarDeclaredEvent;

import AncapLibrary.AncapEvents.AncapEvent;
import Wars.WarStates.WarState;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WarDeclaredEvent extends AncapEvent {

    public static final HandlerList handlers = new HandlerList();

    private WarState attacker;
    private WarState attacked;

    public WarDeclaredEvent(WarState attacker, WarState attacked) {
        this.attacker = attacker;
        this.attacked = attacked;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WarState getAttacker() {
        return attacker;
    }

    public WarState getAttacked() {
        return attacked;
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }
}
