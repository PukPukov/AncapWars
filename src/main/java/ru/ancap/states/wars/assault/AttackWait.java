package ru.ancap.states.wars.assault;

import lombok.Getter;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.states.wars.api.state.Barrier;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.api.war.War;

import java.util.UUID;

@Getter
public class AttackWait {

    private final PathDatabase database;
    private final Barrier barrier;

    public AttackWait(Barrier barrier) {
        this.database = barrier.database().inner("assault-wait");
        this.barrier = barrier;
    }

    public void initialize(WarState attacker, War war) {
        this.database.write("attacker", attacker.id());
        this.database.write("war", war.getId());
        this.database.write("attack-id", UUID.randomUUID().toString());
    }
    
    public String assaultID() {
        return this.database.readString("attack-id");
    }

    public void setAssaultStartTime(long time) {
        this.database.write("time", time);
    }

    public WarState getAttacker() {
        return WarState.of(this.database.readString("attacker"));
    }

    /**
     * @return null if no assault, otherwise time
     */
    public Long getTime() {
        return this.database.readInteger("time");
    }

    public War war() {
        return new War(this.database.readString("war"));
    }

    public void delete() {
        this.database.nullify();
    }
}
