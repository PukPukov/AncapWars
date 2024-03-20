package ru.ancap.states.wars.api.castle;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import ru.ancap.framework.communicate.communicator.Communicator;
import ru.ancap.framework.communicate.message.CallableMessage;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.library.Balance;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.state.Barrier;
import ru.ancap.states.wars.api.state.CityState;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.assault.AttackWait;
import ru.ancap.states.wars.fees.WarFees;
import ru.ancap.states.wars.plugin.listener.AssaultRuntime;

import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode @ToString
public class CoreBarrier implements Barrier {
    
    private final CityState city;
    private final PathDatabase database;
    
    public static @Nullable CoreBarrier of(CityState city) {
        PathDatabase database = city.getDatabase().inner("core-barrier");
        if (database.isSet("broken")) return null;
        return new CoreBarrier(city, database);
    }
    
    @Override
    public void delete() {
        this.database.write("broken", true);
    }

    @Override
    public int protectLevel() {
        return 1;
    }

    @Override
    public WarHexagon hexagon() {
        return new WarHexagon(this.city.getHomeHexagon().code());
    }

    @Override
    public Balance attackFee() {
        return WarFees.CORE_BARRIER_ATTACK;
    }

    @Override
    public CallableMessage attackMessage(WarState attacker, WarState defender) {
        return new LAPIMessage(AncapWars.class, "barrier.central.scheduled-attack",
            new Placeholder("attacker", attacker.getName()),
            new Placeholder("defender", defender.getName())
        );
    }

    @Override
    public void acceptTimedAttack(AttackWait wait, AssaultRuntime prepareRuntime, List<WarHexagon> toSet) {
        Bukkit.getOnlinePlayers().stream().map(Communicator::of)
            .forEach(communicator -> communicator.message(new LAPIMessage(AncapWars.class, "barrier.central.attack",
                new Placeholder("attacker", wait.getAttacker().getName()),
                new Placeholder("defender", wait.getBarrier().owner().getName())
            )));
        for (WarHexagon hexagon : toSet) AncapWars.assaults().setPeace(hexagon.code());
        this.delete();
    }

    @Override
    public AttackWait attackWait() {
        return new AttackWait(this);
    }

    @Override
    public Location location() {
        return this.city.getHome();
    }

    @Override
    public PathDatabase database() {
        return this.city.getDatabase().inner("assault-wait");
    }

    @Override
    public @Nullable CallableMessage deleteMessage(WarState destroyer) {
        return null;
    }

}
