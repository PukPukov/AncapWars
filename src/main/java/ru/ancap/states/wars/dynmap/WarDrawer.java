package ru.ancap.states.wars.dynmap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerSet;
import ru.ancap.states.AncapStates;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.castle.Castle;

import java.util.List;
import java.util.UUID;

public class WarDrawer {

    public void drawCastle(Castle castle) {
        Bukkit.getScheduler().runTask(
                AncapWars.loaded(),
                () -> {
                    MarkerSet markerSet = AncapStates.getDynmapMarkerSet();
                    Location location = castle.getLocation();
                    Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            "dmarker add" +
                                    " id:"+castle.name()+
                                    " icon:tower"+
                                    " set:"+AncapStates.DYNMAP_MARKER_SET_ID+
                                    " x:"+location.getBlockX()+
                                    " y:"+location.getBlockY()+
                                    " z:"+location.getBlockZ()+
                                    " world:"+location.getWorld().getName()
                    );
                    Marker marker = markerSet.findMarker(castle.name());
                    marker.setDescription(new DynmapDescription(List.of(
                            "<center><strong>"+castle.name()+"</strong></center>"
                    )).generateHtml());
                }
        );
    }

    public void drawDevastation(Location location) {
        Bukkit.getScheduler().runTask(
            AncapWars.loaded(),
            () -> {
                MarkerSet markerSet = AncapStates.getDynmapMarkerSet();
                String uuid = UUID.randomUUID().toString();
                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "dmarker add" +
                        " id:"+uuid+
                        " icon:warning"+
                        " set:"+AncapStates.DYNMAP_MARKER_SET_ID+
                        " x:"+location.getBlockX()+
                        " y:"+location.getBlockY()+
                        " z:"+location.getBlockZ()+
                        " world:"+location.getWorld().getName()
                );
                Marker marker = markerSet.findMarker(uuid);
                marker.setDescription(new DynmapDescription(List.of(
                    "<center><strong>Гексагон в состоянии разрухи</strong></center>"
                )).generateHtml());
            }
        );
    }

    public void drawSimpleMarker(Location location) {
        Bukkit.getScheduler().runTask(
                AncapWars.loaded(),
                () -> {
                    MarkerSet markerSet = AncapStates.getDynmapMarkerSet();
                    Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            "dmarker add" +
                                    " id:"+ UUID.randomUUID() +
                                    " icon:house"+
                                    " set:"+AncapStates.DYNMAP_MARKER_SET_ID+
                                    " x:"+location.getBlockX()+
                                    " y:"+location.getBlockY()+
                                    " z:"+location.getBlockZ()+
                                    " world:"+location.getWorld().getName()
                    );
                }
        );
    }
    
}
