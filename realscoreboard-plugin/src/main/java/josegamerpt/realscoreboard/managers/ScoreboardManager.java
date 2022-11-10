package josegamerpt.realscoreboard.managers;

import josegamerpt.realscoreboard.api.config.Data;
import josegamerpt.realscoreboard.api.managers.AbstractScoreboardManager;
import josegamerpt.realscoreboard.api.scoreboard.RScoreboard;
import josegamerpt.realscoreboard.api.scoreboard.RBoard;
import josegamerpt.realscoreboard.api.config.Config;
import josegamerpt.realscoreboard.api.scoreboard.ScoreboardGroup;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoreboardManager extends AbstractScoreboardManager {

    private HashMap<String, ScoreboardGroup> scoreboardList = new HashMap<>();

    @Override
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
