package joserodpt.realscoreboard.api.scoreboard;

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

import joserodpt.realscoreboard.api.RealScoreboardAPI;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class ScoreboardGroup {

    private final String world;
    private final List<RScoreboard> sbs;

    public ScoreboardGroup(String world, List<RScoreboard> sbs) {
        this.world = world;
        this.sbs = sbs;
    }

    /**
     * Gets RScoreboard instance for provided player
     *
     * @param p the player
     * @return  RScoreboard instance
     */
    public RScoreboard getScoreboard(Player p) {
        for (RScoreboard sb : this.sbs) {
            if (p.hasPermission(sb.getPermission())) {
                return sb;
            }
        }
        Optional<RScoreboard> o = this.sbs.stream().filter(rScoreboard -> rScoreboard.getInternalPermission().equalsIgnoreCase("default")).findFirst();
        if (o.isPresent()) {
            return o.get();
        } else {
            RealScoreboardAPI.getInstance().getLogger().log(Level.SEVERE, "[ERROR] You must have the DEFAULT scoreboard permission node on the world " + this.world + ".\nPlease check your config and re-add it again!");
            return new RScoreboard(p);
        }
    }

    /**
     * Gets list of RScoreboard instances
     *
     * @return list of RScoreboards
     */
    public List<RScoreboard> getScoreboards() {
       return this.sbs;
    }
}