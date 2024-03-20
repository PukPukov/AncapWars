package ru.ancap.states.wars.api.request.system;

import java.util.List;

public interface Requester {

    String id();
    List<Request> sends();
    List<Request> receives();

    void addSends(Request request);
    void addReceives(Request request);
    Request removeReceives(String id);
    Request removeSends(String id);
    
}
