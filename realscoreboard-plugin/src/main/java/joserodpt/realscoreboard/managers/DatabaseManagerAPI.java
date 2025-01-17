package joserodpt.realscoreboard.managers;

/*
 *   ____            _ ____                     _                         _
 *  |  _ \ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |
 *  | |_) / _ \/ _` | \___ \ / __/ _ \| '__/ _ \ '_ \ / _ \ / _` | '__/ _` |
 *  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |
 *  |_| \_\___|\__,_|_|____/ \___\___/|_|  \___|_.__/ \___/ \__,_|_|  \__,_|
 *
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2016-2025
 * @link https://github.com/joserodpt/RealScoreboard
 */

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.logger.NullLogBackend;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.config.PlayerData;
import joserodpt.realscoreboard.api.events.DataSaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseManagerAPI extends joserodpt.realscoreboard.api.managers.DatabaseManagerAPI {

    private final Dao<PlayerData, UUID> playerDataDao;
    private final JavaPlugin javaPlugin;
    private final HashMap<UUID, PlayerData> playerDataCache = new HashMap<>();
    private final ExecutorService executor;

    public DatabaseManagerAPI(JavaPlugin javaPlugin) throws SQLException {
        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());
        this.javaPlugin = javaPlugin;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
                new ThreadFactoryBuilder().setNameFormat("RealScoreboard-Pool-%d").build());
        String databaseURL = getDatabaseURL();
        ConnectionSource connectionSource = new JdbcConnectionSource(
                databaseURL,
                RSBConfig.getSql().getString("username"),
                RSBConfig.getSql().getString("password"),
                DatabaseTypeUtils.createDatabaseType(databaseURL)
        );
        TableUtils.createTableIfNotExists(connectionSource, PlayerData.class);
        this.playerDataDao = DaoManager.createDao(connectionSource, PlayerData.class);
        getPlayerData();
    }

    private String getDatabaseURL() {
        String driver = RSBConfig.getSql().getString("driver").toLowerCase(Locale.ROOT);
        switch (driver) {
            case "mysql":
            case "mariadb":
                return "jdbc:mysql://" + RSBConfig.getSql().getString("host") + ":" + RSBConfig.getSql().getInt("port") + "/" + RSBConfig.getSql().getString("database");
            case "postgresql":
                return "jdbc:postgresql://" + RSBConfig.getSql().getString("host") + ":" + RSBConfig.getSql().getInt("port") + "/" + RSBConfig.getSql().getString("database");
            case "sqlserver":
                return "jdbc:sqlserver://" + RSBConfig.getSql().getString("host") + ":" + RSBConfig.getSql().getInt("port") + ";databaseName=" + RSBConfig.getSql().getString("database");
            default:
                return "jdbc:sqlite:" + new File(javaPlugin.getDataFolder(), RSBConfig.getSql().getString("database") + ".db");
        }
    }

    private void getPlayerData() {
        try {
            this.playerDataDao.queryForAll().forEach(playerData -> playerDataCache.put(playerData.getUuid(), playerData));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public PlayerData getPlayerData(UUID uuid) {
        return this.playerDataCache.getOrDefault(uuid, new PlayerData(uuid));
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.3")
    @Override
    public void savePlayerData(PlayerData playerData, boolean async) {
        this.playerDataCache.put(playerData.getUuid(), playerData);
        DataSaveEvent event = new DataSaveEvent(playerData, async);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        if (async) {
            this.saveDataAsync(playerData);
        } else {
            try {
                this.playerDataDao.createOrUpdate(playerData);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.3")
    private void saveDataAsync(PlayerData playerData) {
        this.executor.execute(() -> {
            try {
                this.playerDataDao.createOrUpdate(playerData);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }
}