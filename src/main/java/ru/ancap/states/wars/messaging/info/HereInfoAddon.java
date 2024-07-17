package ru.ancap.states.wars.messaging.info;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import ru.ancap.commons.declarative.flow.Branch;
import ru.ancap.framework.communicate.message.Message;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.states.here.HereInfo;
import ru.ancap.states.hexagons.Codeficator;
import ru.ancap.states.message.InfoMessage;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.castle.Castle;
import ru.ancap.states.wars.api.field.FieldConflicts;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.state.CityState;
import ru.ancap.states.wars.api.war.AssaultOperator;
import ru.ancap.states.wars.plugin.listener.AssaultRuntime;
import ru.ancap.states.wars.plugin.listener.AssaultRuntimeType;

import java.util.ArrayList;

@RequiredArgsConstructor
public class HereInfoAddon {
    
    private final AssaultOperator assaultOperator;
    private final FieldConflicts fieldConflicts;
    
    public void make() {
        HereInfo.registerAddon("wars", (player) -> {
            return new ArrayList<>() {{
                WarHexagon hexagon = new WarHexagon(Codeficator.hexagon(player));
                add(new InfoMessage.Value("hexagon-location", new Message(hexagon.getReadableHexagonCoordinates())));
                add(new InfoMessage.Value("hexagon-code", new Message(hexagon.code())));
                AssaultRuntime runtime = HereInfoAddon.this.assaultOperator.assault(hexagon.code());
                AssaultRuntimeType runtimeType = runtime.type();
                if (runtimeType == AssaultRuntimeType.PREPARE || runtimeType == AssaultRuntimeType.WAR) {
                    add(new InfoMessage.Value("assault-status", switch (runtimeType) {
                        case PREPARE -> new LAPIMessage(AncapWars.class, "assault.status.prepare");
                        case WAR -> new LAPIMessage(AncapWars.class, "assault.status.war");
                        default -> throw new IllegalStateException("Unexpected value: " + runtimeType);
                    }));
                    add(new InfoMessage.Value("attacker", new Message(runtime.attacker().name())));
                }
                @Nullable Castle castle = hexagon.castle();
                if (castle != null) {
                    add(new InfoMessage.Value("castle", new Message(castle.name())));
                }
                CityState owner = hexagon.getOwner();
                if (owner != null) if (owner.getHomeHexagon().code() == hexagon.code()) {
                    add(new InfoMessage.Value("core-barrier-broken", Branch.of(owner.coreBarrier() != null,
                        new LAPIMessage(AncapWars.class, "info.broken.no_"),
                        new LAPIMessage(AncapWars.class, "info.broken.yes_")
                    )));
                }
            }};
        });
    }
    
}