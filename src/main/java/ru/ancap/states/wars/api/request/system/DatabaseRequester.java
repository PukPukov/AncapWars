package ru.ancap.states.wars.api.request.system;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.ancap.commons.aware.Aware;
import ru.ancap.commons.aware.ContextAware;
import ru.ancap.commons.aware.InsecureContextHandle;
import ru.ancap.commons.debug.AncapDebug;
import ru.ancap.framework.database.nosql.PathDatabase;

import java.util.List;

@AllArgsConstructor
@ToString @EqualsAndHashCode
public class DatabaseRequester implements Requester {

    private final String id;
    private final String type;
    private final PathDatabase database;
    
    public String id() { return this.id; }

    @Override
    public List<Request> sends() {
        return this.requestListFrom("sends");
    }

    @Override
    public List<Request> receives() {
        return this.requestListFrom("receives");
    }

    private List<Request> requestListFrom(String sectionName) {
        PathDatabase requestsSection = this.database.inner(this.type+"."+sectionName);
        List<Request> list = requestsSection.keys().stream()
            .map(requestsSection::inner)
            .map(section -> new Request(
                section.readString("target"),
                new RequestState(section.readString("terms"))))
            .toList();
        AncapDebug.debug(list);
        return list;
    }
    
    @Override
    @ContextAware(awareOf = Aware.STATE, handle = InsecureContextHandle.NO_HANDLE)
    public void addReceives(Request request) {
        this.addRequests("receives", request);
    }
    
    @Override
    @ContextAware(awareOf = Aware.STATE, handle = InsecureContextHandle.NO_HANDLE)
    public void addSends(Request request) {
        this.addRequests("sends", request);
    }

    @Override
    @ContextAware(awareOf = Aware.STATE, handle = InsecureContextHandle.NO_HANDLE)
    public Request removeReceives(String id) {
        AncapDebug.debug("DATABASE REQUESTER", this.database, " REMOVES RECEIVE FROM", id);
        return this.removeRequests("receives", id);
    }
    
    @Override
    @ContextAware(awareOf = Aware.STATE, handle = InsecureContextHandle.NO_HANDLE)
    public Request removeSends(String id) {
        return this.removeRequests("sends", id);
    }

    private void addRequests(String direction, Request request) {
        PathDatabase section = this.database.inner(Path.dot(this.type, direction, request.target()));
        section.write("target", request.target());
        section.write("terms", request.state().terms());
    }

    private Request removeRequests(String direction, String id) {
        PathDatabase section = this.database.inner(Path.dot(this.type, direction, id));
        Request request = new Request(section.readString("target"), new RequestState(section.readString("terms")));
        section.nullify();
        return request;
    }

}
