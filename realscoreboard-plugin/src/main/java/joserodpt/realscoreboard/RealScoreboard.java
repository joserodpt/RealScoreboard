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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealScoreboard
 */

import joserodpt.realscoreboard.managers.AnimationManager;
import joserodpt.realscoreboard.api.utils.IPlaceholders;
import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.config.PlayerData;
import joserodpt.realscoreboard.api.managers.AbstractAnimationManager;
import joserodpt.realscoreboard.api.managers.AbstractDatabaseManager;
import joserodpt.realscoreboard.api.managers.AbstractPlayerManager;
import joserodpt.realscoreboard.api.managers.AbstractScoreboardManager;
import joserodpt.realscoreboard.managers.DatabaseManager;
import joserodpt.realscoreboard.managers.PlayerManager;
import joserodpt.realscoreboard.managers.ScoreboardManager;
import joserodpt.realscoreboard.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Logger;

public class RealScoreboard extends RealScoreboardAPI {

    private DatabaseManager databaseManager;
    private final ScoreboardManager scoreboardManager;
    private final PlayerManager playerManager;
    private final AnimationManager animationManager;
    private final Logger logger;
    private final IPlaceholders placeholders;
    private final JavaPlugin plugin;

    public RealScoreboard(JavaPlugin plugin) {
        this.plugin = plugin;
        this.scoreboardManager = new ScoreboardManager();
        this.scoreboardManager.loadScoreboards();
        this.playerManager = new PlayerManager(this);
        try {
            this.databaseManager = new DatabaseManager(plugin);
            PlayerData.setup(this.playerManager); // Init PlayerManager into PlayerData to avoid NPE
        } catch (SQLException ex) {
            this.getLogger().severe("Error while starting the Database Manager!");
            this.getLogger().severe(ex.getMessage());
        }
        this.animationManager = new AnimationManager(plugin);
        this.logger = plugin.getLogger();
        this.placeholders = new Placeholders();
    }

    @Override
    public AbstractScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }

    @Override
    public AbstractDatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    @Override
    public AbstractPlayerManager getPlayerManager() {
        return this.playerManager;
    }

    @Override
    public AbstractAnimationManager getAnimationManager() {
        return this.animationManager;
    }

    @Override
    public void reload() {
        RSBConfig.reload();
        this.playerManager.getTasks().forEach((uuid, scoreboardTask) -> scoreboardTask.cancel());
        this.playerManager.getTasks().clear();
        this.scoreboardManager.reload();
        Bukkit.getOnlinePlayers().forEach(this.playerManager::check);
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