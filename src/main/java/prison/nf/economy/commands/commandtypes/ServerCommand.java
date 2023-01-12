package prison.nf.economy.commands.commandtypes;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import prison.nf.economy.EconomyPlugin;

public abstract class ServerCommand extends Command<CommandSender>
{
    public ServerCommand(EconomyPlugin plugin, String name)
    {
        super(plugin, name);
    }
}
