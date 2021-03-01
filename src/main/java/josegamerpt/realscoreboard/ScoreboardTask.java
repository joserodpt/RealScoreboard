package josegamerpt.realscoreboard;

import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.config.Data;
import josegamerpt.realscoreboard.fastscoreboard.FastBoard;
import josegamerpt.realscoreboard.managers.PlayerManager;
import josegamerpt.realscoreboard.utils.Placeholders;
import josegamerpt.realscoreboard.utils.Text;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ScoreboardTask extends BukkitRunnable {

    private RealScoreboard plugin;

    public ScoreboardTask(RealScoreboard rs) {
        this.plugin = rs;
    }

    @Override
    public void run() {
        for (Map.Entry<Player, FastBoard> playerFastBoardEntry : PlayerManager.sb.entrySet()) {
            Map.Entry<Player, FastBoard> pair = playerFastBoardEntry;
            Player p = pair.getKey();

            FastBoard fb = pair.getValue();
            if (Config.file().getList("Config.Disabled-Worlds").contains(p.getWorld().getName()) || !Config.file().getBoolean("PlayerData." + p.getName() + ".ScoreboardON")) {
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

                fb.updateTitle(RealScoreboard.getAnimationManager().getTitleAnimation(p.getWorld().getName()));
                fb.updateLines(send);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
