package prison.nf.economy.commands.server;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import prison.nf.economy.EconomyPlugin;
import prison.nf.economy.Messages;
import prison.nf.economy.commands.commandtypes.ServerCommand;
import prison.nf.economy.exceptions.AccountNotFoundException;
import prison.nf.economy.Economy;
import prison.nf.economy.datatypes.Account;
import prison.nf.economy.datatypes.Transaction;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.List;

public class AccountCommand extends ServerCommand
{
    public AccountCommand(EconomyPlugin plugin)
    {
        super(plugin, "account");
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: eco account <player> [limit]" + ChatColor.RESET);
            return;
        }

        long limit = 5;
        if (args.length > 1) {
            try {
                limit = Long.parseLong(args[1]);
                if (limit < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                Messages.Errors.InvalidLimit(limit).sendTo(sender);
                return;
            }
        }

        Economy economy = Economy.getInstance();
        if (economy == null) {
            Messages.Errors.LoadFailure().sendTo(sender);
            Messages.Errors.Server.DataStoreNotLoaded().sendToServer();
            return;
        }

        @SuppressWarnings("deprecation") OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (!player.hasPlayedBefore()) {
            Messages.Errors.AccountNotFoundForPlayer(args[0]).sendTo(sender);
            return;
        }

        try {
            Account account = economy.getAccount(player);
            if (account == null) {
                Messages.Errors.AccountNotFoundForPlayer(player.getName()).sendTo(sender);
                return;
            }

            Messages.PlayerBalance(
                player.getName(),
                Messages.Currency.Formatted(account.accountBalance)
            ).sendTo(sender);
            new Messages.Message("").sendTo(sender);

            List<Transaction> transactions = economy.getTransactions(player, limit);

            Messages.TransactionList.Header(limit, player.getName()).sendTo(sender);

            if (transactions.size() < 1) {
                Messages.TransactionList.NoTransactions().sendTo(sender);
                return;
            }

            for(Transaction transaction : transactions) {
                Account fromAccount = transaction.getFromAccount(economy);
                Account toAccount = transaction.getToAccount(economy);

                @Nullable OfflinePlayer fromPlayer = fromAccount == null ? null : fromAccount.getPlayer();
                OfflinePlayer toPlayer = toAccount.getPlayer();

                String fromAccountName = fromPlayer != null
                    ? fromPlayer.getName()
                    : ChatColor.GOLD + "Administrator" + ChatColor.RESET;
                String toAccountName = ChatColor.BLUE + toPlayer.getName() + ChatColor.RESET;

                String valueString = Messages.Currency.Formatted(transaction.amount);
                if (
                    (
                        fromPlayer != null && fromPlayer.getUniqueId() == player.getUniqueId()
                    ) || transaction.amount < 0
                ) {
                    valueString = Messages.Currency.NegativeFormatted(transaction.amount);
                }

                Messages.TransactionList.Transaction.Detail(
                    transaction.createdAt,
                    fromAccountName,
                    toAccountName,
                    valueString
                ).sendTo(sender);
                Messages.TransactionList.Transaction.Memo(
                    transaction.memo
                ).sendTo(sender);
            }
        } catch (AccountNotFoundException e) {
            Messages.Errors.AccountNotFoundForPlayer(args[0]).sendTo(sender);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
