package Wars.Commands.WarCommand.Command;

import AncapLibrary.Commands.AncapCommand;
import AncapLibrary.Commands.AncapCommandException;
import AncapLibrary.Commands.AncapPreCommand;
import AncapLibrary.Library.AncapLibrary;
import AncapLibrary.Player.AncapPlayer;
import Wars.Commands.WarCommand.Command.SubCommands.PeaceOfferCommand.PrePeaceOfferCommand;
import Wars.Commands.WarCommand.Command.SubCommands.WarDeclareCommand.PreWarDeclareCommand;
import Wars.Commands.WarCommand.Command.SubCommands.WarStartCommand.PreWarStartCommand;
import Wars.WarPlayers.AncapWarrior;

import java.util.Collections;

public class WarCommand implements AncapCommand, AncapPreCommand {

    private AncapWarrior warrior;
    private String[] args;

    public WarCommand(AncapWarrior warrior, String[] args) {
        this.warrior = warrior;
        this.args = args;
    }

    public void handle() {
        AncapCommand command = this.getPreparedCommand();
        command.handle();
    }

    @Override
    public AncapCommand getPreparedCommand() {
        this.validate();
        if (this.args[0].equals("declare")) {
            return new PreWarDeclareCommand(this.warrior, this.args).getPreparedCommand();
        }
        if (this.args[0].equals("peace")) {
            return new PrePeaceOfferCommand(this.warrior, this.args).getPreparedCommand();
        }
        if (this.args[0].equals("start346")) {
            return new PreWarStartCommand(this.warrior, this.args).getPreparedCommand();
        }
        throw new AncapCommandException(this.warrior, AncapLibrary.getConfiguration().getInvalidArgMessage(args[0], Collections.singletonList("declare, peace")));
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
        return this.warrior;
    }
}
