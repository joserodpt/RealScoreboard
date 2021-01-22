package josegamerpt.realscoreboard.config;

import java.util.ArrayList;
import java.util.List;

import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.utils.Text;
import org.bukkit.entity.Player;

public class Data {

    public static ArrayList<String> getRegisteredWorlds() {
        return new ArrayList<>(Config.file().getConfigurationSection("Config.Scoreboard").getKeys(false));
    }

    public static String getCorrectPlace (Player p) {
        return checkSystem(p) ? p.getLocation().getWorld().getName() : Data.getRegisteredWorlds().get(0);
    }

    public static boolean checkSystem(Player p) {
        String world = p.getLocation().getWorld().getName();
        ArrayList<String> worlds = Data.getRegisteredWorlds();
        for (String s : worlds) {
            if (s.equalsIgnoreCase(world)) {
                return true;
            }
        }
        return false;
    }
}
