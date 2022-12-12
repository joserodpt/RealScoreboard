package josegamerpt.realscoreboard.api;

import com.google.common.base.Preconditions;
import josegamerpt.realscoreboard.api.managers.AbstractAnimationManager;
import josegamerpt.realscoreboard.api.managers.AbstractDatabaseManager;
import josegamerpt.realscoreboard.api.managers.AbstractPlayerManager;
import josegamerpt.realscoreboard.api.managers.AbstractScoreboardManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * RealScoreboard API implementation class
 */
public abstract class RealScoreboardAPI {

    private static RealScoreboardAPI instance;

    /**
     * Gets instance of this API
     *
     * @return RealScoreboard API instance
     */
    public static RealScoreboardAPI getInstance() {
        return instance;
    }

    /**
     * Sets the RealScoreboard instance.
     * <b>Note! This method may only be called once</b>
     *
     * @param instance the new instance to set
     */
    public static void setInstance(RealScoreboardAPI instance) {
        Preconditions.checkNotNull(instance, "instance");
        Preconditions.checkArgument(RealScoreboardAPI.instance == null, "Instance already set");
        RealScoreboardAPI.instance = instance;
    }

    /**
     * Get ScoreboardManager from API instance
     *
     * @return scoreboard manager instance
     */
    public abstract AbstractScoreboardManager getScoreboardManager();

    /**
     * Gets DatabaseManager from API instance
     *
     * @return database manager instance
     */
    public abstract AbstractDatabaseManager getDatabaseManager();

    /**
     * Gets PlayerManager from API instance
     *
     * @return player manager instance
     */
    public abstract AbstractPlayerManager getPlayerManager();

    /**
     * Gets AnimationManager from API instance
     *
     * @return animation manager instance
     */
    public abstract AbstractAnimationManager getAnimationManager();

    /**
     * Gets RealScoreboard plugin logger
     *
     * @return logger of RealScoreboard
     */
    public abstract Logger getLogger();

    public abstract IPlaceholders getPlaceholders();

    /**
     * Gets JavaPlugin instance of RealScoreboard
     *
     * @return plugin instance of RealScoreboard
     */
    public abstract JavaPlugin getPlugin();

    /**
     * Reloads plugin configuration directly from API instance
     */
    public abstract void reload();
}