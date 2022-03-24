package Wars.Location;

import Wars.AncapWars.AncapWars;
import Wars.Building.Castle.Castle;
import Wars.Building.Castle.CastleAlreadyCreatedException;
import Wars.WarHexagons.WarHexagon;
import org.bukkit.Location;

public class WarLocation {

    private String world;
    private double x;
    private double y;
    private double z;

    public WarLocation(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public WarLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public WarHexagon getHexagon() {
        return new WarHexagon(AncapWars.getGrid().getHexagon(this));
    }

    public void createCastle(String name) {
        this.validateCastleCreation();
        Castle castle = new Castle(this, name);
        this.getHexagon().setCastle(castle);
        castle.createPhysically();
    }

    public void validateCastleCreation() {
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

    public String getWorld() {
        return this.world;
    }
}
