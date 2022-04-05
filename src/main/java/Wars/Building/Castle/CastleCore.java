package Wars.Building.Castle;

import Wars.Location.WarLocation;
import org.bukkit.Material;

public class CastleCore {

    private CoreHealth health;
    private WarLocation location;

    public CastleCore(WarLocation location) {
        this.location = location;
    }

    public void damage() throws CoreDeadException {
        try {
            this.health.damage();
        } catch (CantDamageException e) {
            throw new CoreDeadException();
        }
    }

    public WarLocation getLocation() {
        return this.location;
    }

    public void place() {
        this.location.getBlockAt().setType(Material.OBSIDIAN);
    }
}
