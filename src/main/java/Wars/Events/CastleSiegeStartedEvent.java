package Wars.Events;

import Wars.Building.Castle.Castle;
import Wars.WarStates.WarState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CastleSiegeStartedEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private WarState attacker;
    private Castle castle;

    public CastleSiegeStartedEvent(WarState attacker, Castle castle) {
        this.attacker = attacker;
        this.castle = castle;
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
        return this.attacker;
    }

    public Castle getCastle() {
        return this.castle;
    }

}
