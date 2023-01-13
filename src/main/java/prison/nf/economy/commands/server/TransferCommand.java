package prison.nf.economy.commands.server;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import prison.nf.economy.EconomyPlugin;
import prison.nf.economy.Messages;
import prison.nf.economy.commands.commandtypes.ServerCommand;
import prison.nf.economy.exceptions.AccountNotFoundException;
import prison.nf.economy.exceptions.InsufficientAccountBalanceException;
import prison.nf.economy.exceptions.InvalidTransactionAmountException;
import prison.nf.economy.Economy;

import java.sql.SQLException;
import java.util.Arrays;

public class TransferCommand extends ServerCommand
{
    public TransferCommand(EconomyPlugin plugin)
    {
        super(plugin, "transfer");
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (args.length < 3)
        {
            sender.sendMessage(ChatColor.RED + "Usage: eco transfer <from-player> <to-player> <amount> [memo]" + ChatColor.RESET);
            return;
        }

        String memo = getPlugin().getConfig().getString("Transactions.DefaultTransferMemo", "General transfer");
        if (args.length > 3) {
            memo = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Usage: eco transfer <from-player> <to-player> <amount> [memo]" + ChatColor.RESET);
            return;
        }
        if (amount <= 0) {
            Messages.Errors.TransactionAmountNegative().sendTo(sender);
            return;
        }

        @SuppressWarnings("deprecation") OfflinePlayer fromPlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!fromPlayer.hasPlayedBefore()) {
            Messages.Errors.AccountNotFoundForPlayer(args[0]).sendTo(sender);
            return;
        }

        @SuppressWarnings("deprecation") OfflinePlayer toPlayer = Bukkit.getOfflinePlayer(args[1]);
        if (!toPlayer.hasPlayedBefore()) {
            Messages.Errors.AccountNotFoundForPlayer(args[0]).sendTo(sender);
            return;
        }

        if (fromPlayer.getUniqueId() == toPlayer.getUniqueId()) {
            Messages.Errors.SameAccountTransfer().sendTo(sender);
            return;
        }

        Economy economy = Economy.getInstance();
        if (economy == null) {
            Messages.Errors.LoadFailure().sendTo(sender);
            Messages.Errors.Server.DataStoreNotLoaded().sendToServer();
            return;
        }

        try {
            economy.transfer(fromPlayer, toPlayer, amount, memo);

            if (fromPlayer.isOnline()) {
                Player player = fromPlayer.getPlayer();
                if (player != null) {
                    Messages.Transfer.Admin(
                        ChatColor.BLUE + "your account" + ChatColor.RESET,
                        ChatColor.BLUE + toPlayer.getName() + ChatColor.RESET,
                        Messages.Currency.Formatted(amount)
                    ).sendTo(player);
                }
            }

            if (toPlayer.isOnline()) {
                Player player = toPlayer.getPlayer();
                if (player != null) {
                    Messages.Transfer.Admin(
                        ChatColor.BLUE + toPlayer.getName() + ChatColor.RESET,
                        ChatColor.BLUE + "your account" + ChatColor.RESET,
                        Messages.Currency.Formatted(amount)
                    ).sendTo(player);
                }
            }

            Messages.Transfer.Transferred(
                    ChatColor.BLUE + fromPlayer.getName() + ChatColor.RESET,
                    ChatColor.BLUE + toPlayer.getName() + ChatColor.RESET,
                    Messages.Currency.Formatted(amount)
            ).sendTo(sender);
        } catch (AccountNotFoundException e) {
            Messages.Errors.AccountsNotFound().sendTo(sender);
        } catch (InvalidTransactionAmountException e) {
            Messages.Errors.TransactionAmountNegative().sendTo(sender);
        } catch (InsufficientAccountBalanceException e) {
            Messages.Errors.InsufficientAccountBalance().sendTo(sender);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
