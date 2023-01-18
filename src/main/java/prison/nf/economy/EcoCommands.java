package prison.nf.economy;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import prison.nf.commands.Command;
import prison.nf.commands.Executor;
import prison.nf.commands.exceptions.CommandExecutionException;
import prison.nf.economy.commands.server.AccountCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EcoCommands extends Executor
{
    private final EconomyPlugin plugin;

    public EcoCommands(EconomyPlugin plugin)
    {
        super();
        this.plugin = plugin;
    }

    @Override
    public List<Command<? extends CommandSender>> getCommands()
    {
        return new ArrayList<Command<? extends CommandSender>>() {{
            add(new prison.nf.economy.commands.player.BalanceCommand(plugin));
            add(new prison.nf.economy.commands.player.SendCommand(plugin));
            add(new prison.nf.economy.commands.player.AccountCommand(plugin));

            // Register Server Commands
            add(new prison.nf.economy.commands.server.BalanceCommand(plugin));
            add(new prison.nf.economy.commands.server.SendCommand(plugin));
            add(new prison.nf.economy.commands.server.TransferCommand(plugin));
            add(new prison.nf.economy.commands.server.AccountCommand(plugin));
        }};
    }

    @Override
    public void onCommandExecutionException(CommandSender sender, CommandExecutionException e, Command<? extends CommandSender> command)
    {
        ArrayList<String> messages = new ArrayList<>();
        messages.add("Error: " + e.getMessage());
        messages.add("Usage: " + command.getName() + " " + command.getUsage());

        String[] output = new String[messages.size()];
        messages.toArray(output);

        sender.sendMessage(output);
    }

    @Override
    public void onUnknownCommand(CommandSender sender, String label, String input, List<Command<? extends CommandSender>> potentials)
    {
        List<String> messages = new ArrayList<>(potentials.stream().map(c -> c.getName() + " " + c.getUsage()).toList());
        messages.add(0, "Potential Commands: ");
        messages.add(0, "Unknown command: " + label + " " + input);
        String[] output = new String[messages.size()];
        messages.toArray(output);
        sender.sendMessage(output);
    }
}
