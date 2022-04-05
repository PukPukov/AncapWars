package Wars.Commands.WarCommand.Command.SubCommands.WarDeclareCommand.govno;

import Wars.Location.WarLocation;
import Wars.WarHexagons.WarHexagon;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class BukkitTimer extends BukkitRunnable {
    
    private WarHexagon hexagon;
    private WarLocation loc;

    public BukkitTimer(WarHexagon hexagon, WarLocation loc) {
        this.hexagon = hexagon;
        this.loc = loc;
    }

    public WarHexagon getHexagon() {
        return this.hexagon;
    }

    public WarLocation getLocation() {
        return this.loc;
    }
}
