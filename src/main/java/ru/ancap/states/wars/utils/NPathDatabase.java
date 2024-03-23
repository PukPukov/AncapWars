package ru.ancap.states.wars.utils;

import ru.ancap.framework.database.nosql.PathDatabase;

import java.util.Optional;

public interface NPathDatabase {
    
    static NPathDatabase of(PathDatabase database) {
        return new NPathDatabase() {
            @Override
            public Optional<Boolean> readBoolean(String path) {
                return Optional.ofNullable(database.readBoolean(path));
            }
            
            @Override
            public Optional<String> readString(String path) {
                return Optional.ofNullable(database.readString(path));
            }
        };
    }
    
    Optional<Boolean> readBoolean(String path);
    Optional<String> readString(String path);
    
}