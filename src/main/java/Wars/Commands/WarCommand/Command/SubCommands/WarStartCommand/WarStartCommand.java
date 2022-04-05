package Wars.Commands.WarCommand.Command.SubCommands.WarStartCommand;

import AncapLibrary.Commands.AncapCommand;
import AncapLibrary.Commands.AncapCommandException;
import AncapLibrary.Library.AncapLibrary;
import Wars.AncapWars.AncapWars;
import Wars.WarHexagons.WarHexagon;
import Wars.WarHexagons.WarHexagonStatus;
import Wars.WarPlayers.AncapWarrior;

public class WarStartCommand implements AncapCommand {

    private AncapWarrior warrior;

    public WarStartCommand(AncapWarrior warrior) {
        this.warrior = warrior;
    }

    @Override
    public void handle() {
        this.validate();
        WarHexagon hexagon = warrior.getHexagon();
        hexagon.startKostilBattle(warrior.getWarLocation());
    }

    private void validate() {
        if (warrior.isFree()) {
            throw new AncapCommandException(warrior, AncapWars.getConfiguration().getFreeMessage());
        }
        if (!warrior.canDeclareWars()) {
            throw new AncapCommandException(warrior, AncapLibrary.getConfiguration().getNoPermissionMessage());
        }
        if (warrior.getHexagon().getStatus() == WarHexagonStatus.KOSTIL_PREBATTLE || warrior.getHexagon().getStatus() == WarHexagonStatus.SIEGE) {
            throw new AncapCommandException(warrior, AncapLibrary.getConfiguration().getNoPermissionMessage());
        }
    }
}
