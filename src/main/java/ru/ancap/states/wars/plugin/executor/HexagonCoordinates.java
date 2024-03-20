package ru.ancap.states.wars.plugin.executor;

import lombok.AllArgsConstructor;
import ru.ancap.framework.communicate.message.CallableMessage;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.hexagon.Hexagon;
import ru.ancap.hexagon.common.Point;
import ru.ancap.states.message.PrecisionFormatter;
import ru.ancap.states.wars.AncapWars;

@AllArgsConstructor
public class HexagonCoordinates implements CallableMessage {
    
    private final Hexagon hexagon;

    @Override
    public String call(String nameIdentifier) {
        Point point = this.hexagon.center();
        return new LAPIMessage(
            AncapWars.class, "hexagon.coordinates",
            new Placeholder("x", PrecisionFormatter.format(point.x(), 1)),
            new Placeholder("z", PrecisionFormatter.format(point.y(), 1))
        ).call(nameIdentifier);
    }
}
