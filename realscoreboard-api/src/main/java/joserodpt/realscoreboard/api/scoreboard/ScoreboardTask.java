package joserodpt.realscoreboard.api.scoreboard;

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

import fr.mrmicky.fastboard.FastBoard;
import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.config.PlayerData;
import joserodpt.realscoreboard.api.utils.Text;
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

        //hide scoreboard if the player is in a world where it is disabled,
        //or if the player has the scoreboard off (manually or automatically via vanish command)
        if (Objects.requireNonNull(RSBConfig.file().getList("Config.Disabled-Worlds")).
                contains(this.player.getWorld().getName()) || !playerData.isScoreboardON()) {

            if (!this.fastBoard.isDeleted() || !this.fastBoard.getLines().isEmpty()) {
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
                return Text.color(s);
            }).collect(Collectors.toList());

        /* TODO: complete this and add ItemsAdder support again
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
                    // TODO: find better fix in future since we can do better job :)
                    if (!s.contains(":)")) {
                        stringBuilder.append("§f%img_").append(splitWordSplit[1]).append("%").append(space);
                    } else {
                        stringBuilder.append(":)");
                    }
                }
                return rs.getPlaceholders().setPlaceHolders(player, stringBuilder.toString());
            }).collect(Collectors.toList());
        } */

            String title = rsb.getTitle();
            if (RSBConfig.file().getBoolean("Config.Use-Placeholders-In-Scoreboard-Titles")) {
                title = this.rs.getPlaceholders().setPlaceHolders(this.player, title);
            }
            this.fastBoard.updateTitle(Text.color(title));
            this.fastBoard.updateLines(list);
        } catch (Exception e) {
            rs.getLogger().log(Level.SEVERE, "[ERROR] RealScoreboard threw an error while trying to display the scoreboard for " + this.player.getName());
            rs.getLogger().log(Level.SEVERE, e.getMessage());
        }
    }
}
