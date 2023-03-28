package josegamerpt.realscoreboard.api.managers;

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