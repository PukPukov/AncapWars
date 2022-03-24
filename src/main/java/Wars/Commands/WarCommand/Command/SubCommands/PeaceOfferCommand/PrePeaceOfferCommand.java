package Wars.Commands.WarCommand.Command.SubCommands.PeaceOfferCommand;

import AncapLibrary.Commands.AncapCommand;
import AncapLibrary.Commands.AncapPreCommand;
import AncapLibrary.Player.AncapPlayer;
import Wars.Commands.WarCommand.Command.SubCommands.WarStateTargeted;
import Wars.WarPlayers.AncapWarrior;
import Wars.WarStates.WarState;

public class PrePeaceOfferCommand implements AncapPreCommand, WarStateTargeted {

    private AncapWarrior sender;
    private String[] args;

    public PrePeaceOfferCommand(AncapWarrior sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    @Override
    public AncapCommand getPreparedCommand() {
        this.validate();
        WarState state = this.sender.getHighestWarState();
        return new PeaceOfferCommand(this.sender, state);
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
        return this.sender;
    }
}
