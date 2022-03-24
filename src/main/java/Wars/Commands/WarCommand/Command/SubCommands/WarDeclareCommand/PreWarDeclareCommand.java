package Wars.Commands.WarCommand.Command.SubCommands.WarDeclareCommand;

import AncapLibrary.Commands.AncapPreCommand;
import AncapLibrary.Player.AncapPlayer;
import Wars.Commands.WarCommand.Command.SubCommands.WarStateTargeted;
import Wars.WarPlayers.AncapWarrior;
import Wars.WarStates.WarState;

public class PreWarDeclareCommand implements AncapPreCommand, WarStateTargeted {

    private AncapWarrior warrior;
    private String[] args;

    public PreWarDeclareCommand(AncapWarrior warrior, String[] args) {
        this.warrior = warrior;
        this.args = args;
    }

    @Override
    public WarDeclareCommand getPreparedCommand() {
        this.validate();
        WarState state = this.getWarState();
        return new WarDeclareCommand(this.warrior, state);
    }

    private void validate() {
        this.validateArgsCount(3);
    }

    @Override
    public String[] getArgs() {
        return this.args;
    }

    @Override
    public AncapPlayer getSender() {
        return this.warrior;
    }
}
