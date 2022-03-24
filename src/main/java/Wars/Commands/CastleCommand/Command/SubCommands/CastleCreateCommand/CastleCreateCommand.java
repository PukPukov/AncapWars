package Wars.Commands.CastleCommand.Command.SubCommands.CastleCreateCommand;

import AncapLibrary.Commands.AncapCommand;
import AncapLibrary.Commands.AncapCommandException;
import Wars.AncapWars.AncapWars;
import Wars.Building.Castle.CastleAlreadyCreatedException;
import Wars.WarPlayers.AncapWarrior;

public class CastleCreateCommand implements AncapCommand {

    private AncapWarrior warrior;
    private String name;

    public CastleCreateCommand(AncapWarrior warrior, String name) {
        this.warrior = warrior;
        this.name = name;
    }

    @Override
    public void handle() {
        this.validate();
        try {
            this.warrior.createCastle(name);
        } catch (CastleAlreadyCreatedException e) {
            throw new AncapCommandException(warrior, AncapWars.getConfiguration().getCastleAlreadyCreatedMessage());
        }
    }

    private void validate() {
        if (this.warrior.isFree()) {
            throw new AncapCommandException(warrior, AncapWars.getConfiguration().getFreeMessage());
        }
        if (!this.warrior.isAssistant()) {
            throw new AncapCommandException(warrior, AncapWars.getConfiguration().getNoPermissionMessage());
        }
        if (!this.warrior.getWarCity().haveCastleCreationFee()) {
            throw new AncapCommandException(warrior, AncapWars.getConfiguration().getNoPermissionMessage());
        }
    }
}
