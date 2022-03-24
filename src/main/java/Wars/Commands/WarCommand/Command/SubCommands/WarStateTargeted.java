package Wars.Commands.WarCommand.Command.SubCommands;

import AncapLibrary.Commands.AncapCommandException;
import AncapLibrary.Commands.AncapPreCommand;
import AncapLibrary.Library.AncapLibrary;
import AncapLibrary.Message.Message;
import Wars.AncapWars.AncapWars;
import Wars.WarStates.WarState;
import Wars.WarStates.WarStateMap.UnknownWarStateException;
import Wars.WarStates.WarStateMap.UnknownWarStateTypeException;
import Wars.WarStates.WarStateType;

import java.util.Collections;

public interface WarStateTargeted extends AncapPreCommand {

    default WarState getWarState() {
        WarStateType type = this.getWarStateType();
        String name = this.getArgs()[2];
        try {
            return AncapWars.getWarStateMap().findWarState(type, name);
        } catch (UnknownWarStateException e) {
            Message errorMessage = AncapWars.getConfiguration().getUnknownWarStateMessage(name);
            throw new AncapCommandException(this.getSender(), errorMessage);
        }
    }

    default WarStateType getWarStateType() {
        String typeName = getArgs()[1];
        try {
            return AncapWars.getWarStateMap().findWarStateType(typeName);
        } catch (UnknownWarStateTypeException e) {
            Message errorMessage = AncapLibrary.getConfiguration().getInvalidArgMessage(typeName, Collections.singletonList("city, nation"));
            throw new AncapCommandException(this.getSender(), errorMessage);
        }
    }
}
