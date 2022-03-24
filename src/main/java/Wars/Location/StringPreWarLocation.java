package Wars.Location;

public class StringPreWarLocation {

    private String string;

    public StringPreWarLocation(String string) {
        this.string = string;
    }

    public WarLocation getPreparedLocation() {
        String[] data = this.string.split(";");
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);
        return new WarLocation(data[0], x, y, z);
    }
}
