package josegamerpt.realscoreboard.api.config;

import java.util.ArrayList;
import org.bukkit.entity.Player;

public class Data {

    public static ArrayList<String> getRegisteredWorlds() {
        return new ArrayList<>(Config.file().getSection("Config.Scoreboard").getRoutesAsStrings(false));
    }

    public static String getCorrectPlace(Player p) {
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
