package ru.ancap.states.wars.plugin.listener;

import java.util.function.Consumer;
import java.util.function.Function;

public class Nullable {
    
    public static <T, O> O function(T nullable, Function<T, O> function) {
        if (nullable != null) return function.apply(nullable);
        else return null;
    }
    
    public static <T> void action(T nullable, Consumer<T> action) {
        if (nullable != null) action.accept(nullable);
    }
    
}
