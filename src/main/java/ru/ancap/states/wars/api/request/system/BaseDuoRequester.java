package ru.ancap.states.wars.api.request.system;

import lombok.*;
import lombok.experimental.Delegate;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@With
@ToString @EqualsAndHashCode
public class BaseDuoRequester implements DuoRequester {

    @Delegate
    private final Requester sender;
    private final Requester target;
    @ToString.Exclude @EqualsAndHashCode.Exclude private final BaseDuoRequester companion;
    
    public BaseDuoRequester(Requester sender, Requester target) {
        this.sender = sender;
        this.target = target;
        this.companion = new BaseDuoRequester(target, sender, this);
    }

    @Override
    public Requester target() {
        return this.target;
    }

    public BaseDuoRequester swap() {
        return this.companion;
    }

}
