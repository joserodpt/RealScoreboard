package josegamerpt.realscoreboard;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.config.Data;
import josegamerpt.realscoreboard.fastscoreboard.FastBoard;
import josegamerpt.realscoreboard.managers.PlayerManager;
import josegamerpt.realscoreboard.utils.Placeholders;
import josegamerpt.realscoreboard.utils.Text;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreboardTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Map.Entry<Player, FastBoard> playerFastBoardEntry : PlayerManager.sb.entrySet()) {
            Player p = playerFastBoardEntry.getKey();
            FastBoard fb = playerFastBoardEntry.getValue();
            PlayerData playerData = RealScoreboard.getDatabaseManager().getPlayerData(p.getUniqueId());
            if (Config.file().getList("Config.Disabled-Worlds").contains(p.getWorld().getName()) || !playerData.isScoreboardON()) {
                if (!fb.isDeleted() || fb.getLines().size() != 0) {
                    fb.updateLines();
                }
                return;
            }

            try {
                List<String> lista = Config.file()
                        .getStringList("Config.Scoreboard." + Data.getCorrectPlace(p) + ".Lines");

                List<String> send = new ArrayList<>();

                for (String string : lista) {
                    if (string.equalsIgnoreCase("%blank%")) {
                        send.add(Text.randomColor() + "§r" + Text.randomColor());
                    } else {
                        send.add(Placeholders.setPlaceHolders(p, string));
                    }
                }

                String title = RealScoreboard.getAnimationManager().getTitleAnimation(p.getWorld().getName());

                if (Config.file().getBoolean("Config.Use-Placeholders-In-Scoreboard-Titles")) {
                    title = Placeholders.setPlaceHolders(p, title);
                }

                fb.updateTitle(IridiumColorAPI.process(title));
                fb.updateLines(IridiumColorAPI.process(send));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
