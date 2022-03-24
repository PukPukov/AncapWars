package Wars.Building.Castle;

public class CastleHealth {

    private Castle castle;
    private int health;

    public int getAmount() {
        return this.health;
    }

    public void damage() {
        this.health = this.health - 1;
    }
}
