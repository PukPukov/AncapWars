package ru.ancap.states.wars.debug;

import net.kyori.adventure.text.Component;
import org.apache.commons.math3.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import ru.ancap.hexagon.Hexagon;
import ru.ancap.hexagon.common.Point;
import ru.ancap.states.AncapStates;

import java.util.Random;

public class Debugger {

    @SafeVarargs
    public static void mark(String name, Pair<String, Object>... markeds) {
        Debugger.message("Мониторинг значений переменных в "+name+": ");
        for (Pair<String, Object> marked : markeds) {
            Debugger.message("|"+name+"| "+marked.getFirst()+": "+marked.getSecond());
        }
    }

    public static void mark(String monitorName,
                            String name, Object object) {
        mark(monitorName, new Pair<>(name, object));
    }

    public static void mark(String monitorName,
                            String name, Object object,
                            String name1, Object object1) {
        mark(
                monitorName,
                new Pair<>(name, object),
                new Pair<>(name1, object1)
        );
    }

    public static void mark(String monitorName,
                            String name, Object object,
                            String name1, Object object1,
                            String name2, Object object2) {
        mark(
                monitorName,
                new Pair<>(name, object),
                new Pair<>(name1, object1),
                new Pair<>(name2, object2)
        );
    }

    public static void mark(String monitorName,
                            String name, Object object,
                            String name1, Object object1,
                            String name2, Object object2,
                            String name3, Object object3) {
        mark(
                monitorName,
                new Pair<>(name, object),
                new Pair<>(name1, object1),
                new Pair<>(name2, object2),
                new Pair<>(name3, object3)
        );
    }

    public static void markHexLightning(long code) {
        Debugger.message("Центр гексагона "+code+" был подсвечен молнией");
        Hexagon hexagon = AncapStates.grid.hexagon(code);
        Point center = hexagon.center();
        World world = Bukkit.getWorld("world");
        Location strikeLocation = new Location(
                world,
                center.x(),
                world.getHighestBlockYAt((int) center.x(), (int) center.y()),
                center.y()
        );
        if (new Random().nextDouble() > 0.95) {
            Bukkit.getPlayer("PukPukov").sendMessage(Component.text("Телепортируем по страйк-локации"));
            Bukkit.getPlayer("PukPukov").teleport(strikeLocation);
        }
        world.strikeLightning(strikeLocation);
    }

    public static void message(String msg) {
        // Bukkit.getPlayer("PukPukov").sendMessage(msg);
    }

}