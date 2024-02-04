package joserodpt.realscoreboard.api.managers;

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

import joserodpt.realscoreboard.api.scoreboard.RPlayerHook;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Abstraction class for PlayerManager
 */
public interface AbstractPlayerManager {

    /**
     * Get Player Hook
     */
    RPlayerHook getPlayerHook(UUID uuid);

    Map<UUID, RPlayerHook> getPlayerHooks();

    boolean isVanished(Player p);

    void checkPlayer(Player p);
}