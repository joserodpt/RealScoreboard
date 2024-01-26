package joserodpt.realscoreboard.managers;

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

import joserodpt.realscoreboard.api.config.Data;
import joserodpt.realscoreboard.api.managers.AbstractScoreboardManager;
import joserodpt.realscoreboard.api.scoreboard.RScoreboard;
import joserodpt.realscoreboard.api.scoreboard.RBoard;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.scoreboard.ScoreboardGroup;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoreboardManager extends AbstractScoreboardManager {

    private final HashMap<String, ScoreboardGroup> scoreboardList = new HashMap<>();

    @Override
    public void loadScoreboards() {
        for (String world : RSBConfig.file().getSection("Config.Scoreboard").getRoutesAsStrings(false)) {
            List<RScoreboard> sbs = new ArrayList<>();
            for (String perm : RSBConfig.file().getSection("Config.Scoreboard." + world).getRoutesAsStrings(false)) {
                sbs.add(new RScoreboard(world, perm, RSBConfig.file().getInt("Config.Scoreboard." + world + "." + perm + ".Switch-Timer")));
            }
            this.scoreboardList.put(world, new ScoreboardGroup(world, sbs));
        }
    }

    @Override
    public void reload() {
        this.scoreboardList.forEach((s, rScoreboard) -> rScoreboard.getScoreboards().forEach(RScoreboard::stop));
        this.scoreboardList.clear();
        this.loadScoreboards();
    }

    @Override
    public RScoreboard getScoreboard(Player p) {
        ScoreboardGroup sg = this.scoreboardList.get(Data.getCorrectPlace(p));
        return sg.getScoreboard(p);
    }

    @Override
    public HashMap<String, ScoreboardGroup> getScoreboards() {
        return this.scoreboardList;
    }

    @Override
    public List<RBoard> getBoards() {
        List<RBoard> boards = new ArrayList<>();
        List<ScoreboardGroup> tmp = new ArrayList<>();
        this.scoreboardList.forEach((s, scoreboardGroup) -> tmp.add(scoreboardGroup));
        for (ScoreboardGroup scoreboardGroup : tmp) {
            scoreboardGroup.getScoreboards().forEach(rScoreboard -> boards.addAll(rScoreboard.getBoards()));
        }
        return boards;
    }
}