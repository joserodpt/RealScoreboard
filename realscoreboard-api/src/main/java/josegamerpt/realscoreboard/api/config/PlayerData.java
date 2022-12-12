package josegamerpt.realscoreboard.api.config;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import josegamerpt.realscoreboard.api.RealScoreboardAPI;
import josegamerpt.realscoreboard.api.events.ScoreboardToggleEvent;
import josegamerpt.realscoreboard.api.managers.AbstractPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@DatabaseTable(tableName = "realscoreboard_playerdata")
public class PlayerData {
    @DatabaseField(columnName = "uuid", canBeNull = false, id = true)
    private UUID uuid;

    @DatabaseField(columnName = "scoreboard_on")
    private boolean scoreboardON;

    private final AbstractPlayerManager playerManager = RealScoreboardAPI.getInstance().getPlayerManager();

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
            this.playerManager.check(player);
        }
    }

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.scoreboardON = true;
    }

    @SuppressWarnings("unused")
    public PlayerData() {
    }
}