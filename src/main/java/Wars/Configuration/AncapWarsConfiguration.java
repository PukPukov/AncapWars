package Wars.Configuration;

import AncapLibrary.Economy.Balance;
import AncapLibrary.Economy.ParsedStringPreBalance;
import AncapLibrary.Message.Message;
import AncapLibrary.StringParser.ParsedString;
import AncapLibrary.StringParser.PreParsedString;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

public class AncapWarsConfiguration {

    private FileConfiguration configuration;

    public AncapWarsConfiguration(FileConfiguration config) {
        this.configuration = config;
    }

    public FileConfiguration getConfiguration() {
        return this.configuration;
    }

    public WarMessage getMessage(String path) {
        WarMessage message = new WarMessage(new String[]{this.configuration.getString(path)});
        message.format();
        return message;
    }

    public Balance getBalance(String path) {
        String data = this.configuration.getString(path);
        ParsedString string = (new PreParsedString(data)).parse(":", ";");
        return (new ParsedStringPreBalance(string)).getPreparedBalance();
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

    public Message getNotEnoughMoneyMessage() {
        Message message = this.getMessage("not_enough_money");
        return message;
    }

    public Message getNotYoursCityMessage() {
        Message message = this.getMessage("not_yours_city");
        return message;
    }

    public Message getNameOccupiedMessage() {
        Message message = this.getMessage("name_occupied");
        return message;
    }

    public Sound getWarDeclareSound() {
        return Sound.EVENT_RAID_HORN;
    }

    public Sound getHeartBreakSound() {
        return Sound.ENTITY_WITHER_HURT;
    }

    public Sound getCastleBreakSound() {
        return Sound.ENTITY_ENDER_DRAGON_DEATH;
    }

    public Sound getAttackRepulsedSound() {
        return Sound.BLOCK_BEACON_ACTIVATE;
    }



    public Message getWarDeclareMessage(String name, String name1) {
        Message message = this.getMessage("war_declare_message");
        message.replace("%attacker%", name);
        message.replace("%attacked%", name1);
        return message;
    }

    public Message getAlreadyInWarMessage(String name) {
        Message message = this.getMessage("already_in_war");
        message.replace("%name%", name);
        return message;
    }

    public Message getLeaveHexagonPleaseMessage() {
        Message message = this.getMessage("leave_pls");
        return message;
    }

    public Message getAttackRepulsedMessage() {
        Message message = this.getMessage("attack_repulsed");
        return message;
    }

    public Message getBattleLoseMessage() {
        Message message = this.getMessage("lose_battle");
        return message;
    }

    public int getCastleCoreHealth() {
        return 100;
    }
}
