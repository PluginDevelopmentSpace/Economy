package prison.nf.economy.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import prison.nf.economy.EconomyPlugin;
import prison.nf.economy.Messages;
import prison.nf.economy.commands.commandtypes.PlayerCommand;
import prison.nf.storage.data.stores.economy.EconomyDataStore;
import prison.nf.storage.data.stores.economy.datatypes.Account;

import java.sql.SQLException;

public class BalanceCommand extends PlayerCommand
{
    public BalanceCommand(EconomyPlugin plugin)
    {
        super(plugin, "balance");
    }

    @Override
    public void execute(Player player, String[] args)
    {
        try {
            EconomyDataStore dataStore = EconomyDataStore.getInstance();
            if (dataStore == null) {
                Messages.Errors.LoadFailure().sendTo(player);
                Messages.Errors.Server.DataStoreNotLoaded().sendToServer();
                return;
            }

            Account account = dataStore.getAccount(player);
            if (account == null) {
                Messages.Errors.AccountNotFoundForPlayer(player.getName()).sendTo(player);
                return;
            }

            Messages.PlayerBalance(
                player.getName(),
                Messages.Currency.Formatted(account.accountBalance)
            ).sendTo(player);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
