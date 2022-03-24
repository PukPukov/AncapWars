package Wars.Commands.WarCommand.Command.SubCommands.PeaceOfferCommand;

import AncapLibrary.Commands.AncapCommand;
import AncapLibrary.Commands.AncapCommandException;
import Wars.AncapWars.AncapWars;
import Wars.WarPlayers.AncapWarrior;
import Wars.WarStates.WarState;

public class PeaceOfferCommand implements AncapCommand {

    private AncapWarrior sender;
    private WarState target;

    public PeaceOfferCommand(AncapWarrior sender, WarState target) {
        this.sender = sender;
        this.target = target;
    }

    @Override
    public void handle() {
        this.validate();
    }

    private void validate() {
        if (this.sender.isFree()) {
            throw new AncapCommandException(sender, AncapWars.getConfiguration().getFreeMessage());
        }
        if (!sender.canOfferPeace()) {
            throw new AncapCommandException(sender, AncapWars.getConfiguration().getNoPermissionMessage());
        }
    }
}
