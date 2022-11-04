package josegamerpt.realscoreboard.managers;

import josegamerpt.realscoreboard.config.Data;
import josegamerpt.realscoreboard.scoreboard.RScoreboard;
import josegamerpt.realscoreboard.scoreboard.RBoard;
import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.scoreboard.ScoreboardGroup;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoreboardManager {

    private HashMap<String, ScoreboardGroup> scoreboardList = new HashMap<>();

    public void loadScoreboards() {

        for (String world : Config.file().getSection("Config.Scoreboard").getRoutesAsStrings(false)) {
            //world
            List<RScoreboard> sbs = new ArrayList<>();

            for (String perm : Config.file().getSection("Config.Scoreboard." + world).getRoutesAsStrings(false)) {
                //perm
                sbs.add(new RScoreboard(world, perm, Config.file().getInt("Config.Scoreboard." + world + "." + perm + ".Switch-Timer")));
            }

            this.scoreboardList.put(world, new ScoreboardGroup(world, sbs));
        }
    }

    public void reload() {
        this.scoreboardList.forEach((s, rScoreboard) -> rScoreboard.getScoreboards().forEach(RScoreboard::stop));
        this.scoreboardList.clear();
        this.loadScoreboards();
    }

    public RScoreboard getScoreboard(Player p) {
        ScoreboardGroup sg = this.scoreboardList.get(Data.getCorrectPlace(p));
        return sg.getScoreboard(p);
    }

    public HashMap<String, ScoreboardGroup> getScoreboards() {
        return this.scoreboardList;
    }

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
