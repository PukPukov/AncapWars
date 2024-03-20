package ru.ancap.states.wars.utils;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import ru.ancap.framework.language.LAPI;

@Getter
public class LAPIComponent {

    private final Component component;

    public LAPIComponent(String id, String name) {
        this.component = Component.text(LAPI.localized(id, name));
    }

}
