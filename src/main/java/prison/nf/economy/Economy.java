package prison.nf.economy;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import prison.nf.storage.data.Model;
import prison.nf.storage.data.connections.SqlitePooledConnectionSource;
import prison.nf.storage.data.databases.SqliteDatabase;
import prison.nf.storage.data.DataStore;

import prison.nf.economy.exceptions.AccountNotFoundException;
import prison.nf.economy.exceptions.InsufficientAccountBalanceException;
import prison.nf.economy.exceptions.InvalidTransactionAmountException;

import prison.nf.economy.datatypes.Account;
import prison.nf.economy.datatypes.Transaction;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.*;

public final class Economy extends DataStore<SqlitePooledConnectionSource>
{
    private static Economy instance;

    @Nullable
    public static Economy getInstance()
    {
        return instance;
    }

    public static void initialize(String databaseFile) throws Exception
    {
        instance = new Economy(databaseFile);
        instance.runMigrations();
    }

    private final Dao<Account, String> mAccountDao;
    private final Dao<Transaction, String> mTransactionDao;

    public Economy(String databaseFilePath) throws Exception
    {
        super(new SqliteDatabase(databaseFilePath)
        {
            @Override
            public List<Class<? extends Model>> getModelClasses()
            {
                return new ArrayList<>()
                {{
                    add(Account.class);
                    add(Transaction.class);
                }};
            }
        });

        mAccountDao = this.getDataBase().createDao(Account.class, String.class);
        mTransactionDao = this.getDataBase().createDao(Transaction.class, String.class);
    }

    public Dao<Account, String> getAccountDao()
    {
        return mAccountDao;
    }

    public Dao<Transaction, String> getTransactionDao()
    {
        return mTransactionDao;
    }

    public String formatCurrency(double amount)
    {
        return String.format("%.2f", amount);
    }

    @Nullable
    public Account getAccount(UUID playerId) throws SQLException
    {
        return this.mAccountDao.queryForEq("accountOwner", playerId.toString()).stream().findFirst().orElse(null);
    }

    @Nullable
    public Account getAccount(Player player) throws SQLException
    {
        return getAccount(player.getUniqueId());
    }

    @Nullable
    public Account getAccount(OfflinePlayer player) throws SQLException
    {
        return getAccount(player.getUniqueId());
    }

    public Account getAccountOrCreate(UUID playerId) throws SQLException
    {
        Account account = this.getAccount(playerId);
        if (account != null) {
            return account;
        }

        Date now = new Date();

        account = new Account();
        account.id = UUID.randomUUID().toString();
        account.accountOwner = playerId.toString();
        account.accountBalance = 0d;
        account.createdAt = now;
        account.lastUpdatedAt = now;
        this.mAccountDao.create(account);
        return account;
    }

    public Account getAccountOrCreate(Player player) throws SQLException
    {
        return getAccountOrCreate(player.getUniqueId());
    }

    public Account getAccountOrCreate(OfflinePlayer player) throws SQLException
    {
        return getAccountOrCreate(player.getUniqueId());
    }

    public void giveMoney(UUID toPlayerId, double amount, String memo) throws SQLException, AccountNotFoundException
    {
        Account account = getAccount(toPlayerId);
        if (account == null) {
            throw new AccountNotFoundException();
        }

        account.accountBalance += amount;
        updateAccount(account);

        postTransaction(null, account.id, amount, memo);
    }

    public double getBalance(UUID playerId) throws SQLException, AccountNotFoundException
    {
        Account account = getAccount(playerId);
        if (account == null) {
            throw new AccountNotFoundException();
        }
        return account.accountBalance;
    }

    public void giveMoney(Player toPlayer, double amount, String memo) throws SQLException, AccountNotFoundException
    {
        giveMoney(toPlayer.getUniqueId(), amount, memo);
    }

    public void giveMoney(OfflinePlayer toPlayer, double amount, String memo) throws SQLException, AccountNotFoundException
    {
        giveMoney(toPlayer.getUniqueId(), amount, memo);
    }

    public void takeMoney(UUID fromPlayerId, double amount, String memo) throws SQLException, AccountNotFoundException
    {
        giveMoney(fromPlayerId, -amount, memo);
    }

