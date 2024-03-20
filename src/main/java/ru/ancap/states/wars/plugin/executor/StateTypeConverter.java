package ru.ancap.states.wars.plugin.executor;

import ru.ancap.states.wars.connector.StateType;

public class StateTypeConverter {
    
    public static StateType toWars(ru.ancap.states.states.StateType type) {
        if (type == ru.ancap.states.states.StateType.CITY) return StateType.CITY;
        if (type == ru.ancap.states.states.StateType.NATION) return StateType.NATION;
        throw new IllegalStateException();
    }

    public static ru.ancap.states.states.StateType toStates(StateType type) {
        if (type == StateType.CITY) return ru.ancap.states.states.StateType.CITY;
        if (type == StateType.NATION) return ru.ancap.states.states.StateType.NATION;
        throw new IllegalStateException();
    }
    
}
