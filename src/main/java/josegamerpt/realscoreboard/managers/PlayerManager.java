package josegamerpt.realscoreboard.managers;

import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.classes.SBPlayer;
import josegamerpt.realscoreboard.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class PlayerManager implements Listener {

    public static ArrayList<SBPlayer> players = new ArrayList<>();

    public static SBPlayer getPlayer(Player p) {
        for (SBPlayer player : players) {
            return player.getPlayer() == p ? player : null;
        }
        return null;
    }

    public static void loadPlayer(Player p) {
        players.add(new SBPlayer(p));
    }

    private static void unloadPlayer(SBPlayer sb) {
        players.remove(sb);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        loadPlayer(e.getPlayer());
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        SBPlayer sb = PlayerManager.getPlayer(e.getPlayer());
        if (sb != null) {
            sb.stop();
            PlayerManager.unloadPlayer(sb);
        }
    }

    @EventHandler
    public void changeWorld(PlayerChangedWorldEvent e) {
        SBPlayer player = PlayerManager.getPlayer(e.getPlayer());

        new BukkitRunnable() {
            public void run() {
                if (Config.file().getList("Config.Disabled-Worlds").contains(e.getPlayer().getWorld().getName())) {
                    if (player != null) {
                        player.stop();
                    }
                } else {
                    if (Config.file().getBoolean("PlayerData." + e.getPlayer().getName() + ".ScoreboardON")) {
                        if (player != null) {
                            player.start();
                        }
                    }
                }
            }
        }.runTaskLaterAsynchronously(RealScoreboard.getPlugin(), 5);
    }
}
