package ru.ancap.states.wars.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.ancap.commons.debug.AncapDebug;
import ru.ancap.states.event.events.CityDeleteEvent;
import ru.ancap.states.event.events.CityFoundEvent;
import ru.ancap.states.event.events.NationDeleteEvent;
import ru.ancap.states.event.events.NationFoundEvent;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.connector.StateType;

public class BridgeListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void on(NationDeleteEvent event) {
        AncapDebug.debug("NATION DELETE EVENT", event);
        this.onWarStateDelete(event.getNation().getID());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(CityDeleteEvent event) {
        this.onWarStateDelete(event.getCity().getID());
    }

    private void onWarStateDelete(String id) {
        WarState state = WarState.of(id);
        state.prepareToDelete();
        state.delete();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(CityFoundEvent event) {
        WarState.initialize(event.getHost().getID(), StateType.CITY);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(NationFoundEvent event) {
        WarState.initialize(event.getHost().getID(), StateType.NATION);
        WarState inner = WarState.of(event.getCreator().id());
        WarState host  = WarState.of(event.getHost()   .id());
        AncapDebug.debug("Вызываем warIncorporate() после NationFoundEvent", "inner", inner.debugIdentifier(), "host", host.debugIdentifier());
        inner.warIncorporate(host);
    }

}