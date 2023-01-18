package prison.nf.economy.commands.server;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import prison.nf.commands.types.ServerCommand;
import prison.nf.economy.EconomyPlugin;
import prison.nf.economy.Messages;
import prison.nf.economy.exceptions.AccountNotFoundException;
import prison.nf.economy.Economy;
import prison.nf.permissions.Permissions;

import java.sql.SQLException;
import java.util.Arrays;

public class SendCommand extends ServerCommand
{
    private final EconomyPlugin plugin;

    public SendCommand(EconomyPlugin plugin)
    {
        super("send");
        this.plugin = plugin;
    }

    @Override
    public String getUsage()
    {
        return "<player> <amount> [memo]";
    }

    @Override
    public int getRequiredArgumentCount()
    {
        return 2;
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Usage: eco send <player-name> <amount> [memo]" + ChatColor.RESET);
            return;
        }

        String memo = amount < 0
            ? plugin.getConfig().getString("Transactions.DefaultWithdrawMemo", "Administrative withdraw")
            : plugin.getConfig().getString("Transactions.DefaultDepositMemo", "Administrative deposit");
        if (args.length > 2) {
            memo = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!offlinePlayer.hasPlayedBefore()) {
            Messages.Errors.AccountNotFoundForPlayer(args[0]).sendTo(sender);
            return;
        }

        Economy economy = Economy.getInstance();
        if (economy == null) {
            Messages.Errors.LoadFailure().sendTo(sender);
            Messages.Errors.Server.DataStoreNotLoaded().sendToServer();
            return;
        }

        try {
            economy.giveMoney(offlinePlayer, amount, memo);

            if (offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();
                if (player != null) {
                    Messages.Transfer.Received(
                        ChatColor.GOLD + "A server administrator" + ChatColor.RESET,
                        Messages.Currency.Formatted(amount)
                    ).sendTo(player);
                }
            }

            Permissions permissions = Permissions.getInstance();
            if (permissions == null) {
                throw new RuntimeException("Permissions not initialized.");
            }

            String displayName = permissions.getDisplayNameFor(offlinePlayer);

            Messages.Transfer.Sent(
                displayName,
                Messages.Currency.Formatted(amount)
            ).sendTo(sender);
        } catch (AccountNotFoundException e) {
            Messages.Errors.AccountNotFoundForPlayer(args[0]).sendTo(sender);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
