package prison.nf.economy.commands.player;

import org.bukkit.entity.Player;
import prison.nf.commands.types.PlayerCommand;
import prison.nf.economy.EconomyPlugin;
import prison.nf.economy.Messages;
import prison.nf.economy.Economy;
import prison.nf.economy.datatypes.Account;
import prison.nf.permissions.Permissions;

import java.sql.SQLException;

public class BalanceCommand extends PlayerCommand
{
    private final EconomyPlugin plugin;

    public BalanceCommand(EconomyPlugin plugin)
    {
        super("balance");
        this.plugin = plugin;
    }

    @Override
    public int getRequiredArgumentCount()
    {
        return 0;
    }

    @Override
    public String getUsage()
    {
        return "";
    }

    @Override
    public void execute(Player player, String[] args)
    {
        try {
            Economy dataStore = Economy.getInstance();
            if (dataStore == null) {
                Messages.Errors.LoadFailure().sendTo(player);
                Messages.Errors.Server.DataStoreNotLoaded().sendToServer();
                return;
            }

            Permissions permissions = Permissions.getInstance();
            if (permissions == null) {
                throw new RuntimeException("Permissions not initialized.");
            }

            String displayName = permissions.getDisplayNameFor(player);

            Account account = dataStore.getAccount(player);
            if (account == null) {
                Messages.Errors.AccountNotFoundForPlayer(displayName).sendTo(player);
                return;
            }

            Messages.PlayerBalance(
                displayName,
                Messages.Currency.Formatted(account.accountBalance)
            ).sendTo(player);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
