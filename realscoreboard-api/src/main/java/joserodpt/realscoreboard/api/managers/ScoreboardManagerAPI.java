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
 * @author José Rodrigues © 2016-2024
 * @link https://github.com/joserodpt/RealScoreboard
 */

import joserodpt.realscoreboard.api.scoreboard.RScoreboard;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

public interface ScoreboardManagerAPI {

    void loadScoreboards();
    void reload();
    Map<String, RScoreboard> getScoreboardMap();
    Collection<RScoreboard> getScoreboards();
    RScoreboard getScoreboardForPlayer(Player p);
    RScoreboard getScoreboard(String name);
}
