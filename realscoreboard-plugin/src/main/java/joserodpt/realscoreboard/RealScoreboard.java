package joserodpt.realscoreboard;

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

import joserodpt.realscoreboard.api.config.RSBScoreboards;
import joserodpt.realscoreboard.api.scoreboard.RSBPlayer;
import joserodpt.realscoreboard.managers.AnimationManagerAPI;
import joserodpt.realscoreboard.api.utils.IPlaceholders;
import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.managers.DatabaseManagerAPI;
import joserodpt.realscoreboard.api.managers.PlayerManagerAPI;
import joserodpt.realscoreboard.api.managers.ScoreboardManagerAPI;
import joserodpt.realscoreboard.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Logger;

public class RealScoreboard extends RealScoreboardAPI {

    private joserodpt.realscoreboard.managers.DatabaseManagerAPI databaseManager;
    private final joserodpt.realscoreboard.managers.ScoreboardManagerAPI scoreboardManager;
    private final joserodpt.realscoreboard.managers.PlayerManagerAPI playerManager;
    private final AnimationManagerAPI animationManager;
    private final Logger logger;
    private final IPlaceholders placeholders;
    private final JavaPlugin plugin;

    public RealScoreboard(JavaPlugin plugin) {
        this.plugin = plugin;
        this.scoreboardManager = new joserodpt.realscoreboard.managers.ScoreboardManagerAPI(this);
        this.playerManager = new joserodpt.realscoreboard.managers.PlayerManagerAPI(this);
        try {
            this.databaseManager = new joserodpt.realscoreboard.managers.DatabaseManagerAPI(plugin);
        } catch (SQLException ex) {
            this.getLogger().severe("Error while starting the Database Manager!");
            this.getLogger().severe(ex.getMessage());
        }
        this.animationManager = new AnimationManagerAPI(plugin);
        this.logger = plugin.getLogger();
        this.placeholders = new Placeholders();
    }

    @Override
    public ScoreboardManagerAPI getScoreboardManagerAPI() {
        return this.scoreboardManager;
    }

    @Override
    public DatabaseManagerAPI getDatabaseManagerAPI() {
        return this.databaseManager;
    }

    @Override
    public PlayerManagerAPI getPlayerManagerAPI() {
        return this.playerManager;
    }

    @Override
    public AnimationManagerAPI getAnimationManagerAPI() {
        return this.animationManager;
    }

    @Override
    public void reload() {
        RSBConfig.reload();
        RSBScoreboards.reload();

        this.playerManager.getPlayerMap().values().forEach(RSBPlayer::stopScoreboard);
        this.scoreboardManager.reload();

        this.playerManager.getPlayerMap().values().forEach(RSBPlayer::stopScoreboard);
        this.playerManager.getPlayerMap().clear();
        Bukkit.getOnlinePlayers().forEach(this.playerManager::initPlayer);
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public IPlaceholders getPlaceholders() {
        return this.placeholders;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this.plugin;
    }
}