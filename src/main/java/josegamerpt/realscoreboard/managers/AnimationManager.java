package josegamerpt.realscoreboard.managers;

import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.config.Data;
import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AnimationManager {

    public static int refresh = 20;

    public static void startAnimations() {
        runTitle();
    }

    public static void runTitle() {
        new BukkitRunnable() {
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> executeFor(player));
            }
        }.runTaskTimer(RealScoreboard.getPL(), 0L, Config.file().getInt("Config.Animations.Title-Delay"));
    }

    public static void executeFor(Player p) { TitleManager.startAnimation(p); }

}
