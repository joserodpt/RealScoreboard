package josegamerpt.realscoreboard.listeners;

import com.gmail.nossr50.events.scoreboard.McMMOScoreboardMakeboardEvent;
import com.gmail.nossr50.events.scoreboard.McMMOScoreboardRevertEvent;
import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.config.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class McMMOScoreboardListener implements Listener {

    // Enable RealScoreboard for player on revert
    @EventHandler
    public void onRevert(McMMOScoreboardRevertEvent event) {
        Player player = event.getTargetPlayer();
        PlayerData playerData = RealScoreboard.getInstance().getDatabaseManager().getPlayerData(player.getUniqueId());
        playerData.setScoreboardON(true);
        RealScoreboard.getInstance().getDatabaseManager().savePlayerData(playerData, true);
    }

    // Disable RealScoreboard for player when McMMO creates its own
    @EventHandler
    public void onMake(McMMOScoreboardMakeboardEvent event) {
        Player player = event.getTargetPlayer();
        PlayerData playerData = RealScoreboard.getInstance().getDatabaseManager().getPlayerData(player.getUniqueId());
        playerData.setScoreboardON(false);
        RealScoreboard.getInstance().getDatabaseManager().savePlayerData(playerData, true);
    }
}
