package prison.nf.economy.commands.commandtypes;

import org.bukkit.command.CommandSender;
import prison.nf.economy.EconomyPlugin;

import java.util.logging.Logger;

public abstract class Command<TSender extends CommandSender>
{
    private final EconomyPlugin plugin;
    private final String name;

    public Command(EconomyPlugin plugin, String name)
    {
        this.plugin = plugin;
        this.name = name;
    }

    public EconomyPlugin getPlugin()
    {
        return plugin;
    }

    public Logger getLogger()
    {
        return plugin.getLogger();
    }

    public String getName()
    {
        return name;
    }

    public abstract void execute(TSender sender, String[] args);
}