    public void takeMoney(OfflinePlayer fromPlayer, double amount, String memo) throws SQLException, AccountNotFoundException
    {
        giveMoney(fromPlayer.getUniqueId(), -amount, memo);
    }

    public void takeMoney(Player fromPlayer, double amount, String memo) throws SQLException, AccountNotFoundException
    {
        giveMoney(fromPlayer.getUniqueId(), -amount, memo);
    }

    public void transfer(UUID fromPlayerId, UUID toPlayerId, double amount, String memo)
            throws SQLException, AccountNotFoundException, InvalidTransactionAmountException, InsufficientAccountBalanceException
    {
        if (amount <= 0) {
            throw new InvalidTransactionAmountException();
        }

        Account fromAccount = getAccount(fromPlayerId);
        if (fromAccount == null) {
            throw new AccountNotFoundException();
        }
        Account toAccount = getAccount(toPlayerId);
        if (toAccount == null) {
            throw new AccountNotFoundException();
        }

        if (fromAccount.accountBalance < amount) {
            throw new InsufficientAccountBalanceException();
        }

        fromAccount.accountBalance -= amount;
        updateAccount(fromAccount);

        toAccount.accountBalance += amount;
        updateAccount(toAccount);

        postTransaction(fromAccount.id, toAccount.id, amount, memo);
    }

    public void transfer(Player fromPlayer, Player toPlayer, double amount, String memo)
           throws SQLException, AccountNotFoundException, InvalidTransactionAmountException, InsufficientAccountBalanceException
    {
        transfer(fromPlayer.getUniqueId(), toPlayer.getUniqueId(), amount, memo);
    }

    public void transfer(OfflinePlayer fromPlayer, OfflinePlayer toPlayer, double amount, String memo)
            throws SQLException, AccountNotFoundException, InvalidTransactionAmountException, InsufficientAccountBalanceException
    {
        transfer(fromPlayer.getUniqueId(), toPlayer.getUniqueId(), amount, memo);
    }

    public void transfer(Player fromPlayer, OfflinePlayer toPlayer, double amount, String memo)
            throws SQLException, AccountNotFoundException, InvalidTransactionAmountException, InsufficientAccountBalanceException
    {
        transfer(fromPlayer.getUniqueId(), toPlayer.getUniqueId(), amount, memo);
    }

    public void transfer(OfflinePlayer fromPlayer, Player toPlayer, double amount, String memo)
            throws SQLException, AccountNotFoundException, InvalidTransactionAmountException, InsufficientAccountBalanceException
    {
        transfer(fromPlayer.getUniqueId(), toPlayer.getUniqueId(), amount, memo);
    }

    public List<Transaction> getTransactions(UUID playerId, long limit) throws SQLException, AccountNotFoundException
    {
        Account account = getAccount(playerId);
        if (account == null) {
            throw new AccountNotFoundException();
        }

        QueryBuilder<Transaction, String> qb
                = mTransactionDao.queryBuilder();
        qb
            .where()
            .eq("fromAccount", account.id)
            .or()
            .eq("toAccount", account.id);
        qb.orderBy("createdAt", false);
        qb.limit(limit);

        return mTransactionDao.query(qb.prepare());
    }

    public List<Transaction> getTransactions(Player player, long limit) throws SQLException, AccountNotFoundException
    {
        return getTransactions(player.getUniqueId(), limit);
    }

    public List<Transaction> getTransactions(OfflinePlayer player, long limit) throws SQLException, AccountNotFoundException
    {
        return getTransactions(player.getUniqueId(), limit);
    }

    private void postTransaction(String fromAccount, String toAccount, double amount, String memo) throws SQLException
    {
        Date now = new Date();

        Transaction transaction = new Transaction();
        transaction.id = UUID.randomUUID().toString();
        transaction.amount = amount;
        transaction.fromAccount = fromAccount;
        transaction.toAccount = toAccount;
        transaction.memo = memo;
        transaction.createdAt = now;
        transaction.lastUpdatedAt = now;

        this.mTransactionDao.create(transaction);
    }

    private void updateAccount(Account account) throws SQLException
    {
        account.lastUpdatedAt = new Date();
        this.mAccountDao.update(account);
    }
}
