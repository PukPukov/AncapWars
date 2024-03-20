package ru.ancap.states.wars.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.ancap.commons.debug.AncapDebug;
import ru.ancap.states.event.events.*;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.castle.Castle;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.state.CityState;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.connector.StateType;
import ru.ancap.states.wars.dynmap.WarDrawer;

public class BridgeListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void on(DynmapRedrawEvent event) {
        new Thread(() -> {
            WarDrawer drawer = new WarDrawer();
            for (Castle castle : AncapWars.warMap().getCastles()) {
                drawer.drawCastle(castle);
            }
            for (CityState city : AncapWars.warMap().cities()) {
                for (WarHexagon hexagon : city.territories()) {
                    if (hexagon.devastation() instanceof WarHexagon.DevastationStatus.Devastated devastated) {
                        drawer.drawDevastation(devastated.brokenCastleLocation());
                    }
                }
            }
        }).start();
    }

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
        inner.warIncorporate(host);
    }

}
