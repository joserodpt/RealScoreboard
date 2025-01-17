package joserodpt.realscoreboard.listeners;

/*
 *   ____            _ ____                     _                         _
 *  |  _ \ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |
 *  | |_) / _ \/ _` | \___ \ / __/ _ \| '__/ _ \ '_ \ / _ \ / _` | '__/ _` |
 *  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |
 *  |_| \_\___|\__,_|_|____/ \___\___/|_|  \___|_.__/ \___/ \__,_|_|  \__,_|
 *
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2016-2025
 * @link https://github.com/joserodpt/RealScoreboard
 */

import com.gmail.nossr50.events.scoreboard.McMMOScoreboardMakeboardEvent;
import com.gmail.nossr50.events.scoreboard.McMMOScoreboardRevertEvent;
import joserodpt.realscoreboard.RealScoreboard;
import joserodpt.realscoreboard.api.config.PlayerData;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public class McMMOScoreboardListener implements Listener {

    private final RealScoreboard plugin;

    @EventHandler
    public void onRevert(McMMOScoreboardRevertEvent event) {
        Player player = event.getTargetPlayer();
        PlayerData playerData = this.plugin.getDatabaseManagerAPI().getPlayerData(player.getUniqueId());
        playerData.setScoreboardON(true);
        this.plugin.getDatabaseManagerAPI().savePlayerData(playerData, true);
    }

    @EventHandler
    public void onMake(McMMOScoreboardMakeboardEvent event) {
        Player player = event.getTargetPlayer();
        PlayerData playerData = this.plugin.getDatabaseManagerAPI().getPlayerData(player.getUniqueId());
        playerData.setScoreboardON(false);
        this.plugin.getDatabaseManagerAPI().savePlayerData(playerData, true);
    }
}