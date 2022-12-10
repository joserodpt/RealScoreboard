package josegamerpt.realscoreboard.animation;

import josegamerpt.realscoreboard.RealScoreboardPlugin;
import josegamerpt.realscoreboard.api.animation.TextLooper;
import josegamerpt.realscoreboard.api.config.Config;
import josegamerpt.realscoreboard.api.managers.AbstractAnimationManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;

public class AnimationManager extends AbstractAnimationManager {

    private final HashMap<String, TextLooper> titleAnimations = new HashMap<>();
    private final HashMap<String, TextLooper> loopAnimations = new HashMap<>();

    private BukkitTask title;
    private BukkitTask looper;

    public AnimationManager() {
        start();
    }

    @Override
    public void start() {
        loadAnimations();
        runLoopers();
    }

    private void loadAnimations() {
        //loops
        loopAnimations.put("rainbow", new TextLooper("rainbow", Arrays.asList("&c", "&6", "&e", "&a", "&b", "&9", "&3", "&d")));
    }

    @Override
    public String getLoopAnimation(String s) {
        return loopAnimations.containsKey(s) ? loopAnimations.get(s).get() : "? not found";
    }

    @Override
    public void stop() {
        cancelAnimationTasks();
        titleAnimations.clear();
        loopAnimations.clear();
    }

    @Override
    public void cancelAnimationTasks() {
        if (title != null && !title.isCancelled()) {
            title.cancel();
        }
        if (looper != null && !looper.isCancelled()) {
            looper.cancel();
        }
    }

    private void runLoopers() {
        title = new BukkitRunnable() {
            @Override
            public void run() {
                titleAnimations.forEach((s, textLooper) -> textLooper.next());
            }
        }.runTaskTimerAsynchronously(RealScoreboardPlugin.getInstance(), 0L, Config.file().getInt("Config.Animations.Title-Delay"));
        looper = new BukkitRunnable() {
            @Override
            public void run() {
                loopAnimations.forEach((s, textLooper) -> textLooper.next());
            }
        }.runTaskTimerAsynchronously(RealScoreboardPlugin.getInstance(), 0L, Config.file().getInt("Config.Animations.Loop-Delay"));
    }

    @Override
    public void reload() {
        stop();
        start();
    }
}