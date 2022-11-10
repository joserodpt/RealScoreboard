package josegamerpt.realscoreboard.api.config;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import josegamerpt.realscoreboard.api.events.ScoreboardToggleEvent;
import org.bukkit.Bukkit;

import java.util.UUID;

@DatabaseTable(tableName = "realscoreboard_playerdata")
public class PlayerData {
    @DatabaseField(columnName = "uuid", canBeNull = false, id = true)
    private UUID uuid;

    @DatabaseField(columnName = "scoreboard_on")
    private boolean scoreboardON;

    public boolean isScoreboardON() {
        return scoreboardON;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setScoreboardON(boolean scoreboardON) {
        ScoreboardToggleEvent event = new ScoreboardToggleEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        this.scoreboardON = scoreboardON;
    }

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.scoreboardON = true;
    }

    // OrmLite needs a No Args constructor
    public PlayerData() {
    }

}
