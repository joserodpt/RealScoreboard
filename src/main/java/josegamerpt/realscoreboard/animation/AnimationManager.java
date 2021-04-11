package josegamerpt.realscoreboard.animation;

import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.config.Config;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;

public class AnimationManager {

    private final HashMap<String, TextLooper> titleAnimations = new HashMap<>();
    private final HashMap<String, TextLooper> loopAnimations = new HashMap<>();

    private BukkitTask title;
    private BukkitTask looper;

    public AnimationManager() {
        start();
    }

    public void start() {
        loadAnimations();
        runLoopers();
    }

    private void loadAnimations() {
        //loops
        loopAnimations.put("rainbow", new TextLooper("rainbow", Arrays.asList("&c", "&6", "&e", "&a", "&b", "&9", "&3", "&d")));
    }

    public String getLoopAnimation(String s) {
        return loopAnimations.containsKey(s) ? loopAnimations.get(s).get() : "? not found";
    }

    public void stop() {
        cancelAnimationTasks();

        titleAnimations.clear();
        loopAnimations.clear();
    }

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
            public void run() {
                titleAnimations.forEach((s, textLooper) -> textLooper.next());
            }
        }.runTaskTimerAsynchronously(RealScoreboard.getInstance(), 0L, Config.file().getInt("Config.Animations.Title-Delay"));
        looper = new BukkitRunnable() {
            public void run() {
                loopAnimations.forEach((s, textLooper) -> textLooper.next());
            }
        }.runTaskTimerAsynchronously(RealScoreboard.getInstance(), 0L, Config.file().getInt("Config.Animations.Loop-Delay"));
    }

    public void reload() {
        stop();
        start();
    }
}