package josegamerpt.realscoreboard.api.managers;

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

import josegamerpt.realscoreboard.api.scoreboard.RBoard;
import josegamerpt.realscoreboard.api.scoreboard.RScoreboard;
import josegamerpt.realscoreboard.api.scoreboard.ScoreboardGroup;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Abstraction class for ScoreboardManager
 */
public abstract class AbstractScoreboardManager {

    /**
     * Loads all scoreboards from configuration
     */
    public abstract void loadScoreboards();

    /**
     * Reloads plugin configuration (including boards)
     */
    public abstract void reload();

    /**
     * Gets current player scoreboard by Player instance
     *
     * @param p the player
     * @return  scoreboard of player
     */
    public abstract RScoreboard getScoreboard(Player p);

    /**
     * Gets hashmap of loaded scoreboards
     *
     * @return hashmap of loaded scoreboards
     */
    public abstract HashMap<String, ScoreboardGroup> getScoreboards();

    /**
     * Gets list of RBoard instances
     *
     * @return list of RBoard instances
     */
    public abstract List<RBoard> getBoards();
}