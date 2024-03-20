package ru.ancap.states.wars.stepback;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public class StepbackAPI {

    public static void stepback(Player player, Predicate<Location> locationPredicate) {
        Location teleportTo = new Location(
                player.getLocation().getWorld(),
                player.getLocation().getX()-150,
                player.getLocation().getY(),
                player.getLocation().getZ()
        );
        teleportTo.setY(teleportTo.getWorld().getHighestBlockAt((int) teleportTo.getX(), (int) teleportTo.getZ()).getY());
        player.teleport(teleportTo);
    }
    
}
