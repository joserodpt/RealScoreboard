package josegamerpt.realscoreboard.scoreboard;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class ScoreboardGroup {

    private String world;
    private List<RScoreboard> sbs;

    public ScoreboardGroup(String world, List<RScoreboard> sbs)
    {
        this.world = world;
        this.sbs = sbs;
    }

    public String getWorld() {
        return this.world;
    }

    public RScoreboard getScoreboard(Player p) {
        for (RScoreboard sb : this.sbs) {
            if (p.hasPermission(sb.getPermission())) {
                return sb;
            }
        }

        Optional<RScoreboard> o = this.sbs.stream().filter(rScoreboard -> rScoreboard.getInternalPermission().equalsIgnoreCase("default")).findFirst();
        return o.get();
    }

    public List<RScoreboard> getScoreboards() {
       return this.sbs;
    }
}
