package joserodpt.realscoreboard.api.config;

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

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.events.ScoreboardToggleEvent;
import joserodpt.realscoreboard.api.managers.AbstractPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@DatabaseTable(tableName = "realscoreboard_playerdata")
public class PlayerData {
    @DatabaseField(columnName = "uuid", canBeNull = false, id = true)
    private UUID uuid;

    @DatabaseField(columnName = "scoreboard_on")
    private boolean scoreboardON;

    private static AbstractPlayerManager playerManager;

    /**
     * Gets boolean value of current scoreboard status
     *
     * @return true/false if scoreboard is ON
     */
    public boolean isScoreboardON() {
        return scoreboardON;
    }

    /**
     * Gets saved UUID value in db
     *
     * @return registered UUID in database
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Sets scoreboard status for playerdata
     *
     * @param scoreboardON scoreboard toggle status
     */
    public void setScoreboardON(boolean scoreboardON) {
        ScoreboardToggleEvent event = new ScoreboardToggleEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        this.scoreboardON = scoreboardON;
        if (scoreboardON) {
            Player player = Bukkit.getPlayer(this.uuid);
            assert player != null;
            RealScoreboardAPI.getInstance().getPlayerManager().checkPlayer(player);
        }
    }

    public static void setup(AbstractPlayerManager playerManager) {
        PlayerData.playerManager = playerManager;
    }

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.scoreboardON = !RSBConfig.file().getBoolean("Config.RealScoreboard-Disabled-By-Default");
    }

    @SuppressWarnings("unused")
    public PlayerData() {
    }
}