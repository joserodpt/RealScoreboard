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
 * @author José Rodrigues © 2016-2025
 * @link https://github.com/joserodpt/RealScoreboard
 */

import fr.mrmicky.fastboard.FastBoard;
import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.config.PlayerData;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.external.ExternalBoard;
import joserodpt.realscoreboard.api.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * A player's scoreboard state.
 * <p>
 * Whether a board is on screen is <b>derived</b>, not stored: every refresh
 * re-decides from the board another plugin pushed, the world, and the player's
 * saved preference, in that order. That is what keeps a pushed board alive
 * across a world change, and what lets a forced one outrank a player who has
 * their scoreboard turned off, without ever touching their saved preference.
 */
public class RSBPlayer {

    private static final int FALLBACK_REFRESH = 5;

    private final Player p;
    private RScoreboard current;
    private FastBoard fastBoard;
    private BukkitTask scoreboardRefreshTask;
    private int taskRefreshTicks;

    /**
     * Last resolved content, so a board created on the main thread can be filled
     * immediately instead of staying blank until the next refresh.
     */
    private volatile String pendingTitle;
    private volatile List<String> pendingLines = Collections.emptyList();
    /** Guards against queueing more than one create/delete at a time. */
    private final AtomicBoolean boardChangeQueued = new AtomicBoolean();

    public RSBPlayer(Player p) {
        this.p = p;
        this.current = RealScoreboardAPI.getInstance().getScoreboardManagerAPI().getScoreboardForPlayer(p);
        this.startScoreboard();
    }

    /**
     * Starts the refresh task. The task stays alive while the player is online
     * even when nothing is being shown, because it is what notices that
     * something should start being shown again.
     */
    public void startScoreboard() {
        int refresh = this.refreshTicks();
        if (this.scoreboardRefreshTask != null && this.taskRefreshTicks == refresh) {
            return;
        }

        if (this.scoreboardRefreshTask != null) {
            this.scoreboardRefreshTask.cancel();
        }

        this.taskRefreshTicks = refresh;
        this.scoreboardRefreshTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline()) {
                    this.cancel();
                    scoreboardRefreshTask = null;
                    return;
                }

                resolve();
            }
        }.runTaskTimerAsynchronously(RealScoreboardAPI.getInstance().getPlugin(), 0L, refresh);
    }

    /**
     * Decides what should be on screen and applies it. Runs asynchronously.
     */
    private void resolve() {
        ExternalBoard external = RealScoreboardAPI.getInstance().getExternalScoreboardManagerAPI().getBoard(p);

        //a forced board outranks both the disabled worlds and the player's toggle
        if (external != null && external.isForced()) {
            this.show(external.getTitle(), external.getLines());
            return;
        }

        if (this.isWorldDisabled() || !this.isRealScoreboardVisible()) {
            this.hide();
            return;
        }

        if (external != null) {
            this.show(external.getTitle(), external.getLines());
            return;
        }

        if (this.current != null) {
            this.show(this.current.getTitle(), this.current.getLines());
        } else {
            this.hide();
        }
    }

    private void show(String title, List<String> lines) {
        if (title == null || lines == null) {
            this.hide();
            return;
        }

        if (RSBConfig.file().getBoolean("Config.Use-Placeholders-In-Scoreboard-Titles")) {
            title = RealScoreboardAPI.getInstance().getPlaceholders().setPlaceholders(p, title, false);
        }

        List<String> rendered = lines.stream()
                .map(s -> RealScoreboardAPI.getInstance().getPlaceholders().setPlaceholders(p, s, false))
                .filter(s -> !s.contains("$skip"))
                .map(s -> {
                    s = s.matches("(?i)%blank%") ?
                            (Text.randomColor() + "§r" + Text.randomColor()) :
                            s;
                    return Text.color(s);
                })
                .collect(Collectors.toList());

        this.pendingTitle = Text.color(title);
        this.pendingLines = rendered;

        if (!this.isScoreboardActive()) {
            this.queueBoardChange(true);
            return;
        }

        this.fastBoard.updateTitle(this.pendingTitle);
        if (!rendered.isEmpty()) {
            this.fastBoard.updateLines(rendered);
        }
    }

    private void hide() {
        if (this.fastBoard != null) {
            this.queueBoardChange(false);
        }
    }

    /**
     * FastBoard is created and deleted on the main thread, matching where those
     * happened before this class refreshed itself, while updates stay async.
     */
    private void queueBoardChange(boolean visible) {
        if (!this.boardChangeQueued.compareAndSet(false, true)) {
            return;
        }

        Bukkit.getScheduler().runTask(RealScoreboardAPI.getInstance().getPlugin(), () -> {
            try {
                if (visible) {
                    if (!p.isOnline()) {
                        return;
                    }
                    if (this.fastBoard == null || this.fastBoard.isDeleted()) {
                        this.fastBoard = new FastBoard(p);
                    }

                    String title = this.pendingTitle;
                    List<String> lines = this.pendingLines;
                    if (title != null) {
                        this.fastBoard.updateTitle(title);
                    }
                    if (lines != null && !lines.isEmpty()) {
                        this.fastBoard.updateLines(lines);
                    }
                } else if (this.fastBoard != null) {
                    if (!this.fastBoard.isDeleted()) {
                        this.fastBoard.delete();
                    }
                    this.fastBoard = null;
                }
            } finally {
                this.boardChangeQueued.set(false);
            }
        });
    }

    /**
     * Cancels the refresh task and removes the board from the screen. Used on
     * quit and on reload; a board that should come back is brought back by
     * {@link #startScoreboard()}.
     */
    public void stopScoreboard() {
        if (this.scoreboardRefreshTask != null) {
            this.scoreboardRefreshTask.cancel();
            this.scoreboardRefreshTask = null;
        }
        if (this.fastBoard != null && !this.fastBoard.isDeleted()) {
            this.fastBoard.delete();
        }
        this.fastBoard = null;
    }

    /**
     * Sets which of RealScoreboard's own scoreboards this player should see.
     * Whether it is actually displayed is decided on the next refresh.
     */
    public void setScoreboard(RScoreboard sb) {
        this.current = sb;

        //a board with a different refresh rate needs the task restarted
        this.startScoreboard();
    }

    public boolean isScoreboardActive() {
        return this.fastBoard != null && !this.fastBoard.isDeleted();
    }

    /**
     * The player's own preference, as saved in the database. A board can still
     * be on screen while this is false, if a plugin forced one.
     */
    public boolean isRealScoreboardVisible() {
        return RealScoreboardAPI.getInstance().getDatabaseManagerAPI().getPlayerData(p.getUniqueId()).isScoreboardON();
    }

    /**
     * Records the player's preference and persists it. The screen follows on the
     * next refresh.
     */
    public void setRealScoreboardVisible(boolean realScoreboardVisible) {
        PlayerData playerData = RealScoreboardAPI.getInstance().getDatabaseManagerAPI().getPlayerData(p.getUniqueId());
        playerData.setScoreboardON(realScoreboardVisible);
        RealScoreboardAPI.getInstance().getDatabaseManagerAPI().savePlayerData(playerData, true);
    }

    private boolean isWorldDisabled() {
        return RSBConfig.file().getStringList("Config.Disabled-Worlds").contains(p.getWorld().getName())
                || RSBConfig.file().getStringList("Config.Bypass-Worlds").contains(p.getWorld().getName());
    }

    private int refreshTicks() {
        return this.current != null && this.current.globalScoreboardRefresh > 0
                ? this.current.globalScoreboardRefresh
                : FALLBACK_REFRESH;
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
