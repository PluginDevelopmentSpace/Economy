package prison.nf.economy.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import prison.nf.economy.EconomyPlugin;
import prison.nf.economy.Messages;
import prison.nf.economy.commands.commandtypes.PlayerCommand;
import prison.nf.economy.exceptions.AccountNotFoundException;
import prison.nf.economy.Economy;
import prison.nf.economy.datatypes.Account;
import prison.nf.economy.datatypes.Transaction;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.List;

public class AccountCommand extends PlayerCommand
{
    public AccountCommand(EconomyPlugin plugin)
    {
        super(plugin, "account");
    }

    @Override
    public void execute(Player player, String[] args)
    {
        long limit = 5;
        if (args.length > 0) {
            try {
                limit = Long.parseLong(args[0]);
                if (limit < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                Messages.Errors.InvalidLimit(limit).sendTo(player);
                return;
            }
        }


        Economy economy = Economy.getInstance();
        if (economy == null) {
            Messages.Errors.LoadFailure().sendTo(player);
            Messages.Errors.Server.DataStoreNotLoaded().sendToServer();
            return;
        }

        try {
            Account account = economy.getAccount(player);
            if (account == null) {
                Messages.Errors.AccountNotFoundForPlayer(player.getName()).sendTo(player);
                return;
            }

            Messages.PlayerBalance(
                    player.getName(),
                    Messages.Currency.Formatted(account.accountBalance)
            ).sendTo(player);
            new Messages.Message("").sendTo(player);

            List<Transaction> transactions = economy.getTransactions(player, limit);

            Messages.TransactionList.Header(limit, player.getName()).sendTo(player);

            if (transactions.size() < 1) {
                Messages.TransactionList.NoTransactions().sendTo(player);
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
                ).sendTo(player);
                Messages.TransactionList.Transaction.Memo(
                        transaction.memo
                ).sendTo(player);
            }
        } catch (AccountNotFoundException e) {
            Messages.Errors.AccountNotFoundForPlayer(args[0]).sendTo(player);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
