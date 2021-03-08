package josegamerpt.realscoreboard;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@DatabaseTable(tableName = "blockdata")
public class PlayerData {
    @DatabaseField(columnName = "uuid", canBeNull = false, id = true)
    private @NotNull UUID uuid;

    @DatabaseField(columnName = "scoreboard_on")
    private boolean scoreboardON;

    public boolean isScoreboardON() {
        return scoreboardON;
    }

    public void setScoreboardON(boolean scoreboardON) {
        this.scoreboardON = scoreboardON;
    }

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.scoreboardON = true;
    }

    public PlayerData() {
    }

}
