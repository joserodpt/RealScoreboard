package josegamerpt.realscoreboard.managers;

import josegamerpt.realscoreboard.fastscoreboard.FastBoard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class PlayerManager implements Listener {

    public static HashMap<Player, FastBoard> sb = new HashMap<>();

    public static void load(Player p) {
        sb.put(p, new FastBoard(p));
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
