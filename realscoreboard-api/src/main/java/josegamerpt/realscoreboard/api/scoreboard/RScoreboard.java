package josegamerpt.realscoreboard.api.scoreboard;

import josegamerpt.realscoreboard.api.animation.BoardLooper;
import josegamerpt.realscoreboard.api.animation.TextLooper;
import josegamerpt.realscoreboard.api.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class RScoreboard {

    String world;
    String permission;
    int interval;
    List<RBoard> boards = new ArrayList<>();

    private final HashMap<String, TextLooper>
            titleAnimations = new HashMap<>(),
            loopAnimations = new HashMap<>();

    private final BoardLooper bp;

    private boolean valid = true;

    //config error scoreboard.
    public RScoreboard(Player p) {
        this.valid = false;
        this.world = p.getWorld().getName();
        this.interval = 100;
        this.permission = "default";
        this.boards.add(new RBoard(this.world, Collections.singletonList("&c&lCONFIG ERROR"), Arrays.asList("&c&lCONFIG ERROR", "&6&lCHECK CONSOLE")));
        this.bp = new BoardLooper(this.world, this.boards);
        this.start();
    }

    public RScoreboard(String world, String perm, int interv)
    {
        this.world = world;
        this.interval = interv;
        this.permission = perm;
        Config.file().getSection("Config.Scoreboard." + this.world + "." + this.permission + ".Boards")
                .getRoutesAsStrings(false).forEach(s -> this.boards.add(new RBoard(s, Config.file().getStringList("Config.Scoreboard." + this.world + "." + this.permission + ".Boards." + s + ".Title"), Config.file().getStringList("Config.Scoreboard." + this.world + "." + this.permission + ".Boards." + s + ".Lines"))));

        this.boards.forEach(rBoard -> this.titleAnimations.put(rBoard.getWorldBoard(), new TextLooper(rBoard.getWorldBoard(), rBoard.getTitle())));
        this.bp = new BoardLooper(this.world, this.boards);
        this.start();
    }

    public List<RBoard> getBoards()
    {
        return this.boards;
    }

    public RBoard getBoard()
    {
        return this.bp.getBoard();
    }

    public String getInternalPermission()
    {
        return this.permission;
    }

    public String getPermission()
    {
        return "realscoreboard." + this.world + "." + this.permission;
    }

    public List<String> getLines() {
        return this.valid ? this.bp.getBoard().getLines() : Arrays.asList("&c&LCONFIG ERROR", "&6&LCHECK CONSOLE");
    }

    public String getTitle() {
        return this.valid ? this.titleAnimations.get(this.getBoard().getWorldBoard()).get() : "&c&LCONFIG ERROR";
    }

    private BukkitTask switches;
    private BukkitTask title;
    private BukkitTask looper;

    public void start() {
        this.runLoopers();
    }

    public void stop() {
        this.cancelAnimationTasks();

        this.titleAnimations.clear();
        this.loopAnimations.clear();
    }

    public void cancelAnimationTasks() {
        if (this.switches != null && !this.switches.isCancelled()) {
            this.switches.cancel();
        }
        if (this.title != null && !this.title.isCancelled()) {
            this.title.cancel();
        }
        if (this.looper != null && !this.looper.isCancelled()) {
            this.looper.cancel();
        }
    }

    private void runLoopers() {
        this.switches = new BukkitRunnable() {
            public void run() {
                bp.next();
            }
        }.runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("RealScoreboard"), 0L, this.interval);
        this.title = new BukkitRunnable() {
            public void run() {
                titleAnimations.forEach((s, textLooper) -> textLooper.next());
            }
        }.runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("RealScoreboard"), 0L, Config.file().getInt("Config.Animations.Title-Delay"));
        this.looper = new BukkitRunnable() {
            public void run() {
                loopAnimations.forEach((s, textLooper) -> textLooper.next());
            }
        }.runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("RealScoreboard"), 0L, Config.file().getInt("Config.Animations.Loop-Delay"));
    }

}
