package josegamerpt.realscoreboard.managers;

import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.config.Data;
import josegamerpt.realscoreboard.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class AnimationManager {

    private BukkitTask title;
    private BukkitTask rainbow;
    static HashMap<UUID, String> titleAnim = new HashMap<>();
    static int i = 0;

    public AnimationManager() {
        runTitle();
        runRainbow();
    }

    public void cancelAnimationTasks() {
        if (title != null && !title.isCancelled()) {
            title.cancel();
        }
        if (rainbow != null && !rainbow.isCancelled()) {
            rainbow.cancel();
        }
        titleAnim.clear();
    }


    private void runTitle() {
        title = new BukkitRunnable() {
            public void run() {
                Bukkit.getOnlinePlayers().forEach(AnimationManager::startTitleAnimation);
            }
        }.runTaskTimer(RealScoreboard.getPlugin(), 0L, Config.file().getInt("Config.Animations.Title-Delay"));
    }

    private void runRainbow() {
        rainbow = new BukkitRunnable() {
            public void run() {
                Text.startAnimation();
            }
        }.runTaskTimer(RealScoreboard.getPlugin(), 0L, Config.file().getInt("Config.Animations.Rainbow-Delay"));
    }

    public static void startTitleAnimation(Player p) {
        String go = "Config.Scoreboard." + Data.getCorrectPlace(p) + ".Title";
        try {
            if (i >= Config.file().getStringList(go).size()) {
                i = 0;
            }
            titleAnim.put(p.getUniqueId(), Text.color(Config.file().getStringList(go).get(i)));
            i++;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getTitleAnimation(Player p) {
        if (titleAnim.get(p.getUniqueId()) != null)
        {
            return titleAnim.get(p.getUniqueId());
        }
        return Text.getPrefix();
    }
}
