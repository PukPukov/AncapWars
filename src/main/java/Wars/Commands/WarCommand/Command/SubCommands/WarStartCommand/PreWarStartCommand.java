package Wars.Commands.WarCommand.Command.SubCommands.WarStartCommand;

import AncapLibrary.Commands.AncapCommand;
import AncapLibrary.Commands.AncapPreCommand;
import AncapLibrary.Player.AncapPlayer;
import Wars.WarPlayers.AncapWarrior;

public class PreWarStartCommand implements AncapPreCommand {

    private AncapWarrior sender;
    private String[] args;

    public PreWarStartCommand(AncapWarrior warrior, String[] args) {
        this.sender = warrior;
        this.args = args;
    }

    @Override
    public AncapCommand getPreparedCommand() {
        return new WarStartCommand(sender);
    }

    @Override
    public String[] getArgs() {
        return this.args;
    }

    @Override
    public AncapPlayer getSender() {
        return this.sender;
    }
}
