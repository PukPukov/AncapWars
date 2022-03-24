package Wars.Commands.CastleCommand.Executor;

import AncapLibrary.Commands.AncapCommandException;
import Wars.Commands.CastleCommand.Command.CastleCommand;
import Wars.WarPlayers.AncapWarrior;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CastleCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        AncapWarrior warrior = new AncapWarrior(sender.getName());
        CastleCommand castleCommand = new CastleCommand(warrior, args);
        try {
            castleCommand.handle();
        } catch (AncapCommandException e) {
            e.handle();
        }
        return true;
    }

}
