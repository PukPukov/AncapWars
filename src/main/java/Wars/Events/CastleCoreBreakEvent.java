package Wars.Events;

import Wars.Building.Castle.Castle;
import Wars.WarPlayers.AncapWarrior;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CastleCoreBreakEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private Castle castle;
    private AncapWarrior warrior;

    public CastleCoreBreakEvent(Castle castle, AncapWarrior warrior) {
        this.castle = castle;
        this.warrior = warrior;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Castle getCastle() {
        return this.castle;
    }

    public AncapWarrior getWarrior() {
        return this.warrior;
    }

}
