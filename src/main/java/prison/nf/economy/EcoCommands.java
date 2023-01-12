package prison.nf.economy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import prison.nf.economy.commands.commandtypes.PlayerCommand;
import prison.nf.economy.commands.commandtypes.ServerCommand;
import prison.nf.economy.commands.server.AccountCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class EcoCommands implements CommandExecutor
{
    private static final ArrayList<PlayerCommand> playerCommands = new ArrayList<>();
    private static final ArrayList<ServerCommand> serverCommands = new ArrayList<>();

    public static void register(EconomyPlugin plugin) {
        // Register Player Commands
        playerCommands.add(new prison.nf.economy.commands.player.BalanceCommand(plugin));
        playerCommands.add(new prison.nf.economy.commands.player.SendCommand(plugin));
        playerCommands.add(new prison.nf.economy.commands.player.AccountCommand(plugin));

        // Register Server Commands
        serverCommands.add(new prison.nf.economy.commands.server.BalanceCommand(plugin));
        serverCommands.add(new prison.nf.economy.commands.server.SendCommand(plugin));
        serverCommands.add(new prison.nf.economy.commands.server.TransferCommand(plugin));
        serverCommands.add(new AccountCommand(plugin));

        Objects.requireNonNull(plugin.getCommand("eco")).setExecutor(new EcoCommands());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player) {
            return onPlayerCommand((Player)sender, args);
        }

        return onServerCommand(sender, args);
    }

    public boolean onServerCommand(CommandSender sender, String[] args) {
        for (ServerCommand sCommand : serverCommands) {
            if (sCommand.getName().equalsIgnoreCase(args[0])) {
                sCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }

        return false;
    }

    public boolean onPlayerCommand(Player player, String[] args) {
        for (PlayerCommand pCommand : playerCommands) {
            if (pCommand.getName().equalsIgnoreCase(args[0])) {
                pCommand.execute(player, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }

        return false;
    }
}
