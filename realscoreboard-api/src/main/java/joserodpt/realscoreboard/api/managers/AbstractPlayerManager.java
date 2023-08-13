package joserodpt.realscoreboard.api.managers;

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

import joserodpt.realscoreboard.api.scoreboard.ScoreboardTask;
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