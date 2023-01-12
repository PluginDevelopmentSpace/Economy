package prison.nf.economy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Date;

public class Messages
{
    public static class Currency
    {
        public static String Formatted(double value)
        {
            if (value < 0) {
                return Currency.NegativeFormatted(value);
            }

            if (value == 0) {
                return Currency.ZeroFormatted(value);
            }

            return Currency.PositiveFormatted(value);
        }

        public static String PositiveFormatted(double value)
        {
            String format = instance.plugin.getConfig().getString(
                "Currency.Format.Positive",
                "&a$%value%&r"
            );

            return ChatColor.translateAlternateColorCodes(
                '&', format.replace("%value%", String.format("%.2f", value))
            );
        }

        public static String NegativeFormatted(double value)
        {
            String format = instance.plugin.getConfig().getString(
                    "Currency.Format.Negative",
                    "&c$%value%&r"
            );

            return ChatColor.translateAlternateColorCodes(
                    '&', format.replace("%value%", String.format("%.2f", value))
            );
        }

        public static String ZeroFormatted(double value)
        {
            String format = instance.plugin.getConfig().getString(
                "Currency.Format.Zero",
                "&a$%value%&r"
            );

            return ChatColor.translateAlternateColorCodes(
                '&', format.replace("%value%", String.format("%.2f", value))
            );
        }
    }

    public static class TransactionList
    {
        public static Message NoTransactions()
        {
            String msgString = instance.plugin.getConfig().getString("Strings.Messages.TransactionList.NoTransactions", "&eNo Transactions.&r");

            return new Message(ChatColor.translateAlternateColorCodes('&', msgString));
        }

        public static Message Header(long count, String playerName)
        {
            String msgString = instance.plugin.getConfig().getString("Strings.Messages.TransactionList.Header", "Last &a%count%&r transactions for &9%player%&r");

            return new Message(ChatColor.translateAlternateColorCodes('&', msgString.replace("%count%", Long.toString(count)).replace("%player%", playerName))) {
                @Override
                public void sendTo(CommandSender sender)
                {
                    sender.sendMessage(this.toString());
                    sender.sendMessage(new Message("").toString());
                }
            };
        }

        public static class Transaction {
            public static Message Detail(Date date, String from, String to, String value)
            {
                String msgString = instance.plugin.getConfig().getString("Strings.Messages.TransactionList.Transaction.Detail", "&f - &7&o[%date%] &9%from%&f -> &9%to%&r &f: %value%&r");
                return new Message(ChatColor.translateAlternateColorCodes('&', msgString.replace("%date%", date.toString()).replace("%from%", from).replace("%to%", to).replace("%value%", value)));
            }

            public static Message Memo(String memo)
            {
                String msgString = instance.plugin.getConfig().getString("Strings.Messages.TransactionList.Transaction.Memo", "   &fMemo: &f&r&o%memo%&r");
                return new Message(ChatColor.translateAlternateColorCodes('&', msgString.replace("%memo%", memo))) {
                    @Override
                    public void sendTo(CommandSender sender)
                    {
                        sender.sendMessage(this.toString());
                        sender.sendMessage(new Message("").toString());
                    }
                };
            }
        }
    }

    public static class Transfer
    {
        public static Message Received(String from, String value)
        {
            String msgString = instance.plugin.getConfig().getString("Strings.Messages.Transfer.Received", "%from% has transferred %value% to &9your account&r.");

            return new Message(ChatColor.translateAlternateColorCodes('&', msgString.replace("%from%", from).replace("%value%", value)));
        }

        public static Message Admin(String from, String to, String value)
        {
            String msgString = instance.plugin.getConfig().getString("Strings.Messages.Transfer.Admin", "&6A server administrator&r has transferred %value% from %from% to %to%.");

            return new Message(ChatColor.translateAlternateColorCodes('&', msgString.replace("%from%", from).replace("%to%", to).replace("%value%", value)));
        }

