package Wars.Location;

import Wars.AncapWars.AncapWars;
import Wars.Building.Castle.Castle;
import Wars.Building.Castle.CastleAlreadyCreatedException;
import Wars.Building.Castle.CastleCore;
import Wars.Building.Castle.CastleNameAlreadyUsedException;
import Wars.WarHexagons.WarHexagon;
import library.Hexagon;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class WarLocation {

    private WarWorld world;
    private double x;
    private double y;
    private double z;

    public WarLocation(WarWorld world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public WarLocation(Location location) {
        this.world = new WarWorld(location);
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public WarHexagon getHexagon() {
        Hexagon hex = AncapWars.getGrid().getHexagon(this);
        return AncapWars.getWarStateMap().findWarHexagon(hex.getQ(), hex.getR());
    }

    @Deprecated (forRemoval = true)
    public void createCastle(String name) {
        this.validateCastleCreation();
        Castle castle = new Castle(name, new CastleCore(this));
        this.getHexagon().setCastle(castle);
    }

    public void validateCastleCreation() {
        if (false) {
            throw new CastleNameAlreadyUsedException();
        }
        if (this.getHexagon().getCastle() != null) {
            throw new CastleAlreadyCreatedException("");
        }
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public String getString() {
        return this.world+";"+this.x+";"+this.y+";"+this.z;
    }

    public WarWorld getWorld() {
        return this.world;
    }

    public Block getBlockAt() {
        return this.world.getBlockAt(this);
    }

    public Location getBukkitLocation() {
        return new Location(this.world.getBukkitWorld(), this.x, this.y, this.z);
    }

    public boolean blockEquals(WarLocation location) {
        return location.world.equals(this.getWorld()) &&
                ((int) location.x) == ((int) this.x) &&
                ((int) location.y) == ((int) this.y) &&
                ((int) location.z) == ((int) this.z);
    }
}
