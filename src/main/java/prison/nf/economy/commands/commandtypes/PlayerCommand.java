package prison.nf.economy.commands.commandtypes;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import prison.nf.economy.EconomyPlugin;

public abstract class PlayerCommand extends Command<Player>
{
    public PlayerCommand(EconomyPlugin plugin, String name)
    {
        super(plugin, name);
    }
}
