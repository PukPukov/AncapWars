package Wars.Commands.CastleCommand.Command;

import AncapLibrary.Commands.AncapCommand;
import AncapLibrary.Commands.AncapCommandException;
import AncapLibrary.Commands.AncapPreCommand;
import AncapLibrary.Library.AncapLibrary;
import AncapLibrary.Player.AncapPlayer;
import Wars.WarPlayers.AncapWarrior;

import java.util.Collections;

public class CastleCommand implements AncapCommand, AncapPreCommand {

    private AncapWarrior sender;
    private String[] args;

    public CastleCommand(AncapWarrior sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    @Override
    public void handle() {
        this.getPreparedCommand().handle();
    }

    @Override
    public AncapCommand getPreparedCommand() {
        this.validate();
        String name = args[0];
        if (name.equals("create")) {
            return new PreCastleCreateCommand(sender, args).getPreparedCommand();
        }
        throw new AncapCommandException(sender, AncapLibrary.getConfiguration().getInvalidArgMessage(name, Collections.singletonList("create")));
    }

    private void validate() {
        this.validateArgsCount(1);
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
