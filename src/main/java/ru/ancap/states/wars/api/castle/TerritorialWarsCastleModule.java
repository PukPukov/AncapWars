package ru.ancap.states.wars.api.castle;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TerritorialWarsCastleModule /* implements WalkthroughOperator<Hexagon, TerritorialWarsCastleModule.Steps> */ {
    
    /*
    private final WarState attacker;
    private final int maxSteps;
    
    private final Mark pathExists;

    @Override
    public Steps initialData(Hexagon base) {
        return new Steps(0);
    }
    
    @Override
    public StepResult<Steps> step(Hexagon hexagon, WalkthroughData<Hexagon, Steps> walkthroughData) {
        WarHexagon warHexagon = new WarHexagon(hexagon.code());
        if (this.attacker.equals(warHexagon.getOwner())) {
            this.pathExists.mark();
            return StepResult.END_WALKTHROUGH();
        }
        if (warHexagon.barrier() != null || walkthroughData.customData().steps() > this.maxSteps) return StepResult.DENY();
        return new StepResult.Allow<>(walkthroughData.customData().incremented());
    }

    @With
    public record Steps(int steps) {
        
        public Steps incremented() {
            return this.withSteps(this.steps+1);
        }
        
    }
    */
    
}