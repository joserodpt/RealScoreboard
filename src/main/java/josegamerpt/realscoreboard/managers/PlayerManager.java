package josegamerpt.realscoreboard.managers;

import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.fastscoreboard.FastBoard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.logging.Level;

public class PlayerManager implements Listener {

    public static HashMap<Player, FastBoard> sb = new HashMap<>();

    public static void load(Player p) {
        sb.put(p, new FastBoard(p));
        if (Config.file().getConfigurationSection("PlayerData." + p.getName()) == null) {
            RealScoreboard.log(Level.INFO, "Creating Player Data for " + p.getName());
            Config.file().set("PlayerData." + p.getName() + ".ScoreboardON", true);
            Config.save();
        }
        if (!Config.file().contains("PlayerData." + p.getName() + ".ScoreboardON")) {
            Config.file().set("PlayerData." + p.getName() + ".ScoreboardON", true);
            Config.save();
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        load(e.getPlayer());
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        sb.remove(e.getPlayer());
    }

}
