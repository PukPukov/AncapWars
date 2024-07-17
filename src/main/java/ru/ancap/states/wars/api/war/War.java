package ru.ancap.states.wars.api.war;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.id.WarID;
import ru.ancap.states.wars.plugin.listener.AssaultRuntimeType;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
@EqualsAndHashCode @ToString
public class War {

    private final String id;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private final PathDatabase database;
    
    @Nullable
    public static War ofName(String name) {
        if (!WarID.war().isBound(name)) return null;
        return new War(WarID.war().get(name));
    }

    public War(String id) {
        this.id = id;
        this.database = AncapWars.database().inner("wars."+id);
    }

    public void initialize(WarState attacker, WarState defender, WarData data) {
        attacker.addWar(this.id);
        defender.addWar(this.id);
        this.database.write("name", data.name());
        this.database.write("reason", data.reason());
        this.database.write("active", true);
        this.database.write("status", "active");
        this.database.write("sides.attacker.id", attacker.id());
        this.database.write("sides.defender.id", defender.id());
    }
    
    @NotNull
    public String name() {
        return Objects.requireNonNull(this.database.readString("name"));
    }
    
    @NotNull
    public String reason() {
        return Objects.requireNonNull(this.database.readString("reason"));
    }

    public List<WarView> sides() {
        return List.of(this.attackerView(), this.defenderView());
    }

    public WarView attackerView() {
        return this.viewAs(this.attackerId());
    }

    public String attackerId() {
        return this.database.readString("sides.attacker.id");
    }

    public WarView defenderView() {
        return this.viewAs(this.defenderId());
    }

    public String defenderId() {
        return this.database.readString("sides.defender.id");
    }

    private static final String ATTACKER = "attacker";
    private static final String DEFENDER = "defender";

    public WarView viewAs(String stateId) {
        boolean isAttacker = this.attackerId().equals(stateId);
        boolean isDefender = this.defenderId().equals(stateId);
        if (isAttacker == isDefender) throw new IllegalStateException("Illegal opponents: " + 
            "attacker = "+isAttacker+"; " +
            "defender = "+isDefender+"; " +
            "requested state id = "+stateId+" ("+ WarState.of(stateId).getName()+");  "+ 
            "war id = "+this.id+" ("+this.name()+")"
        );
        String current;
        String opponent;
        if (isAttacker) { current = ATTACKER; opponent = DEFENDER; }
        else            { current = DEFENDER; opponent = ATTACKER; }
        return new WarView(this, this.database, new WarView.Redirects(current, opponent));
    }

    public void prepareToEnd() {
        for (WarView view : this.sides()) view.state().cancelPeaceRequests(this.id);
        AncapWars.assaults().findAssaultRuntimesOf(this).forEach(runtime -> {
            if (runtime.type() == AssaultRuntimeType.WAR) new AssaultEndEvent(AssaultEndEvent.Reason.PEACE, runtime).callEvent();
        });
    }

    private void end(WarEndStrategy strategy) {
        strategy.apply(this);
    }

    public void stop(WarEndStrategy strategy) {
        this.prepareToEnd();
        this.end(strategy);
    }

    public boolean active() {
        return this.database.readBoolean("active");
    }

    public interface WarEndStrategy {
        
        void apply(War war);
        
    }
    
    @AllArgsConstructor
    public static class PeaceStrategy implements WarEndStrategy {
        
        private final WarState initiator;
        private final String terms;
        
        @Override
        public void apply(War war) {
            war.database.write("active", false);
            war.database.write("status", "peace");
            war.database.write("peace.initiator", this.initiator.id());
            war.database.write("peace.terms", this.terms);
        }
        
    }
    
    @AllArgsConstructor
    public static class MergeStrategy implements WarEndStrategy {
        
        private final War mergedInto;
        
        @Override
        public void apply(War war) {
            war.database.write("active", false);
            war.database.write("status", "merged");
            war.database.write("merged-into", this.mergedInto.getId());
        }
        
    }
    
    @AllArgsConstructor
    public static class DefeatStrategy implements WarEndStrategy {
        
        private final WarState winner;
        
        @Override
        public void apply(War war) {
            war.database.write("active", false);
            war.database.write("status", "defeat");
            war.database.write("defeat.winner", this.winner.id());
        }
        
    }
    
    public static class AdminTerminateStrategy implements WarEndStrategy {
        
        public static final AdminTerminateStrategy STRATEGY = new AdminTerminateStrategy();
        
        @Override
        public void apply(War war) {
            war.database.nullify();
        }
        
    }
    
}