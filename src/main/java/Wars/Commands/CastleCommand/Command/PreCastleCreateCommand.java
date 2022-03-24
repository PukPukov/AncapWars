package Wars.Commands.CastleCommand.Command;

import AncapLibrary.Commands.AncapCommand;
import AncapLibrary.Commands.AncapPreCommand;
import AncapLibrary.Player.AncapPlayer;
import Wars.Commands.CastleCommand.Command.SubCommands.CastleCreateCommand.CastleCreateCommand;
import Wars.WarPlayers.AncapWarrior;

public class PreCastleCreateCommand implements AncapPreCommand {

    private AncapWarrior sender;
    private String[] args;

    public PreCastleCreateCommand(AncapWarrior sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    @Override
    public AncapCommand getPreparedCommand() {
        this.validateArgsCount(2);
        return new CastleCreateCommand(this.sender, args[1]);
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
