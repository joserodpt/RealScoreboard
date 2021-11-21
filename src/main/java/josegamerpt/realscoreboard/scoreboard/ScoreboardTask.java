package josegamerpt.realscoreboard.scoreboard;

import josegamerpt.iridiumapi.IridiumAPI;
import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.config.PlayerData;
import josegamerpt.realscoreboard.scoreboard.fastscoreboard.FastBoard;
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
        if (!this.player.isOnline())
        {
            this.cancel();
            return;
        }

        PlayerData playerData = RealScoreboard.getInstance().getDatabaseManager().getPlayerData(this.player.getUniqueId());

        if (Config.file().getList("Config.Disabled-Worlds").contains(this.player.getWorld().getName()) || !playerData.isScoreboardON()) {
            if (!this.fastBoard.isDeleted() || this.fastBoard.getLines().size() != 0) {
                this.fastBoard.updateLines();
            }
            return;
        }

        try {
            List<String> scoreboardLines = this.rs.getScoreboardManager().getScoreboard(this.player).getLines().stream().map(string -> {
                if (string.equalsIgnoreCase("%blank%")) {
                    return Text.randomColor() + "§r" + Text.randomColor();
                } else {
                    return rs.getPlaceholders().setPlaceHolders(player, string);
                }
            }).collect(Collectors.toList());

            String title = this.rs.getScoreboardManager().getScoreboard(this.player).getTitle();

            if (Config.file().getBoolean("Config.Use-Placeholders-In-Scoreboard-Titles")) {
                title = this.rs.getPlaceholders().setPlaceHolders(this.player, title);
            }

            this.fastBoard.updateTitle(IridiumAPI.process(title));
            this.fastBoard.updateLines(IridiumAPI.process(scoreboardLines));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
