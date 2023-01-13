package prison.nf.economy.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import prison.nf.economy.EconomyPlugin;
import prison.nf.economy.Messages;
import prison.nf.economy.commands.commandtypes.PlayerCommand;
import prison.nf.economy.exceptions.AccountNotFoundException;
import prison.nf.economy.exceptions.InsufficientAccountBalanceException;
import prison.nf.economy.exceptions.InvalidTransactionAmountException;
import prison.nf.economy.Economy;

import java.sql.SQLException;
import java.util.Arrays;

public class SendCommand extends PlayerCommand
{
    public SendCommand(EconomyPlugin plugin)
    {
        super(plugin, "send");
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (args.length < 2)
        {
            player.sendMessage(ChatColor.RED + "Usage: /eco send <player> <amount> [memo]" + ChatColor.RESET);
            return;
        }

        String memo = getPlugin().getConfig().getString("Transactions.DefaultTransferMemo", "General transfer");
        if (args.length > 2) {
            memo = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Usage: eco transfer <from-player> <to-player> <amount> [memo]" + ChatColor.RESET);
            return;
        }
        if (amount <= 0) {
            Messages.Errors.TransactionAmountNegative().sendTo(player);
            return;
        }

        @SuppressWarnings("deprecation") OfflinePlayer toPlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!toPlayer.hasPlayedBefore()) {
            Messages.Errors.AccountNotFoundForPlayer(args[0]).sendTo(player);
            return;
        }

        if (player.getUniqueId() == toPlayer.getUniqueId()) {
            Messages.Errors.SameAccountTransfer().sendTo(player);
            return;
        }

        Economy economy = Economy.getInstance();
        if (economy == null) {
            Messages.Errors.LoadFailure().sendTo(player);
            Messages.Errors.Server.DataStoreNotLoaded().sendToServer();
            return;
        }

        try {
            economy.transfer(player, toPlayer, amount, memo);

            Messages.Transfer.Sent(
                    ChatColor.BLUE + toPlayer.getName() + ChatColor.RESET,
                    Messages.Currency.Formatted(amount)
            ).sendTo(player);

            if (toPlayer.isOnline()) {
                Player onlineToPlayer = toPlayer.getPlayer();
                if (onlineToPlayer != null) {
                    Messages.Transfer.Received(
                        ChatColor.BLUE + player.getName() + ChatColor.RESET,
                        Messages.Currency.Formatted(amount)
                    ).sendTo(onlineToPlayer);
                }
            }
        } catch (AccountNotFoundException e) {
            Messages.Errors.AccountsNotFound().sendTo(player);
        } catch (InvalidTransactionAmountException e) {
            Messages.Errors.TransactionAmountNegative().sendTo(player);
        } catch (InsufficientAccountBalanceException e) {
            Messages.Errors.InsufficientAccountBalance().sendTo(player);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
