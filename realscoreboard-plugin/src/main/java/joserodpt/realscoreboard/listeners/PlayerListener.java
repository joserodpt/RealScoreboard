package joserodpt.realscoreboard.listeners;

import joserodpt.realscoreboard.RealScoreboardPlugin;
import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.config.PlayerData;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.utils.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final RealScoreboardAPI rsa;
    public PlayerListener(RealScoreboardAPI rsa) {
        this.rsa = rsa;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void join(PlayerJoinEvent e) {
        rsa.getPlayerManager().checkPlayer(e.getPlayer());
        if (e.getPlayer().isOp() && RealScoreboardPlugin.getNewUpdate()) {
            Text.send(e.getPlayer(), "&6&lWARNING &fThere is a new version of RealScoreboard! https://www.spigotmc.org/resources/22928/");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void changeWorld(PlayerChangedWorldEvent e) {
        rsa.getPlayerManager().checkPlayer(e.getPlayer());
    }

    private final String[] vanishCommands = {"/pv", "/vanish", "/premiumvanish"};
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        //check if this hiding behaviour is hidden
        if (RSBConfig.file().getBoolean("Config.Auto-Hide-In-Vanish")) {
            Player p = e.getPlayer();
            String cmd = e.getMessage().split(" ")[0];
            for (String vanishCommand : vanishCommands) {
                if (cmd.equalsIgnoreCase(vanishCommand)) {
                    PlayerData playerData = this.rsa.getDatabaseManager().getPlayerData(p.getUniqueId());
                    //before the command is processed, the vanish tag in the player is still the previous state, so we set it as the previous
                    playerData.setScoreboardON(rsa.getPlayerManager().isVanished(p));
                    this.rsa.getDatabaseManager().savePlayerData(playerData, true);
                    break;
                }
            }
        }
    }
}
