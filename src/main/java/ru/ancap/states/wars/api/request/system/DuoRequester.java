package ru.ancap.states.wars.api.request.system;

import ru.ancap.states.wars.api.request.system.exception.AlreadySentException;
import ru.ancap.states.wars.api.request.system.exception.NoRequestException;

public interface DuoRequester extends Requester {

    Requester target();
    DuoRequester swap();

    default RequestState send(String terms) throws AlreadySentException {
        if (this.sends().stream().map(Request::target).anyMatch(string -> string.equals(this.target().id()))) throw new AlreadySentException();
        RequestState state = new RequestState(terms);
        this.addSends(new Request(this.target().id(), state));
        this.target().addReceives(new Request(this.id(), state));
        return state;
    }

    default RequestState receive(String terms) throws AlreadySentException {
        return this.swap().send(terms);
    }

    default RequestState unsend() throws NoRequestException {
        if (this.sends().stream().map(Request::target).noneMatch(string -> string.equals(this.target().id()))) throw new NoRequestException();
        this.removeSends(this.target().id());
        return this.target().removeReceives(this.id()).state();
    }

    default RequestState unreceive() throws NoRequestException {
        return this.swap().unsend();
    }

}
