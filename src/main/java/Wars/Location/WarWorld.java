package Wars.Location;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class WarWorld {

    private World world;

    public WarWorld(World world) {
        this.world = world;
    }

    public WarWorld(Location loc) {
        this.world = loc.getWorld();
    }

    public World getBukkitWorld() {
        return this.world;
    }

    public Block getBlockAt(WarLocation location) {
        return this.world.getBlockAt(location.getBukkitLocation());
    }

    public boolean equals(WarWorld world) {
        return world.getBukkitWorld().getName().equals(this.getBukkitWorld().getName());
    }
}
