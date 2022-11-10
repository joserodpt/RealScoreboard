package josegamerpt.realscoreboard.api.managers;

import josegamerpt.realscoreboard.api.scoreboard.ScoreboardTask;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Abstraction class for PlayerManager
 */
public abstract class AbstractPlayerManager {

    /**
     * Checks player
     *
     * @param p the player
     */
    public abstract void check(Player p);

    /**
     * Gets hashmap of scoreboard tasks
     *
     * @return hashmap of tasks
     */
    public abstract HashMap<UUID, ScoreboardTask> getTasks();
}
