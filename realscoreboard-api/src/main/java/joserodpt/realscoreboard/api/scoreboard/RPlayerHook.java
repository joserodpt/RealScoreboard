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
import joserodpt.realscoreboard.api.config.PlayerData;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.utils.Text;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.stream.Collectors;

public class RPlayerHook {

    private Player p;
    private RScoreboard current;
    private FastBoard fastBoard;
    private BukkitTask scoreboardRefreshTask;
    private boolean realScoreboardVisible;

    public RPlayerHook(Player p, boolean realScoreboardVisible) {
        this.p = p;
        this.realScoreboardVisible = realScoreboardVisible;
        this.setScoreboard(RealScoreboardAPI.getInstance().getScoreboardManager().getScoreboardForPlayer(p));
        if (this.realScoreboardVisible)
            this.startScoreboard();
    }

    public void startScoreboard() {
        if (this.current != null) {
            this.realScoreboardVisible = true;
            this.fastBoard = new FastBoard(p);
            this.scoreboardRefreshTask = new BukkitRunnable() {
                @Override
                public void run() {
                    setScoreboard(RealScoreboardAPI.getInstance().getScoreboardManager().getScoreboardForPlayer(p));

                    String title = current.getTitle();
                    if (RSBConfig.file().getBoolean("Config.Use-Placeholders-In-Scoreboard-Titles")) {
                        title = RealScoreboardAPI.getInstance().getPlaceholders().setPlaceHolders(p, title);
                    }
                    fastBoard.updateTitle(Text.color(title));

                    List<String> list = current.getLines().stream().map(s -> {
                        s = s.matches("(?i)%blank%") ?
                                (Text.randomColor() + "§r" + Text.randomColor()) :
                                RealScoreboardAPI.getInstance().getPlaceholders().setPlaceHolders(p, s);
                        return Text.color(s);
                    }).collect(Collectors.toList());
                    fastBoard.updateLines(list);
                }
            }.runTaskTimerAsynchronously(RealScoreboardAPI.getInstance().getPlugin(), 0L, current.globalScoreboardRefresh);
        }
    }

    public void stopScoreboard() {
        if (this.scoreboardRefreshTask != null) { this.scoreboardRefreshTask.cancel(); }
        if (this.fastBoard != null && !this.fastBoard.isDeleted()) { this.fastBoard.delete(); this.fastBoard = null; }
        this.realScoreboardVisible = false;
    }

    public void setScoreboard(RScoreboard sb) {
        this.current = sb;
    }

    public boolean isRealScoreboardVisible() {
        return realScoreboardVisible;
    }

    public void setRealScoreboardVisible(boolean realScoreboardVisible) {
        if (realScoreboardVisible != this.realScoreboardVisible) {
            if (realScoreboardVisible) {
                startScoreboard();
            } else {
                stopScoreboard();
            }
        }
        this.realScoreboardVisible = realScoreboardVisible;
        PlayerData playerData = RealScoreboardAPI.getInstance().getDatabaseManager().getPlayerData(p.getUniqueId());
        playerData.setScoreboardON(this.realScoreboardVisible);
        RealScoreboardAPI.getInstance().getDatabaseManager().savePlayerData(playerData, true);
    }

    public RScoreboard getScoreboard() {
        return this.current;
    }

    @Override
    public String toString() {
        return "RPlayerHook{" +
                "player=" + p +
                ", current=" + current +
                ", fastBoard=" + fastBoard +
                ", scoreboardRefreshTask=" + scoreboardRefreshTask +
                '}';
    }
}
