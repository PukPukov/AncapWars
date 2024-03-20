package ru.ancap.states.wars.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import ru.ancap.states.wars.AncapWars;

public abstract class AncapEvent extends Event {

    @Override
    public boolean callEvent() {
        new Thread(() -> {
            if (this.isAsynchronous()) {
                Bukkit.getScheduler().runTaskAsynchronously(
                        AncapWars.loaded(),
                        super :: callEvent
                );
            } else {
                Bukkit.getScheduler().callSyncMethod(
                        AncapWars.loaded(),
                        super :: callEvent
                );
            }
        }).start();
        return true;
    }

}
