package ru.ancap.states.wars.api.request.system;

public class Path {
    
    public static String dot(String... nodes) {
        return String.join(".", nodes);
    }
}
