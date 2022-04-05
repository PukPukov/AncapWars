package Wars.Building.Castle;

import AncapLibrary.Library.AncapObject;
import Wars.Building.Building;
import states.Main.AncapStates;

public class Castle implements Building, AncapObject {

    private String name;
    private CastleCore core;

    public Castle(String name, CastleCore core) {
        this.name = name;
        this.core = core;
    }

    public void spawnCore() {

    }

    public String getName() {
        return this.name;
    }

    public CastleCore getCore() {
        return this.core;
    }

    @Override
    public void setMeta(String s, String s1) {
        AncapStates.getMainDatabase().write("states.castle."+this.name+"."+s, s1);
    }

    @Override
    public String getMeta(String s) {
        return AncapStates.getMainDatabase().getString("states.castle."+this.name+"."+s);
    }
}
