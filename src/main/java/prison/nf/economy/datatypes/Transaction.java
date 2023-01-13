package prison.nf.economy.datatypes;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import prison.nf.storage.data.Model;
import prison.nf.economy.Economy;

import javax.annotation.Nullable;
import java.sql.SQLException;

@DatabaseTable(tableName = "eco_transactions")
public class Transaction extends Model
{
    @DatabaseField
    public double amount;

    @DatabaseField
    public String memo;

    @DatabaseField
    public String fromAccount;

    @DatabaseField
    public String toAccount;

    @Nullable
    public Account getFromAccount(Economy store) throws SQLException
    {
        return store.getAccountDao().queryForId(this.fromAccount);
    }

    public Account getToAccount(Economy store) throws SQLException
    {
        return store.getAccountDao().queryForId(this.toAccount);
    }

    public String getFormattedAmount()
    {
        return String.format("%.2f", this.amount);
    }

    public String getFormattedAmount(String format)
    {
        return format.replace("%amount%", this.getFormattedAmount());
    }
}
