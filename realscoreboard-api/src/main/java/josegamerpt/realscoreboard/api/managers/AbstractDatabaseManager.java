package josegamerpt.realscoreboard.api.managers;

import josegamerpt.realscoreboard.api.config.PlayerData;
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
    public abstract void savePlayerData(PlayerData playerData, boolean async);
}