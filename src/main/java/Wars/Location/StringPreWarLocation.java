package Wars.Location;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class StringPreWarLocation {

    private String string;

    public StringPreWarLocation(String string) {
        this.string = string;
    }

    public WarLocation getPreparedLocation() {
        String[] data = this.string.split(";");
        World world = Bukkit.getWorld(data[0]);
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);
        return new WarLocation(new WarWorld(world), x, y, z);
    }
}
