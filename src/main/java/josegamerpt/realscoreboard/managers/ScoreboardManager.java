package josegamerpt.realscoreboard.managers;

import josegamerpt.realscoreboard.scoreboard.RScoreboard;
import josegamerpt.realscoreboard.scoreboard.RBoard;
import josegamerpt.realscoreboard.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoreboardManager {

    private HashMap<String, RScoreboard> scoreboardList = new HashMap<>();

    public void loadScoreboards() {
        Config.file().getConfigurationSection("Config.Scoreboard").getKeys(false)
                .forEach(s -> scoreboardList.put(s, new RScoreboard(s, Config.file().getInt("Config.Scoreboard." + s + ".Switch-Timer"))));
    }

    public void reload() {
        this.scoreboardList.forEach((s, rScoreboard) -> rScoreboard.stop());
        this.scoreboardList.clear();
        this.loadScoreboards();
    }

    public RScoreboard getScoreboard(String s) {
        return this.scoreboardList.get(s);
    }

    public HashMap<String, RScoreboard> getScoreboards() {
        return this.scoreboardList;
    }

    public List<RBoard> getBoards() {
        List<RBoard> boards = new ArrayList<>();
        this.scoreboardList.forEach((s, rScoreboard) -> boards.addAll(rScoreboard.getBoards()));
        return boards;
    }
}
