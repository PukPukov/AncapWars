package ru.ancap.states.wars.api.castle;

public class Mark {
    
    private boolean marked;
    
    public void mark() {
        this.marked = true;
    }
    
    public boolean marked() {
        return this.marked;
    }
    
}
