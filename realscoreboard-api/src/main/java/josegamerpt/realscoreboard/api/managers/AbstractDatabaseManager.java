package josegamerpt.realscoreboard.api.managers;

import josegamerpt.realscoreboard.api.config.PlayerData;
import java.util.UUID;

/**
 * Abstraction class for DatabaseManager
 */
public abstract class AbstractDatabaseManager {

    public abstract PlayerData getPlayerData(UUID uuid);

    public abstract void savePlayerData(PlayerData playerData, boolean async);
}
