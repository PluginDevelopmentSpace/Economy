package prison.nf.economy.commands.server;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import prison.nf.economy.EconomyPlugin;
import prison.nf.economy.Messages;
import prison.nf.economy.commands.commandtypes.ServerCommand;
import prison.nf.economy.Economy;
import prison.nf.economy.datatypes.Account;

import java.sql.SQLException;

public class BalanceCommand extends ServerCommand
{
    public BalanceCommand(EconomyPlugin plugin)
    {
        super(plugin, "balance");
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: eco balance <player-name>" + ChatColor.RESET);
            return;
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
            Account account = economy.getAccount(offlinePlayer);
            if (account == null) {
                Messages.Errors.AccountNotFoundForPlayer(args[0]).sendTo(sender);
                return;
            }

            Messages.PlayerBalance(
                offlinePlayer.getName(),
                Messages.Currency.Formatted(account.accountBalance)
            ).sendTo(sender);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
