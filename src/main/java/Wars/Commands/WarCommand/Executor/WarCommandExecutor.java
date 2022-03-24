package Wars.Commands.WarCommand.Executor;

import AncapLibrary.Commands.AncapCommandException;
import Wars.Commands.WarCommand.Command.WarCommand;
import Wars.WarPlayers.AncapWarrior;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WarCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        AncapWarrior warrior = new AncapWarrior(sender.getName());
        WarCommand warCommand = new WarCommand(warrior, args);
        try {
            warCommand.handle();
        } catch (AncapCommandException e) {
            e.handle();
        }
        return true;
    }
}
