package josegamerpt.realscoreboard.managers;

import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.fastscoreboard.FastBoard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class PlayerManager implements Listener {

    public static HashMap<Player, FastBoard> sb = new HashMap<>();

    public static void load(Player p) {
       check(p);
    }

    public static void unload(Player p) {
        sb.remove(p);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void join(PlayerJoinEvent e) {
        load(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void leave(PlayerQuitEvent e) {
        unload(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void changeWorld(PlayerChangedWorldEvent e)
    {
        check(e.getPlayer());
    }

    public static void check(Player p)
    {
        if (Config.file().getList("Config.Bypass-Worlds").contains(p.getWorld().getName())) {
            sb.get(p).delete();
            unload(p);
        } else {
            sb.put(p, new FastBoard(p));
        }
    }

}
