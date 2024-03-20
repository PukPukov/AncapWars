package ru.ancap.states.wars.api.castle;

import org.jetbrains.annotations.Nullable;

public class Founder<T> {
    
    private T data;
    
    public void load(T data) {
        this.data = data;
    }
    
    @Nullable
    public T found() {
        return this.data;
    }
    
}
