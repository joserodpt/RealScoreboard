package josegamerpt.realscoreboard.managers;

import josegamerpt.realscoreboard.classes.TextLooper;
import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.config.Data;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;

public class AnimationManager {

    private HashMap<String, TextLooper> titleAnimations = new HashMap<>();
    private HashMap<String, TextLooper> loopAnimations = new HashMap<>();

    private JavaPlugin plugin;

    private BukkitTask title;
    private BukkitTask looper;

    public AnimationManager(JavaPlugin jp) {
        this.plugin = jp;
        start();
    }

    public void start() {
        loadAnimations();
        runLoopers();
    }

    private void loadAnimations() {
        //titles
        for (String path : Config.file().getConfigurationSection("Config.Scoreboard").getKeys(false)) {
            titleAnimations.put(path, new TextLooper(path, Config.file().getStringList("Config.Scoreboard." + path + ".Title")));
        }
        //loops
        loopAnimations.put("rainbow", new TextLooper("rainbow", Arrays.asList("&c", "&6", "&e", "&a", "&b", "&9", "&3", "&d")));
    }

    public String getTitleAnimation(String s) {
        return titleAnimations.containsKey(s) ? titleAnimations.get(s).get() : titleAnimations.get(Data.getRegisteredWorlds().get(0)).get();
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
        }.runTaskTimerAsynchronously(plugin, 0L, Config.file().getInt("Config.Animations.Title-Delay"));
        looper = new BukkitRunnable() {
            public void run() {
                loopAnimations.forEach((s, textLooper) -> textLooper.next());
            }
        }.runTaskTimerAsynchronously(plugin, 0L, Config.file().getInt("Config.Animations.Loop-Delay"));
    }

    public void reload() {
        stop();
        start();
    }
}
