package josegamerpt.realscoreboard.scoreboard;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.config.PlayerData;
import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.config.Data;
import josegamerpt.realscoreboard.scoreboard.fastscoreboard.FastBoard;
import josegamerpt.realscoreboard.utils.Placeholders;
import josegamerpt.realscoreboard.utils.Text;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardTask extends BukkitRunnable {

    private Player player;
    private FastBoard fastBoard;
    private RealScoreboard rs;

    public ScoreboardTask(Player p, RealScoreboard rs) {
        this.player = p;
        this.fastBoard = new FastBoard(p);
        this.rs = rs;
    }

    @Override
    public void run() {
        if (!player.isOnline())
        {
            this.cancel();
            return;
        }

        PlayerData playerData = RealScoreboard.getInstance().getDatabaseManager().getPlayerData(player.getUniqueId());

        if (Config.file().getList("Config.Disabled-Worlds").contains(player.getWorld().getName()) || !playerData.isScoreboardON()) {
            if (!fastBoard.isDeleted() || fastBoard.getLines().size() != 0) {
                fastBoard.updateLines();
            }
            return;
        }

        try {
            List<String> scoreboardLines = this.rs.getScoreboardManager().getScoreboard(Data.getCorrectPlace(player)).getLines().stream().map(string -> {
                if (string.equalsIgnoreCase("%blank%")) {
                    return Text.randomColor() + "§r" + Text.randomColor();
                } else {
                    return Placeholders.setPlaceHolders(player, string);
                }
            }).collect(Collectors.toList());

            String title = this.rs.getScoreboardManager().getScoreboard(Data.getCorrectPlace(player)).getTitle();

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
