package Wars.Events.CastleCoreDamageEvent;

import Wars.Building.Castle.CastleCore;
import Wars.WarPlayers.AncapWarrior;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CastleCoreDamageEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private CastleCore core;
    private AncapWarrior warrior;

    public CastleCoreDamageEvent(CastleCore core, AncapWarrior warrior) {
        this.core = core;
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

    public CastleCore getCore() {
        return this.core;
    }

    public AncapWarrior getWarrior() {
        return this.warrior;
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }

}
