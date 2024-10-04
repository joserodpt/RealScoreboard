package joserodpt.realscoreboard.api.utils;

/*
 *   ____            _ ____                     _                         _
 *  |  _ \ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |
 *  | |_) / _ \/ _` | \___ \ / __/ _ \| '__/ _ \ '_ \ / _ \ / _` | '__/ _` |
 *  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |
 *  |_| \_\___|\__,_|_|____/ \___\___/|_|  \___|_.__/ \___/ \__,_|_|  \__,_|
 *
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2016-2024
 * @link https://github.com/joserodpt/RealScoreboard
 */

import org.bukkit.entity.Player;

public interface IPlaceholders {

    /**
     * Fixes placeholders in provided string
     *
     * @param player the player
     * @param string the string message
     * @param skipCond if should skip conditions parsing
     * @return  fixed string with placeholders
     */
    String setPlaceholders(Player player, String string, boolean skipCond);
}