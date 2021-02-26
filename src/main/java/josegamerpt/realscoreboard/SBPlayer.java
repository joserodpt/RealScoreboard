package josegamerpt.realscoreboard;

import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.config.Data;
import josegamerpt.realscoreboard.fastscoreboard.FastBoard;
import josegamerpt.realscoreboard.managers.AnimationManager;
import josegamerpt.realscoreboard.managers.TitleManager;
import josegamerpt.realscoreboard.utils.Placeholders;
import josegamerpt.realscoreboard.utils.Text;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SBPlayer {

    public Player p;
    public FastBoard scoreboard;
    public BukkitTask br;

    public SBPlayer(Player p) {
        this.p = p;
        if (Config.file().getConfigurationSection("PlayerData." + p.getName()) == null) {
            RealScoreboard.log(Level.INFO, "Creating Player Data for " + p.getName());
            Config.file().set("PlayerData." + p.getName() + ".ScoreboardON", true);
            Config.save();
        }
        if (!Config.file().contains("PlayerData." + p.getName() + ".ScoreboardON")) {
            Config.file().set("PlayerData." + p.getName() + ".ScoreboardON", true);
            Config.save();
        }

        AnimationManager.executeFor(p);

        if (Config.file().getBoolean("PlayerData." + p.getName() + ".ScoreboardON")) {
            if (!Config.file().getList("Config.Disabled-Worlds").contains(p.getWorld().getName()))
                start();
        }
    }

    public void stop() {
        if(br != null && !br.isCancelled()) {
            br.cancel();
        }
        
        if (scoreboard == null) {
            return;
        }

        if (!scoreboard.isDeleted())
            scoreboard.delete();
    }

    public void start() {
        stop();
        
        scoreboard = new FastBoard(p);

        br = new BukkitRunnable() {
            public void run() {
                if (scoreboard != null && !scoreboard.isDeleted()) {
                    try {
                        List<String> lista = Config.file()
                                .getStringList("Config.Scoreboard." + Data.getCorrectPlace(p) + ".Lines");

                        List<String> send = new ArrayList<>();

                        for (String it : lista) {
                            String place = Placeholders.setPlaceHolders(p, it);

                            if (it.equalsIgnoreCase("%blank%")) {
                                send.add(Text.randomColor() + "§r" + Text.randomColor());
                            } else {
                                send.add(place);
                            }
                        }

                        scoreboard.updateTitle(TitleManager.getTitleAnimation(p));
                        scoreboard.updateLines(send);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }.runTaskTimer(RealScoreboard.getPL(), 0L, AnimationManager.refresh);
    }


    public void toggle() {
        if (!Config.file().getBoolean("PlayerData." + p.getName() + ".ScoreboardON")) {
            Config.file().set("PlayerData." + p.getName() + ".ScoreboardON", true);
            Config.save();
            if (!Config.file().getList("Config.Disabled-Worlds").contains(p.getWorld().getName())) {
                start();
            }
            p.sendMessage(Text.color(RealScoreboard.getPrefix() + Config.file().getString("Config.Messages.Scoreboard-Toggle.ON")));
        } else {
            Config.file().set("PlayerData." + p.getName() + ".ScoreboardON", false);
            Config.save();
            stop();
            p.sendMessage(Text.color(RealScoreboard.getPrefix() + Config.file().getString("Config.Messages.Scoreboard-Toggle.OFF")));
        }
    }
}