package joserodpt.realscoreboard.api.managers;

import joserodpt.realscoreboard.api.scoreboard.RScoreboard;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface AbstractScoreboardManager {

    void loadScoreboards();
    void reload();
    Map<String, RScoreboard> getScoreboardMap();
    List<RScoreboard> getScoreboards();
    RScoreboard getScoreboardForPlayer(Player p);
    RScoreboard getScoreboard(String name);
}
