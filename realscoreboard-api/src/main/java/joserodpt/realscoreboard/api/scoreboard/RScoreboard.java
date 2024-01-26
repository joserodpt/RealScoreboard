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

import joserodpt.realscoreboard.api.animation.BoardLooper;
import joserodpt.realscoreboard.api.animation.TextLooper;
import joserodpt.realscoreboard.api.config.RSBConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class RScoreboard {

    private final String world;
    private final String permission;
    private final int interval;
    private final List<RBoard> boards = new ArrayList<>();
    private final HashMap<String, TextLooper>
            titleAnimations = new HashMap<>(),
            loopAnimations = new HashMap<>();
    private final BoardLooper bp;
    private boolean valid = true;
    private BukkitTask switches;
    private BukkitTask title;
    private BukkitTask looper;

    public RScoreboard(Player p) {
        this.valid = false;
        this.world = p.getWorld().getName();
        this.interval = 100;
        this.permission = "default";
        this.boards.add(new RBoard(this.world, Collections.singletonList("&c&lCONFIG ERROR"), Arrays.asList("&c&lCONFIG ERROR", "&6&lCHECK CONSOLE")));
        this.bp = new BoardLooper(this.world, this.boards);
        this.start();
    }

    public RScoreboard(String world, String perm, int interv) {
        this.world = world;
        this.interval = interv;
        this.permission = perm;
        RSBConfig.file().getSection("Config.Scoreboard." + this.world + "." + this.permission + ".Boards")
                .getRoutesAsStrings(false).forEach(s -> this.boards.add(new RBoard(s, RSBConfig.file().getStringList("Config.Scoreboard." + this.world + "." + this.permission + ".Boards." + s + ".Title"), RSBConfig.file().getStringList("Config.Scoreboard." + this.world + "." + this.permission + ".Boards." + s + ".Lines"))));
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
        return this.valid ? this.bp.getBoard().getLines() : Arrays.asList("&c&lCONFIG ERROR", "&6&lCHECK CONSOLE");
    }

    public String getTitle() {
        return this.valid ? this.titleAnimations.get(this.getBoard().getWorldBoard()).get() : "&c&lCONFIG ERROR";
    }

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

    @SuppressWarnings("ConstantConditions")
    private void runLoopers() {
        this.switches = new BukkitRunnable() {
            @Override
            public void run() {
                bp.next();
            }
        }.runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("RealScoreboard"), 0L, this.interval);
        this.title = new BukkitRunnable() {
            @Override
            public void run() {
                titleAnimations.forEach((s, textLooper) -> textLooper.next());
            }
        }.runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("RealScoreboard"), 0L, RSBConfig.file().getInt("Config.Animations.Title-Delay"));
        this.looper = new BukkitRunnable() {
            @Override
            public void run() {
                loopAnimations.forEach((s, textLooper) -> textLooper.next());
            }
        }.runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("RealScoreboard"), 0L, RSBConfig.file().getInt("Config.Animations.Loop-Delay"));
    }
}