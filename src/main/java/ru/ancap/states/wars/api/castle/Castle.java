package ru.ancap.states.wars.api.castle;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.states.AncapStates;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.player.Warrior;
import ru.ancap.states.wars.api.state.Barrier;
import ru.ancap.states.wars.api.state.CityState;
import ru.ancap.states.wars.assault.AttackWait;

/**
 * Получать через WarHexagon#getCastle();
 */
@EqualsAndHashCode @ToString
public abstract class Castle implements Barrier {

    public abstract String id();
    public abstract PathDatabase getDatabase();

    public void initialize(String creator, Location location, String name) {
        this.getDatabase().write("creator", creator);
        this.getDatabase().write("hexagon", ""+ AncapStates.grid.hexagon(location).code());
        this.getDatabase().write("location.world", location.getWorld().getName());
        this.getDatabase().write("location.x", location.getBlockX());
        this.getDatabase().write("location.y", location.getBlockY());
        this.getDatabase().write("location.z", location.getBlockZ());
    }

    public boolean exists() {
        return this.getDatabase().isSet("name");
    }

    public Location getLocation() {
        return new Location(
            Bukkit.getWorld(
            this.getDatabase().readString("location.world")),
            this.getDatabase().readInteger("location.x"),
            this.getDatabase().readInteger("location.y"),
            this.getDatabase().readInteger("location.z")
        );
    }

    public String name() {
        return this.getDatabase().readString("name");
    }

    public WarHexagon hexagon() {
        String hexagonId = this.getDatabase().readString("hexagon");
        if (hexagonId == null) {
            return null;
        }
        return new WarHexagon(Long.parseLong(hexagonId));
    }

    public boolean canBeManagedBy(Warrior warrior) {
        return warrior.hasAssistantPerms();
    }

    @NotNull
    public AttackWait attackWait() {
        return new AttackWait(this);
    }

    public void delete() {
        this.hexagon().getDatabase().delete("castle");
        this.getDatabase().nullify();
    }

    @NotNull
    public CityState getOwner() {
        return this.hexagon().getOwner();
    }
    
}
