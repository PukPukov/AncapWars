package ru.ancap.states.wars.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.ancap.hexagon.Hexagon;
import ru.ancap.hexagon.common.Point;

public class HexLocation {


    public static Location fromHexCenter(Hexagon hexagon) {
        Point center = hexagon.center();
        return fromHexPoint(center);
    }

    public static Location fromHexPoint(Point center) {
        return new Location(
            Bukkit.getWorld("world"),
            center.x(),
            64,
            center.y()
        );
    }

}
