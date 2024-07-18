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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.stream.Collectors;

public class RSBPlayer {

    private final Player p;
    private RScoreboard current;
    private FastBoard fastBoard;
    private BukkitTask scoreboardRefreshTask;
    private boolean realScoreboardVisible;

    public RSBPlayer(Player p) {
        this.p = p;
        this.realScoreboardVisible = !RSBConfig.file().getStringList("Config.Disabled-Worlds").contains(p.getWorld().getName()) && !RSBConfig.file().getStringList("Config.Bypass-Worlds").contains(p.getWorld().getName()) && RealScoreboardAPI.getInstance().getDatabaseManagerAPI().getPlayerData(p.getUniqueId()).isScoreboardON();
        this.setScoreboard(RealScoreboardAPI.getInstance().getScoreboardManagerAPI().getScoreboardForPlayer(p));
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
                    if (!p.isOnline() || !realScoreboardVisible) {
                        this.cancel();
                        return;
                    }

                    if (current != null) {
                        String title = current.getTitle();
                        if (RSBConfig.file().getBoolean("Config.Use-Placeholders-In-Scoreboard-Titles")) {
                            title = RealScoreboardAPI.getInstance().getPlaceholders().setPlaceholders(p, title);
                        }
                        if (fastBoard != null && !fastBoard.isDeleted())
                            fastBoard.updateTitle(Text.color(title));

                        Collection<String> list = current.getLines().stream().map(s -> {
                            s = s.matches("(?i)%blank%") ?
                                    (Text.randomColor() + "§r" + Text.randomColor()) :
                                    RealScoreboardAPI.getInstance().getPlaceholders().setPlaceholders(p, s);
                            return Text.color(s);
                        }).collect(Collectors.toList());
                        if (fastBoard != null && !fastBoard.isDeleted() && !list.isEmpty())
                            fastBoard.updateLines(list);
                    }
                }
            }.runTaskTimerAsynchronously(RealScoreboardAPI.getInstance().getPlugin(), 0L, current.globalScoreboardRefresh);
        }
    }

    public void stopScoreboard() {
        if (this.scoreboardRefreshTask != null) {
            this.scoreboardRefreshTask.cancel();
        }
        if (this.fastBoard != null && !this.fastBoard.isDeleted()) {
            this.fastBoard.delete();
            this.fastBoard = null;
        }
        this.realScoreboardVisible = false;
    }

    public void setScoreboard(RScoreboard sb) {
        if (RSBConfig.file().getStringList("Config.Disabled-Worlds").contains(p.getWorld().getName())) {
            stopScoreboard();
            return;
        }

        if (sb != null) {
            this.current = sb;
            if (!isScoreboardActive()) {
                startScoreboard();
            }
        } else {
            stopScoreboard();
        }
    }

    public boolean isScoreboardActive() {
        return this.scoreboardRefreshTask != null && this.fastBoard != null && !this.fastBoard.isDeleted();
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
        PlayerData playerData = RealScoreboardAPI.getInstance().getDatabaseManagerAPI().getPlayerData(p.getUniqueId());
        playerData.setScoreboardON(this.realScoreboardVisible);
        RealScoreboardAPI.getInstance().getDatabaseManagerAPI().savePlayerData(playerData, true);
    }

    public RScoreboard getScoreboard() {
        return this.current;
    }

    public Player getPlayer() {
        return p;
    }

    @Override
    public String toString() {
        return "RSBPlayer{" +
                "player=" + p +
                ", current=" + current +
                ", fastBoard=" + fastBoard +
                ", scoreboardRefreshTask=" + scoreboardRefreshTask +
                '}';
    }

    public void announce(String message, Integer seconds) {
        RScoreboard prev = current;
        setScoreboard(new RScoreboardSingle(message));
        Bukkit.getScheduler().runTaskLater(RealScoreboardAPI.getInstance().getPlugin(), () -> setScoreboard(prev), (seconds == null ? 10 : seconds) * 20);
    }
}
