package Wars.Events;

import Wars.WarHexagons.WarHexagon;
import Wars.WarPlayers.AncapWarrior;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WarHexagonStayEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private AncapWarrior warrior;
    private WarHexagon hexagon;

    public WarHexagonStayEvent(AncapWarrior warrior, WarHexagon hexagon) {
        this.warrior = warrior;
        this.hexagon = hexagon;
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

    public WarHexagon getHexagon() {
        return this.hexagon;
    }
}
