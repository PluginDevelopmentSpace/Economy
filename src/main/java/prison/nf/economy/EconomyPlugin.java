package prison.nf.economy;

import org.bukkit.plugin.java.JavaPlugin;
import prison.nf.economy.events.PlayerEvents;

import java.io.File;
import java.util.List;

public class EconomyPlugin extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        // Initialize Config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Register Economy Datastore
        try {
            Economy.initialize(getDataFolder().getAbsolutePath() + File.separator + "database.sqlite");
            getLogger().info("Initialized Economy DataStore.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Register Strings
        Messages.initialize(this);

        // Register Commands
        EcoCommands.register(this);

        // Register Events
        this.getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        getLogger().info("Enabled " + this.getName() + " v" + this.getVersion() + " by " + this.getAuthorsString());
    }

    @Override
    public void onDisable()
    {
        getLogger().info("Disabled " + this.getName() + " v" + this.getVersion() + " by " + this.getAuthorsString());
    }

    private String getAuthorsString()
    {
        // Declare variables
        StringBuilder authorsStringBuilder;
        List<String> authors;
        int authorsSize, authorIdx;

        // Initialize variables
        authorsStringBuilder = new StringBuilder();
        authors = this.getDescription().getAuthors();
        authorsSize = authors.size();

        // Loop through the authors.
        for (authorIdx = 0; authorIdx < authorsSize; authorIdx++) {
            // Retrieve the name of the author.
            String author = authors.get(authorIdx);

            // If we're on a subsequent iteration, check if this is the last author and if so, use the word "and" for
            // concatenation. Otherwise, use a comma for concatenation.
            if (authorIdx > 0) {
                boolean isLastAuthor = authorIdx == authorsSize - 1;
                authorsStringBuilder.append(isLastAuthor ? " and " : ", ");
            }

            // Append the authors name.
            authorsStringBuilder.append(author);
        }

        return authorsStringBuilder.toString();
    }

    private String getVersion()
    {
        return this.getDescription().getVersion();
    }
}
