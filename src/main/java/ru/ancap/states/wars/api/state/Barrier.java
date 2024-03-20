package ru.ancap.states.wars.api.state;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import ru.ancap.framework.communicate.message.CallableMessage;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.library.Balance;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.assault.AttackWait;
import ru.ancap.states.wars.plugin.listener.AssaultRuntime;

import java.util.List;

public interface Barrier {
    
    void delete();
    int protectLevel();
    WarHexagon hexagon();
    
    Balance attackFee();
    CallableMessage attackMessage(WarState attacker, WarState defender);
    
    void acceptTimedAttack(AttackWait wait, AssaultRuntime prepareRuntime, List<WarHexagon> toSet);
    
    default WarState owner() {
        return this.hexagon().getOwner();
    }

    AttackWait attackWait();
    Location location();
    PathDatabase database();

    @Nullable CallableMessage deleteMessage(WarState destroyer);

    default boolean canBeAttackedBy(WarState attacker) {
        WarState owner = this.hexagon().getOwner();
        if (owner == null) return false;
        WarHexagon hexagon = this.hexagon();
        if (hexagon.neighbors(1).stream().anyMatch(neighbor -> attacker.containsHex(new WarHexagon(neighbor.code())))) return true;
        if (hexagon.getPathsTo(attacker).size() >= 1) return true;
        /* boolean[] caught = new boolean[]{false};
        new Walkthrough<>(this.getHexagon().getHexagon(), new WalkthroughOperator<Hexagon, Integer>() {
            @Override
            public StepResult<Integer> step(Hexagon baseHexagon, WalkthroughData<Hexagon, Integer> walkthroughData) {
                WarHexagon hexagon = new WarHexagon(baseHexagon.code());
                if (walkthroughData.customData() >= 2) return StepResult.DENY();
                if (owner.containsHex(hexagon)) {
                    caught[0] = true;
                    return StepResult.END_WALKTHROUGH();
                }
                if (hexagon.getCastle() != null) return StepResult.DENY();
                return new StepResult.Allow<>(walkthroughData.customData()+1);
            }

            @Override
            public Integer initialData(Hexagon ignoredByDefaultValue) {
                return 0;
            }
        }, HexagonMethodApplier.INSTANCE).walkthrough();
        if (!caught[0]) return false;*/
        return false;
    }
    
}
