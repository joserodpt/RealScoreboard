package josegamerpt.realscoreboard.managers;

import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.SBPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class PlayerManager implements Listener {

    public static ArrayList<SBPlayer> players = new ArrayList<SBPlayer>();

    public static SBPlayer getPlayer(Player p) {
        for (SBPlayer player : players) {
            if (player.p == p)
            {
                return player;
            }
        }
        return null;
    }

    public static void loadPlayer(Player p) {
        players.add(new SBPlayer(p));
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        loadPlayer(e.getPlayer());
    }


    private static void unloadPlayer(SBPlayer sb) {
        players.remove(sb);
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
    public void changeWorld(PlayerTeleportEvent e) {
        new BukkitRunnable()
        {
            public void run()
            {
                if (Config.file().getList("Config.Disabled-Worlds").contains(e.getPlayer().getWorld().getName()))
                {
                    PlayerManager.getPlayer(e.getPlayer()).stop();
                } else {
                    if (Config.file().getBoolean("PlayerData." + e.getPlayer().getName() + ".ScoreboardON")) {
                        if (PlayerManager.getPlayer(e.getPlayer()) != null) {
                            PlayerManager.getPlayer(e.getPlayer()).start();
                        }
                    }
                }
            }
        }.runTaskLater(RealScoreboard.getPL(), 5);
    }
}
