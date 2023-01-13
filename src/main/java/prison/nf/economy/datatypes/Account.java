package prison.nf.economy.datatypes;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import prison.nf.storage.data.Model;

import java.util.UUID;

@DatabaseTable(tableName = "eco_accounts")
public class Account extends Model
{
    @DatabaseField
    public String accountOwner;

    @DatabaseField(defaultValue = "0", canBeNull = false)
    public double accountBalance;

    public String getFormattedBalance()
    {
        return String.format("%.2f", this.accountBalance);
    }

    public String getFormattedBalance(String format)
    {
        return format.replace("%balance%", this.getFormattedBalance());
    }

    public OfflinePlayer getPlayer()
    {
        return Bukkit.getOfflinePlayer(UUID.fromString(this.accountOwner));
    }
}
