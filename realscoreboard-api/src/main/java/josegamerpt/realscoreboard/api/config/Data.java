package josegamerpt.realscoreboard.api.config;

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

import java.util.ArrayList;
import org.bukkit.entity.Player;

public class Data {

    /**
     * Gets list of registered worlds
     *
     * @return arraylist of registered worlds
     */
    public static ArrayList<String> getRegisteredWorlds() {
        return new ArrayList<>(Config.file().getSection("Config.Scoreboard").getRoutesAsStrings(false));
    }

    /**
     * Gets correct scoreboard place for player
     *
     * @param p the player
     * @return  correct scoreboard place
     */
    @SuppressWarnings("ConstantConditions")
    public static String getCorrectPlace(Player p) {
        return checkSystem(p) ? p.getLocation().getWorld().getName() : Data.getRegisteredWorlds().get(0);
    }

    @SuppressWarnings("ConstantConditions")
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