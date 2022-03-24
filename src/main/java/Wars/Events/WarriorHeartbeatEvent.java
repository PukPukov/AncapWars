package Wars.Events;

import Wars.WarPlayers.AncapWarrior;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WarriorHeartbeatEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private AncapWarrior warrior;

    public WarriorHeartbeatEvent(AncapWarrior warrior) {
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

    public AncapWarrior getWarrior() {
        return this.warrior;
    }

}
