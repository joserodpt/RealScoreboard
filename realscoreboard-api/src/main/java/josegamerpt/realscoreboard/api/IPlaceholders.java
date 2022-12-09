package josegamerpt.realscoreboard.api;

import org.bukkit.entity.Player;

public interface IPlaceholders {

    /**
     * Gets player ping value as integer
     *
     * @param player the player related to thi method
     * @return       ping of provided player
     */
    public int getPing(Player player);

    /**
     * Fixes placeholders in provided string
     *
     * @param player the player
     * @param string the string message
     * @return  fixed string with placeholders
     */
    public String setPlaceHolders(Player player, String string);
}