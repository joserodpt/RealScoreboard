package josegamerpt.realscoreboard.api.scoreboard;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import fr.mrmicky.fastboard.FastBoard;
import josegamerpt.realscoreboard.api.RealScoreboardAPI;
import josegamerpt.realscoreboard.api.config.Config;
import josegamerpt.realscoreboard.api.config.PlayerData;
import josegamerpt.realscoreboard.api.utils.Text;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ScoreboardTask extends BukkitRunnable {

    private final Player player;
    private final FastBoard fastBoard;
    private final RealScoreboardAPI rs;

    public ScoreboardTask(Player p, RealScoreboardAPI rs) {
        this.player = p;
        this.fastBoard = new FastBoard(p);
        this.rs = rs;
    }

    @Override
    public void run() {
        if (!this.player.isOnline()) {
            this.cancel();
            return;
        }
        PlayerData playerData = RealScoreboardAPI.getInstance().getDatabaseManager().getPlayerData(this.player.getUniqueId());
        if (Objects.requireNonNull(Config.file().getList("Config.Disabled-Worlds")).
                contains(this.player.getWorld().getName()) || !playerData.isScoreboardON()) {
            if (!this.fastBoard.isDeleted() || this.fastBoard.getLines().size() != 0) {
                this.fastBoard.updateLines();
            }
            return;
        }
        try {
            RScoreboard rsb = this.rs.getScoreboardManager().getScoreboard(this.player);
            List<String> list = rsb.getLines().stream().map(s -> {
                s = s.matches("(?i)%blank%") ?
                        (Text.randomColor() + "§r" + Text.randomColor()) :
                        rs.getPlaceholders().setPlaceHolders(player, s);
                return IridiumColorAPI.process(s);
            }).collect(Collectors.toList());
            if (Config.file().getBoolean("Config.ItemAdder-Support")) {
                list = list.stream().map(s -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    String[] split = s.split(" ");
                    for (int i = 0; i < split.length; i++) {
                        String space = " ";
                        if (i == split.length - 1) space = "";
                        String splitWord = split[i];
                        String[] splitWordSplit = splitWord.split(":");
                        if (splitWordSplit.length < 2) {
                            stringBuilder.append(splitWord).append(space);
                            continue;
                        }
                        stringBuilder.append("§f%img_").append(splitWordSplit[1]).append("%").append(space);
                    }
                    return rs.getPlaceholders().setPlaceHolders(player, stringBuilder.toString());
                }).collect(Collectors.toList());
            }
            String title = rsb.getTitle();
            if (Config.file().getBoolean("Config.Use-Placeholders-In-Scoreboard-Titles")) {
                title = this.rs.getPlaceholders().setPlaceHolders(this.player, title);
            }
            this.fastBoard.updateTitle(IridiumColorAPI.process(title));
            this.fastBoard.updateLines(list);
        } catch (Exception e) {
            rs.getLogger().log(Level.SEVERE, "[ERROR] RealScoreboard threw an error while trying to display the scoreboard for " + this.player.getName());
            e.printStackTrace();
        }
    }
}