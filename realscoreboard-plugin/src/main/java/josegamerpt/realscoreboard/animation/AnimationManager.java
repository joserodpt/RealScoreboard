package josegamerpt.realscoreboard.animation;

import josegamerpt.realscoreboard.RealScoreboardPlugin;
import josegamerpt.realscoreboard.api.animation.TextLooper;
import josegamerpt.realscoreboard.api.config.Config;
import josegamerpt.realscoreboard.api.managers.AbstractAnimationManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;

public class AnimationManager extends AbstractAnimationManager {

    private final HashMap<String, TextLooper> titleAnimations = new HashMap<>();
    private final HashMap<String, TextLooper> loopAnimations = new HashMap<>();
    private final JavaPlugin plugin;
    private BukkitTask title;
    private BukkitTask looper;

    public AnimationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.start();
    }

    @Override
    public void start() {
        this.loadAnimations();
        this.runLoopers();
    }

    private void loadAnimations() {
        loopAnimations.put("rainbow", new TextLooper("rainbow", Arrays.asList("&c", "&6", "&e", "&a", "&b", "&9", "&3", "&d")));
    }

    @Override
    public String getLoopAnimation(String s) {
        return this.loopAnimations.containsKey(s) ? this.loopAnimations.get(s).get() : "? not found";
    }

    @Override
    public void stop() {
        this.cancelAnimationTasks();
        this.titleAnimations.clear();
        this.loopAnimations.clear();
    }

    @Override
    public void cancelAnimationTasks() {
        if (this.title != null && !this.title.isCancelled()) {
            this.title.cancel();
        }
        if (this.looper != null && !this.looper.isCancelled()) {
            this.looper.cancel();
        }
    }

    private void runLoopers() {
        this.title = new BukkitRunnable() {
            @Override
            public void run() {
                titleAnimations.forEach((s, textLooper) -> textLooper.next());
            }
        }.runTaskTimerAsynchronously(this.plugin, 0L, Config.file().getInt("Config.Animations.Title-Delay"));
        this.looper = new BukkitRunnable() {
            @Override
            public void run() {
                loopAnimations.forEach((s, textLooper) -> textLooper.next());
            }
        }.runTaskTimerAsynchronously(this.plugin, 0L, Config.file().getInt("Config.Animations.Loop-Delay"));
    }

    @Override
    public void reload() {
        this.stop();
        this.start();
    }
}