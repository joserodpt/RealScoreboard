package josegamerpt.realscoreboard.scoreboard;

import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.animation.BoardLooper;
import josegamerpt.realscoreboard.animation.TextLooper;
import josegamerpt.realscoreboard.config.Config;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class RScoreboard {

    String world;
    int interval;
    List<RBoard> boards = new ArrayList<>();

    private final HashMap<String, TextLooper> titleAnimations = new HashMap<>();
    private final HashMap<String, TextLooper> loopAnimations = new HashMap<>();
    private BoardLooper bp;

    public RScoreboard(String world, int interv)
    {
        this.world = world;
        this.interval = interv;
        Config.file().getConfigurationSection("Config.Scoreboard." + this.world + ".Boards").getKeys(false).forEach(s -> this.boards.add(new RBoard(s, Config.file().getStringList("Config.Scoreboard." + this.world + ".Boards." + s + ".Title"), Config.file().getStringList("Config.Scoreboard." + this.world + ".Boards." + s + ".Lines"))));

        this.bp = new BoardLooper(world, this.boards);
        this.boards.forEach(rBoard -> this.titleAnimations.put(rBoard.getWorldBoard(), new TextLooper(rBoard.getWorldBoard(), rBoard.getTitle())));
        start();
    }

    public List<RBoard> getBoards()
    {
        return this.boards;
    }

    public RBoard getBoard()
    {
        return this.bp.getBoard();
    }

    public List<String> getLines() {
        return this.bp.getBoard().getLines();
    }

    public String getTitle() {
        return this.titleAnimations.get(this.getBoard().getWorldBoard()).get();
    }

    private BukkitTask switche;
    private BukkitTask title;
    private BukkitTask looper;

    public void start() {
        runLoopers();
    }

    public void stop() {
        cancelAnimationTasks();

        titleAnimations.clear();
        loopAnimations.clear();
    }

    public void cancelAnimationTasks() {
        if (this.switche != null && !this.switche.isCancelled()) {
            this.switche.cancel();
        }
        if (this.title != null && !this.title.isCancelled()) {
            this.title.cancel();
        }
        if (this.looper != null && !this.looper.isCancelled()) {
            this.looper.cancel();
        }
    }

    private void runLoopers() {
        this.switche = new BukkitRunnable() {
            public void run() {
                bp.next();
            }
        }.runTaskTimerAsynchronously(RealScoreboard.getInstance(), 0L, this.interval);
        this.title = new BukkitRunnable() {
            public void run() {
                titleAnimations.forEach((s, textLooper) -> textLooper.next());
            }
        }.runTaskTimerAsynchronously(RealScoreboard.getInstance(), 0L, Config.file().getInt("Config.Animations.Title-Delay"));
        this.looper = new BukkitRunnable() {
            public void run() {
                loopAnimations.forEach((s, textLooper) -> textLooper.next());
            }
        }.runTaskTimerAsynchronously(RealScoreboard.getInstance(), 0L, Config.file().getInt("Config.Animations.Loop-Delay"));
    }

}
