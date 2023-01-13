package prison.nf.economy.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import prison.nf.economy.Economy;

import java.sql.SQLException;

public class PlayerEvents implements Listener
{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Economy economy = Economy.getInstance();
        if (economy != null) {
            try {
                economy.getAccountOrCreate(event.getPlayer().getUniqueId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
