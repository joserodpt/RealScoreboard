package joserodpt.realscoreboard.api.managers;

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
