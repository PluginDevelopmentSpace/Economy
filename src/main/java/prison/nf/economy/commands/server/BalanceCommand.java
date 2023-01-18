package prison.nf.economy.commands.server;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import prison.nf.commands.types.ServerCommand;
import prison.nf.economy.EconomyPlugin;
import prison.nf.economy.Messages;
import prison.nf.economy.Economy;
import prison.nf.economy.datatypes.Account;
import prison.nf.permissions.Permissions;

import java.sql.SQLException;

public class BalanceCommand extends ServerCommand
{
    private final EconomyPlugin plugin;

    public BalanceCommand(EconomyPlugin plugin)
    {
        super("balance");
        this.plugin = plugin;
    }

    @Override
    public String getUsage()
    {
        return "<player>";
    }

    @Override
    public int getRequiredArgumentCount()
    {
        return 1;
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
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

            Permissions permissions = Permissions.getInstance();
            if (permissions == null) {
                throw new RuntimeException("Permissions not initialized.");
            }

            String displayName = permissions.getDisplayNameFor(offlinePlayer);

            Messages.PlayerBalance(
                displayName,
                Messages.Currency.Formatted(account.accountBalance)
            ).sendTo(sender);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
