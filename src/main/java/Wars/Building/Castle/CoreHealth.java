package Wars.Building.Castle;

import Wars.AncapWars.AncapWars;
import Wars.Location.WarLocation;

public class CoreHealth {

    private int health = AncapWars.getConfiguration().getCastleCoreHealth();
    private WarLocation location;

    public int getAmount() {
        return this.health;
    }

    public void damage() throws CantDamageException {
        if (this.getAmount() < 2) {
            throw new CantDamageException();
        }
        this.health = this.health - 1;
    }
}
