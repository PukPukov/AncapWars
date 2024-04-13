package ru.ancap.states.wars.plugin.executor.util;

import ru.ancap.framework.communicate.message.CacheMessage;

public class Indicator extends CacheMessage {
    
    public Indicator(String indicator, boolean indicate) {
        super((nameIdentifier) -> {
            if (!indicate) return "";
            else return indicator;
        });
    }
    
}