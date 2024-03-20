package ru.ancap.states.wars.plugin.executor.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StateIsNeutralException extends Exception {
    
    private final String neutralStateName;

}
