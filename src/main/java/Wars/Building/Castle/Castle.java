package Wars.Building.Castle;

import AncapLibrary.Library.AncapLibrary;
import AncapLibrary.Library.AncapObject;
import Wars.AncapWars.AncapWars;
import Wars.Building.Building;
import Wars.Location.WarLocation;
import Wars.WarStates.WarCities.WarCity;
import Wars.WarStates.WarStateType;
import com.fasterxml.uuid.Generators;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import states.Main.AncapStates;

public class Castle implements Building, AncapObject {

    private String id;
    private String name;
    private WarLocation location;
    private CastleHealth health;

    public Castle(WarLocation location, String name) {
        this.location = location;
        this.name = name;
        this.id = Generators.timeBasedGenerator().generate().toString();
        this.health = new CastleHealth();
    }

    public void spawnCore() {

    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public WarLocation getLocation() {
        return this.location;
    }

    public CastleHealth getHealth() {
        return this.health;
    }

    @Override
    public void setMeta(String s, String s1) {
        AncapStates.getMainDatabase().write("states.castle."+this.id+"."+s, s1);
    }

    @Override
    public String getMeta(String s) {
        return AncapStates.getMainDatabase().getString("states.castle."+this.id+"."+s);
    }

    @Deprecated(forRemoval = true)
    public void createPhysically() {
        this.saveToDb();
        this.drawOnDynMap();
        this.notifyForCreation();
    }

    @Deprecated(forRemoval = true)
    private void notifyForCreation() {
        WarCity city = (WarCity) AncapWars.getWarStateMap().findWarState(WarStateType.CITY, AncapStates.getCityMap().getCity(new Location(Bukkit.getWorld(this.getLocation().getWorld()), this.getLocation().getX(), this.getLocation().getY(), this.getLocation().getZ())).getName());
        AncapStates.sendMessage(AncapWars.getConfiguration().getCastleCreationMessage(city.getName(), this.getName()));
    }

    @Deprecated(forRemoval = true)
    private void drawOnDynMap() {
        WarLocation location = this.getLocation();
        String x = String.valueOf(location.getX());
        String y = String.valueOf(location.getY());
        String z = String.valueOf(location.getZ());
        String world = location.getWorld();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dmarker add id:"+this.name+" icon:tower set:ancap x:"+x+" y:"+y+" z:"+z+" world:"+world);
    }

    @Deprecated(forRemoval = true)
    private void saveToDb() {
        AncapLibrary.getConfiguredDatabase().write("states.hexagons."+this.getLocation().getHexagon().toString()+".castle", this.id);
        this.setMeta("name", this.name);
        this.setMeta("location", this.location.getString());
    }
}
