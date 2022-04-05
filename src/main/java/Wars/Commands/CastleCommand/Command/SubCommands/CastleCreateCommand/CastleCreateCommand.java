package Wars.Commands.CastleCommand.Command.SubCommands.CastleCreateCommand;

import AncapLibrary.Commands.AncapCommand;
import AncapLibrary.Commands.AncapCommandException;
import AncapLibrary.Library.AncapLibrary;
import Wars.AncapWars.AncapWars;
import Wars.Building.Castle.CastleAlreadyCreatedException;
import Wars.Building.Castle.CastleNameAlreadyUsedException;
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
        } catch (CastleNameAlreadyUsedException e) {
            throw new AncapCommandException(warrior, AncapWars.getConfiguration().getNameOccupiedMessage());
        }
    }

    private void validate() {
        if (this.warrior.isFree()) {
            throw new AncapCommandException(warrior, AncapWars.getConfiguration().getFreeMessage());
        }
        if (!this.warrior.isAssistant() && !this.warrior.isMayor()) {
            throw new AncapCommandException(warrior, AncapLibrary.getConfiguration().getNoPermissionMessage());
        }
        if (!this.warrior.getWarCity().haveCastleCreationFee()) {
            throw new AncapCommandException(warrior, AncapWars.getConfiguration().getNotEnoughMoneyMessage());
        }
        if (!this.warrior.getCity().equals(this.warrior.getCityAtPosition())) {
            throw new AncapCommandException(warrior, AncapWars.getConfiguration().getNotYoursCityMessage());
        }
    }
}
