package ru.ancap.states.wars.api.war;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.states.wars.api.request.system.Path;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.event.PenaltyGiveEvent;

@RequiredArgsConstructor
public class WarView {
    
    private final @NotNull War          war;
    private final @NotNull PathDatabase warDatabase;
    private final @NotNull PathDatabase database;
    private final @NotNull Redirects    redirects;
    
    public WarView(War war, PathDatabase warDatabase, Redirects redirects) {
        this(war, warDatabase, warDatabase.inner(Path.dot("sides", redirects.current())), redirects);
    }
    
    public War war() { return this.war; }

    public boolean atPenalty() {
        if (true) return false;
        Long penaltyUntil = this.database.readInteger("penalty-until");
        return penaltyUntil == null ? false : penaltyUntil > System.currentTimeMillis();
    }

    public void givePenalty(int hours) {
        new PenaltyGiveEvent(this, hours).callEvent();
    }

    public void setPenalty(int hours) {
        this.setPenaltyUntil(System.currentTimeMillis() + hours * 60L * 60 * 1000);
    }

    public void setPenaltyUntil(long until) {
        this.database.write("penalty-until", until);
    }

    public WarView invert() {
        return new WarView(this.war, this.warDatabase, this.redirects.invert());
    }

    public WarState opponent() {
        return this.invert().state();
    }
    
    public void transferTo(WarState state) {
        this.database.write("id", state.id());
    }

    public WarState state() {
        return WarState.of(this.database.readString("id"));
    }

    public record Redirects(String current, String opponent) {
        
        public Redirects invert() {
            return new Redirects(this.opponent, this.current);
        }
        
    }
    
}