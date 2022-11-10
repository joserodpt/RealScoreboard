package josegamerpt.realscoreboard.api;

import org.bukkit.entity.Player;

public abstract class Placeholders {

    /**
     * Gets player ping value as integer
     *
     * @param player the player related to thi method
     * @return       ping of provided player
     */
    public abstract int getPing(Player player);

    /**
     * Fixes placeholders in provided string
     *
     * @param p the player
     * @param s the string message
     * @return  fixed string with placeholders
     */
    public abstract String setPlaceHolders(Player p, String s);
}
