package josegamerpt.realscoreboard.scoreboard;

import josegamerpt.realscoreboard.RealScoreboard;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

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
        if (o.isPresent()) {
            return o.get();
        } else {
            RealScoreboard.getInstance().getLogger().log(Level.SEVERE, "[ERROR] You must have the DEFAULT scoreboard permission node on the world " + world + ".\nPlease check your config and re-add it again!");
            return null;
        }
    }

    public List<RScoreboard> getScoreboards() {
       return this.sbs;
    }
}
