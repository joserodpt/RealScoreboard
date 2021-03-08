package josegamerpt.realscoreboard;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.config.Data;
import josegamerpt.realscoreboard.fastscoreboard.FastBoard;
import josegamerpt.realscoreboard.managers.PlayerManager;
import josegamerpt.realscoreboard.utils.Placeholders;
import josegamerpt.realscoreboard.utils.Text;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoreboardTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Map.Entry<Player, FastBoard> playerFastBoardEntry : PlayerManager.sb.entrySet()) {
            Player player = playerFastBoardEntry.getKey();
            FastBoard fastBoard = playerFastBoardEntry.getValue();
            PlayerData playerData = RealScoreboard.getInstance().getDatabaseManager().getPlayerData(player.getUniqueId());
            if (Config.file().getList("Config.Disabled-Worlds").contains(player.getWorld().getName()) || !playerData.isScoreboardON()) {
                if (!fastBoard.isDeleted() || fastBoard.getLines().size() != 0) {
                    fastBoard.updateLines();
                }
                return;
            }

            try {
                List<String> scoreboardLines = Config.file().getStringList("Config.Scoreboard." + Data.getCorrectPlace(player) + ".Lines").stream().map(string -> {
                    if (string.equalsIgnoreCase("%blank%")) {
                        return Text.randomColor() + "§r" + Text.randomColor();
                    } else {
                        return Placeholders.setPlaceHolders(player, string);
                    }
                }).collect(Collectors.toList());

                String title = RealScoreboard.getInstance().getAnimationManager().getTitleAnimation(player.getWorld().getName());

                if (Config.file().getBoolean("Config.Use-Placeholders-In-Scoreboard-Titles")) {
                    title = Placeholders.setPlaceHolders(player, title);
                }

                fastBoard.updateTitle(IridiumColorAPI.process(title));
                fastBoard.updateLines(IridiumColorAPI.process(scoreboardLines));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
