package joserodpt.realscoreboard.managers;

/*
 *   ____            _ ____                     _                         _
 *  |  _ \ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |
 *  | |_) / _ \/ _` | \___ \ / __/ _ \| '__/ _ \ '_ \ / _ \ / _` | '__/ _` |
 *  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |
 *  |_| \_\___|\__,_|_|____/ \___\___/|_|  \___|_.__/ \___/ \__,_|_|  \__,_|
 *
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealScoreboard
 */

import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.managers.AbstractPlayerManager;
import joserodpt.realscoreboard.api.scoreboard.RSBPlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements AbstractPlayerManager {

    private final Map<UUID, RSBPlayer> playerHooks = new HashMap<>();
    private final RealScoreboardAPI rsa;

    public PlayerManager(RealScoreboardAPI rsa) {
        this.rsa = rsa;
    }

    @Override
    public boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    @Override
    public void initPlayer(Player p) {
        rsa.getPlayerManager().getPlayerMap().put(p.getUniqueId(),
                new RSBPlayer(p, !RSBConfig.file().getList("Config.Bypass-Worlds").contains(p.getWorld().getName()) && rsa.getDatabaseManager().getPlayerData(p.getUniqueId()).isScoreboardON()));
    }

    @Override
    public RSBPlayer getPlayer(UUID uuid) {
        return this.getPlayerMap().get(uuid);
    }

    @Override
    public Map<UUID, RSBPlayer> getPlayerMap() {
        return this.playerHooks;
    }
}