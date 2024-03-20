package ru.ancap.states.wars.utils;

import lombok.AllArgsConstructor;
import ru.ancap.framework.language.LAPI;

import java.util.function.Function;

@AllArgsConstructor
public class LAPIPreparator implements Function<String, String> {

    private final String domainPattern;
    private final String name;

    @Override
    public String apply(String s) {
        return LAPI.localized(
                domainPattern.replace("?", s),
                name
        );
    }
}
