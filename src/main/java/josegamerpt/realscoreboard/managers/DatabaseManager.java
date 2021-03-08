package josegamerpt.realscoreboard.managers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import josegamerpt.realscoreboard.classes.PlayerData;
import josegamerpt.realscoreboard.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;


public class DatabaseManager {

    private final Dao<PlayerData, UUID> playerDataDao;

    private final JavaPlugin javaPlugin;

    private final HashMap<UUID, PlayerData> playerDataCache = new HashMap<>();

    public DatabaseManager(JavaPlugin javaPlugin) throws SQLException {
        this.javaPlugin = javaPlugin;
        String databaseURL = getDatabaseURL();

        ConnectionSource connectionSource = new JdbcConnectionSource(
                databaseURL,
                Config.getSql().getString("username"),
                Config.getSql().getString("password"),
                DatabaseTypeUtils.createDatabaseType(databaseURL)
        );

        TableUtils.createTableIfNotExists(connectionSource, PlayerData.class);

        this.playerDataDao = DaoManager.createDao(connectionSource, PlayerData.class);

        getPlayerData();
    }

    /**
     * Database connection String used for establishing a connection.
     *
     * @return The database URL String
     */
    private @NotNull String getDatabaseURL() {
        switch (Config.getSql().getString("driver")) {
            case "MYSQL":
            case "MARIADB":
            case "POSTGRESQL":
                return "jdbc:" + Config.getSql().getString("driver") + "://" + Config.getSql().getString("host") + ":" + Config.getSql().getInt("port") + "/" + Config.getSql().getString("database");
            case "SQLSERVER":
                return "jdbc:sqlserver://" + Config.getSql().getString("host") + ":" + Config.getSql().getInt("port") + ";databaseName=" + Config.getSql().getString("database");
            default:
                return "jdbc:sqlite:" + new File(javaPlugin.getDataFolder(), Config.getSql().getString("database") + ".db");
        }
    }

    private void getPlayerData() {
        try {
            playerDataDao.queryForAll().forEach(playerData -> playerDataCache.put(playerData.getUuid(), playerData));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataCache.getOrDefault(uuid, new PlayerData(uuid));
    }

    public void savePlayerData(PlayerData playerData, boolean async) {
        playerDataCache.put(playerData.getUuid(), playerData);
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, () -> savePlayerData(playerData, false));
        } else {
            try {
                playerDataDao.createOrUpdate(playerData);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}
