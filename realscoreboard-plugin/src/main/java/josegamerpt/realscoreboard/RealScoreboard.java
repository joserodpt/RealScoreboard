package josegamerpt.realscoreboard;

import josegamerpt.realscoreboard.animation.AnimationManager;
import josegamerpt.realscoreboard.api.IPlaceholders;
import josegamerpt.realscoreboard.api.RealScoreboardAPI;
import josegamerpt.realscoreboard.api.config.Config;
import josegamerpt.realscoreboard.api.managers.AbstractAnimationManager;
import josegamerpt.realscoreboard.api.managers.AbstractDatabaseManager;
import josegamerpt.realscoreboard.api.managers.AbstractPlayerManager;
import josegamerpt.realscoreboard.api.managers.AbstractScoreboardManager;
import josegamerpt.realscoreboard.managers.DatabaseManager;
import josegamerpt.realscoreboard.managers.PlayerManager;
import josegamerpt.realscoreboard.managers.ScoreboardManager;
import josegamerpt.realscoreboard.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Logger;

public class RealScoreboard extends RealScoreboardAPI {

    private static RealScoreboard inst;
    private DatabaseManager databaseManager;
    private final ScoreboardManager scoreboardManager;
    private final PlayerManager playerManager;
    private final AnimationManager animationManager;
    private final Logger logger;
    private final IPlaceholders placeholders;
    private final JavaPlugin plugin;

    public RealScoreboard(JavaPlugin plugin) {
        inst = this;
        this.plugin = plugin;
        this.scoreboardManager = new ScoreboardManager();
        this.scoreboardManager.loadScoreboards();
        try {
            this.databaseManager = new DatabaseManager(plugin);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        this.playerManager = new PlayerManager(this);
        this.animationManager = new AnimationManager();
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
        Config.reload();
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

    public static RealScoreboard inst() {
        return inst;
    }
}