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
import prison.nf.economy.Economy;

import java.sql.SQLException;
import java.util.Arrays;

public class SendCommand extends ServerCommand
{
    public SendCommand(EconomyPlugin plugin)
    {
        super(plugin, "send");
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: eco send <player-name> <amount> [memo]" + ChatColor.RESET);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Usage: eco send <player-name> <amount> [memo]" + ChatColor.RESET);
            return;
        }

        String memo = amount < 0
            ? getPlugin().getConfig().getString("Transactions.DefaultWithdrawMemo", "Administrative withdraw")
            : getPlugin().getConfig().getString("Transactions.DefaultDepositMemo", "Administrative deposit");
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

            Messages.Transfer.Sent(
                ChatColor.BLUE + offlinePlayer.getName() + ChatColor.RESET,
                Messages.Currency.Formatted(amount)
            ).sendTo(sender);
        } catch (AccountNotFoundException e) {
            Messages.Errors.AccountNotFoundForPlayer(args[0]).sendTo(sender);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
