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

import joserodpt.realscoreboard.api.RealScoreboardAPI;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class RBoard {
    protected int titleIndex;
    protected BukkitTask titleLooperTask;
    private final List<String> title, lines;
    private RScoreboard rsb;

    public RBoard(List<String> title, List<String> lines) {
        this.title = title;
        this.lines = lines;
    }

    public RBoard(final RScoreboard rsb, List<String> title, List<String> lines) {
        this(title, lines);
        this.rsb = rsb;
    }

    /**
     * Gets list of scoreboard titles
     *
     * @return list of scoreboard titles
     */
    public List<String> getTitleList() {
        return this.title;
    }

    /**
     * Gets current title of board
     *
     * @return current title of board
     */
    public String getTitle() {
        return this.getTitleList().get(titleIndex);
    }

    /**
     * Gets list of scoreboard lines
     *
     * @return list of scoreboard lines
     */
    public List<String> getLines() {
        return this.lines;
    }

    public void stopTasks() {
        if (this.titleLooperTask != null) {
            this.titleLooperTask.cancel();
        }
    }

    public void init() {
        if (rsb == null) {
            RealScoreboardAPI.getInstance().getLogger().severe("RBoard with title: " + this. getTitleList().toString() + " doesn't have a Scoreboard assigned in code! (possible bug)");
            return;
        }

        titleIndex = 0;
        if (title.size() > 1) {
            this.titleLooperTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (titleIndex == title.size() - 1) {
                        titleIndex = 0;
                    } else {
                        ++titleIndex;
                    }
                }
            }.runTaskTimerAsynchronously(RealScoreboardAPI.getInstance().getPlugin(), 0L, rsb.titleLoopDelay);
        }
    }

    public void setScoreboard(RScoreboard rsb) {
        this.rsb = rsb;
    }
}