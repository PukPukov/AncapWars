package Wars.Configuration;

import AncapLibrary.Configuration.AncapLibraryConfiguration;
import AncapLibrary.Economy.Balance;
import AncapLibrary.Message.Message;
import org.bukkit.configuration.file.FileConfiguration;

public class AncapWarsConfiguration extends AncapLibraryConfiguration {

    public AncapWarsConfiguration(FileConfiguration config) {
        super(config);
    }

    public Message getUnknownWarStateMessage(String name) {
        Message message = this.getMessage("unknown_war_state");
        message.replace("%state%", name);
        return message;
    }

    public Message getNeutralWarStateErrorMessage(String name) {
        Message message = this.getMessage("this_war_state_is_neutral");
        message.replace("%state%", name);
        return message;
    }

    public Message getFreeMessage() {
        Message message = this.getMessage("free");
        return message;
    }

    public Balance getStartCastleCreationFee() {
        return this.getBalance("start_castle_creation_fee");
    }

    public Balance getCastleCreationFeeRaising() {
        return this.getBalance("castle_creation_fee_raising");
    }

    public Balance getCastleMaintenanceFee() {
        return this.getBalance("castle_maintenance_fee");
    }

    public Message getCastleAlreadyCreatedMessage() {
        Message message = this.getMessage("castle_already_created");
        return message;
    }

    public Message getCommandInDevMessage(String date) {
        Message message = this.getMessage("this_command_is_in_dev");
        message.replace("%date%", date);
        return message;
    }

    public Message getCastleCreationMessage(String name, String s) {
        Message message = this.getMessage("castle_create");
        message.replace("%name%", name);
        message.replace("%castle%", s);
        return message;
    }
}
