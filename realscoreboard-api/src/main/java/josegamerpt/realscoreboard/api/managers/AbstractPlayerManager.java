package josegamerpt.realscoreboard.api.managers;

import josegamerpt.realscoreboard.api.scoreboard.ScoreboardTask;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Abstraction class for PlayerManager
 */
public abstract class AbstractPlayerManager {

    public abstract void check(Player p);

    public abstract HashMap<UUID, ScoreboardTask> getTasks();
}
