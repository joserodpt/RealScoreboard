package josegamerpt.realscoreboard.api;

import com.google.common.base.Preconditions;
import josegamerpt.realscoreboard.api.managers.AbstractAnimationManager;
import josegamerpt.realscoreboard.api.managers.AbstractDatabaseManager;
import josegamerpt.realscoreboard.api.managers.AbstractPlayerManager;
import josegamerpt.realscoreboard.api.managers.AbstractScoreboardManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public abstract class RealScoreboardAPI {

    private static RealScoreboardAPI instance;

    public static RealScoreboardAPI getInstance() {
        return instance;
    }

    public static void setInstance(RealScoreboardAPI instance) {
        Preconditions.checkNotNull(instance, "instance");
        Preconditions.checkArgument(RealScoreboardAPI.instance == null, "Instance already set");
        RealScoreboardAPI.instance = instance;
    }

    public abstract AbstractScoreboardManager getScoreboardManager();

    public abstract AbstractDatabaseManager getDatabaseManager();

    public abstract AbstractPlayerManager getPlayerManager();

    public abstract AbstractAnimationManager getAnimationManager();

    public abstract Logger getLogger();

    public abstract Placeholders getPlaceholders();

    public abstract JavaPlugin getPlugin();

    public abstract void reload();
}
