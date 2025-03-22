package ru.ancap.states.wars.plugin.listener;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ancap.states.wars.api.state.Barrier;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.api.war.War;
import ru.ancap.states.wars.assault.AttackWait;

import java.util.concurrent.atomic.AtomicInteger;

public interface AssaultRuntime {

    AssaultRuntime PEACE = new Peace();

    static AssaultRuntime warOf(int health, Barrier barrier, WarState attacker, War war, AttackWait wait) {
        return new Realized(health, barrier, attacker, war, wait, wait.assaultID());
    }

    static AssaultRuntime prepareOf(Barrier barrier, WarState attacker, War war, AttackWait wait) {
        return new Prepare(barrier, attacker, war, wait, wait.assaultID());
    }

    String id();
    int health();
    void setHealth(int health);
    boolean hit();
    AssaultStatus status();
    Barrier barrier();
    WarState attacker();
    War war();
    AttackWait waitAPI();
    boolean active();
    void setActive(boolean active);

    @EqualsAndHashCode @ToString
    class Peace implements AssaultRuntime {
        
        private final static AssaultStatus status = new AssaultStatus(AssaultStatus.PoliticalState.PEACE);

        @Override
        public AssaultStatus status() {
            return status;
        }

        @Override public String id()                      { throw new UnsupportedOperationException(); }
        @Override public int health()                     { throw new UnsupportedOperationException(); }
        @Override public void setHealth(int health)       { throw new UnsupportedOperationException(); }
        @Override public boolean hit()                    { throw new UnsupportedOperationException(); }
        @Override public Barrier barrier()                { throw new UnsupportedOperationException(); }
        @Override public WarState attacker()              { throw new UnsupportedOperationException(); }
        @Override public War war()                        { throw new UnsupportedOperationException(); }
        @Override public AttackWait waitAPI()             { throw new UnsupportedOperationException(); }
        @Override public boolean active()                 { throw new UnsupportedOperationException(); }
        @Override public void setActive(boolean boolean_) { throw new UnsupportedOperationException(); }

    }
    
    @AllArgsConstructor
    @ToString @EqualsAndHashCode
    class Prepare implements AssaultRuntime {
        
        private final static AssaultStatus status = new AssaultStatus(AssaultStatus.PoliticalState.PREPARE);
        private final Barrier barrier;
        private final WarState attacker;
        private final War war;
        private final AttackWait waitAPI;
        private final String id;
        
        @Override public AssaultStatus status()         { return status;                   }
        @Override public Barrier            barrier  () { return this.barrier;             }
        @Override public WarState           attacker () { return this.attacker.warActor(); }
        @Override public War                war      () { return this.war;                 }
        @Override public AttackWait         waitAPI  () { return this.waitAPI;             }
        @Override public String             id       () { return this.id;                  }

        @Override public int health()                     { throw new UnsupportedOperationException(); }
        @Override public void setHealth(int health)       { throw new UnsupportedOperationException(); }
        @Override public boolean hit()                    { throw new UnsupportedOperationException(); }
        @Override public boolean active()                 { throw new UnsupportedOperationException(); }
        @Override public void setActive(boolean boolean_) { throw new UnsupportedOperationException(); }

    }

    @Getter
    @ToString @EqualsAndHashCode
    class Realized implements AssaultRuntime {
        
        private final static AssaultStatus status = new AssaultStatus(AssaultStatus.PoliticalState.BATTLE);
        private final Barrier barrier;
        private final WarState attacker;
        private final War war;
        private final AttackWait waitAPI;
        private final String id;

        @EqualsAndHashCode.Exclude
        private final AtomicInteger health = new AtomicInteger();

        private volatile boolean active = true;

        public Realized(int health, Barrier barrier, WarState attacker, War war, AttackWait wait, String id) {
            this.health.set(health);
            this.barrier = barrier;
            this.attacker = attacker;
            this.war = war;
            this.waitAPI = wait;
            this.id = id;
        }

        @Override
        public boolean hit() {
            this.health.set(this.health.get() - 1);
            if (this.health.get() <= 0) {
                this.active = false;
                return true;
            }
            return false;
        }

        @Override
        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public void setHealth(int health) {
            this.health.set(health);
        }

        @Override public int                health   () { return this.health.get();        }
        @Override public AssaultStatus      status()    { return status;                   }
        @Override public Barrier            barrier  () { return this.barrier;             }
        @Override public WarState           attacker () { return this.attacker.warActor(); }
        @Override public War                war      () { return this.war;                 }
        @Override public AttackWait         waitAPI  () { return this.waitAPI;             }
        @Override public boolean            active   () { return false;                    }
        @Override public String             id       () { return this.id;                  }
        
    }

}