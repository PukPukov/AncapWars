package ru.ancap.states.wars.plugin.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(fluent = true) @Getter
public class AssaultStatus {
    
    private final PoliticalState politicalState;
    
    public enum PoliticalState {
        PEACE,
        PREPARE,
        BATTLE,
    }

}