package josegamerpt.realscoreboard;

import josegamerpt.realscoreboard.animation.AnimationManager;
import josegamerpt.realscoreboard.api.Placeholders;
import josegamerpt.realscoreboard.api.RealScoreboardAPI;
import josegamerpt.realscoreboard.api.config.Config;
import josegamerpt.realscoreboard.api.managers.AbstractAnimationManager;
import josegamerpt.realscoreboard.api.managers.AbstractPlayerManager;
import josegamerpt.realscoreboard.api.managers.AbstractScoreboardManager;
import josegamerpt.realscoreboard.managers.DatabaseManager;
import josegamerpt.realscoreboard.managers.PlayerManager;
import josegamerpt.realscoreboard.managers.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class RealScoreboard extends RealScoreboardAPI {

    private static RealScoreboard inst;
    private final DatabaseManager databaseManager;
    private final ScoreboardManager scoreboardManager;
    private final PlayerManager playerManager;
    private final AnimationManager animationManager;
    private final Logger logger;
    private final josegamerpt.realscoreboard.utils.Placeholders placeholders;
    private final JavaPlugin plugin;

    // TODO: make this constructor better?
    public RealScoreboard(JavaPlugin plugin, josegamerpt.realscoreboard.utils.Placeholders placeholders, Logger logger, AnimationManager animationManager,
                          PlayerManager playerManager, DatabaseManager databaseManager,
                          ScoreboardManager scoreboardManager) {
        inst = this;
        this.scoreboardManager = scoreboardManager;
        this.scoreboardManager.loadScoreboards();
        this.databaseManager = databaseManager;
        this.playerManager = playerManager;
        this.animationManager = animationManager;
        this.logger = logger;
        this.placeholders = placeholders;
        this.plugin = plugin;
    }

    @Override
    public AbstractScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }

    @Override
    public DatabaseManager getDatabaseManager() {
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
    public Placeholders getPlaceholders() {
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
