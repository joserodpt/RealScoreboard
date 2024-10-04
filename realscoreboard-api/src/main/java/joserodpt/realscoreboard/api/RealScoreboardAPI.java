package joserodpt.realscoreboard.api;

/*
 *   ____            _ ____                     _                         _
 *  |  _ \ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |
 *  | |_) / _ \/ _` | \___ \ / __/ _ \| '__/ _ \ '_ \ / _ \ / _` | '__/ _` |
 *  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |
 *  |_| \_\___|\__,_|_|____/ \___\___/|_|  \___|_.__/ \___/ \__,_|_|  \__,_|
 *
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2016-2024
 * @link https://github.com/joserodpt/RealScoreboard
 */

import com.google.common.base.Preconditions;
import joserodpt.realscoreboard.api.conditions.ConditionManager;
import joserodpt.realscoreboard.api.managers.AnimationManagerAPI;
import joserodpt.realscoreboard.api.managers.DatabaseManagerAPI;
import joserodpt.realscoreboard.api.managers.PlayerManagerAPI;
import joserodpt.realscoreboard.api.managers.ScoreboardManagerAPI;
import joserodpt.realscoreboard.api.utils.IPlaceholders;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * RealScoreboard API implementation class
 */
public abstract class RealScoreboardAPI {

    /**
     * -- GETTER --
     *  Gets instance of this API
     */
    @Getter
    private static RealScoreboardAPI instance;

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
    public abstract ScoreboardManagerAPI getScoreboardManagerAPI();

    /**
     * Gets DatabaseManager from API instance
     *
     * @return database manager instance
     */
    public abstract DatabaseManagerAPI getDatabaseManagerAPI();

    /**
     * Gets PlayerManager from API instance
     *
     * @return player manager instance
     */
    public abstract PlayerManagerAPI getPlayerManagerAPI();

    /**
     * Gets AnimationManager from API instance
     *
     * @return animation manager instance
     */
    public abstract AnimationManagerAPI getAnimationManagerAPI();

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

    public String getVersion() {
        return this.getPlugin().getDescription().getVersion();
    }

    public abstract ConditionManager getConditionManager();
}