        public static Message Transferred(String from, String to, String value)
        {
            String msgString = instance.plugin.getConfig().getString("Strings.Messages.Transfer.Transferred", "&aTransferred %value% from %from% to %to%.&r");

            return new Message(ChatColor.translateAlternateColorCodes('&', msgString.replace("%from%", from).replace("%to%", to).replace("%value%", value)));
        }

        public static Message Sent(String to, String value)
        {
            String msgString = instance.plugin.getConfig().getString("Strings.Messages.Transfer.Sent", "&aSent %value% to %to%.&r");

            return new Message(ChatColor.translateAlternateColorCodes('&', msgString.replace("%to%", to).replace("%value%", value)));
        }
    }

    public static Message PlayerBalance(String playerName, String value)
    {
        String msgString = instance.plugin.getConfig().getString("Strings.Messages.PlayerBalance", "Account Balance for &9%player%&r: %value%");

        return new Message(ChatColor.translateAlternateColorCodes('&', msgString.replace("%player%", playerName).replace("%value%", value)));
    }

    public static class Errors
    {
        public static Message InvalidLimit(long limit)
        {
            String msgString = instance.plugin.getConfig().getString("Strings.Errors.InvalidLimit", "&cInvalid limit '%limit%'. Must be a valid number.&r");

            return new Message(ChatColor.translateAlternateColorCodes('&', msgString.replace("%limit%", Long.toString(limit))));
        }

        public static Message AccountNotFoundForPlayer(String playerName)
        {
            String msgString = instance.plugin.getConfig().getString("Strings.Errors.AccountNotFoundForPlayer", "&cCould not locate an account for player &9%player%&c.&r");

            return new Message(ChatColor.translateAlternateColorCodes('&', msgString.replace("%player%", playerName)));
        }

        public static Message AccountsNotFound()
        {
            String msgString = instance.plugin.getConfig().getString("Strings.Errors.AccountsNotFound", "&cCould not locate one or more of the specified accounts.&r");

            return new Message(ChatColor.translateAlternateColorCodes('&', msgString));
        }

        public static Message InsufficientAccountBalance()
        {
            String msgString = instance.plugin.getConfig().getString("Strings.Errors.InsufficientAccountBalance", "&cInsufficient account balance.&r");

            return new Message(ChatColor.translateAlternateColorCodes('&', msgString));
        }

        public static Message LoadFailure()
        {
            String msgString = instance.plugin.getConfig().getString("Strings.Errors.LoadFailure", "&cFailed to load economy system.&r");

            return new Message(ChatColor.translateAlternateColorCodes('&', msgString));
        }

        public static Message SameAccountTransfer()
        {
            String msgString = instance.plugin.getConfig().getString("Strings.Errors.SameAccountTransfer", "&cFunds can only be transferred between differing accounts.&r");

            return new Message(ChatColor.translateAlternateColorCodes('&', msgString));
        }

        public static Message TransactionAmountNegative()
        {
            String msgString = instance.plugin.getConfig().getString("Strings.Errors.TransactionAmountNegative", "&cTransaction amount must be a positive number.&r");

            return new Message(ChatColor.translateAlternateColorCodes('&', msgString));
        }

        public static class Server
        {
            public static Message DataStoreNotLoaded()
            {
                return new Message(ChatColor.RED + "An attempt was made to access the economy data store, " + "but the data store had not yet been initialized.");
            }
        }
    }

    public static class Message
    {
        private final String message;

        public Message(String message)
        {
            this.message = message;
        }

        @Override
        public String toString()
        {
            String messagePrefix = instance.plugin.getConfig().getString("MessagePrefix", "[&aEconomy&r] ");
            String formattedPrefix = ChatColor.translateAlternateColorCodes('&', messagePrefix);
            return formattedPrefix + this.message;
        }

        public void sendTo(CommandSender sender)
        {
            sender.sendMessage(this.toString());
        }

        public void sendToServer()
        {
            this.sendTo(instance.plugin.getServer().getConsoleSender());
        }
    }

    private static Messages instance;

    public static void initialize(EconomyPlugin plugin)
    {
        Messages.instance = new Messages(plugin);
    }

    private final EconomyPlugin plugin;

    public Messages(EconomyPlugin plugin)
    {
        this.plugin = plugin;
    }
}
