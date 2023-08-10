package josegamerpt.realscoreboard.api.managers;

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

import josegamerpt.realscoreboard.api.config.PlayerData;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

/**
 * Abstraction class for DatabaseManager
 */
public abstract class AbstractDatabaseManager {

    /**
     * Gets playerdata for given UUID
     *
     * @param uuid the uuid
     * @return     playerdata instance for provided uuid
     */
    public abstract PlayerData getPlayerData(UUID uuid);

    /**
     * Saves provided playerdata asynchronously or not
     *
     * @param playerData the playerdata instance
     * @param async      boolean value if save should be async
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.3")
    public abstract void savePlayerData(PlayerData playerData, boolean async);
}