package josegamerpt.realscoreboard.api;

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

import org.bukkit.entity.Player;

public interface IPlaceholders {

    /**
     * Gets player ping value as integer
     *
     * @param player the player related to thi method
     * @return       ping of provided player
     */
    int getPing(Player player);

    /**
     * Fixes placeholders in provided string
     *
     * @param player the player
     * @param string the string message
     * @return  fixed string with placeholders
     */
    String setPlaceHolders(Player player, String string);
}