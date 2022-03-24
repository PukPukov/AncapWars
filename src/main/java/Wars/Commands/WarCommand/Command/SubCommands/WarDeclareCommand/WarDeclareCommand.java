package Wars.Commands.WarCommand.Command.SubCommands.WarDeclareCommand;

import AncapLibrary.Commands.AncapCommand;
import AncapLibrary.Commands.AncapCommandException;
import Wars.AncapWars.AncapWars;
import Wars.WarPlayers.AncapWarrior;
import Wars.WarStates.WarState;

public class WarDeclareCommand implements AncapCommand {

    private AncapWarrior warrior;
    private WarState target;

    public WarDeclareCommand(AncapWarrior warrior, WarState state) {
        this.warrior = warrior;
        this.target = state;
    }

    @Override
    public void handle() {
        this.validate();
        warrior.getHighestWarState().declareWar(target);
    }

    private void validate() {
        if (warrior.isFree()) {
            throw new AncapCommandException(warrior, AncapWars.getConfiguration().getFreeMessage());
        }
        if (!warrior.canDeclareWars()) {
            throw new AncapCommandException(warrior, AncapWars.getConfiguration().getNoPermissionMessage());
        }
        if (!target.isNeutral()) {
            throw new AncapCommandException(warrior, AncapWars.getConfiguration().getNeutralWarStateErrorMessage(target.getName()));
        }
    }
}
