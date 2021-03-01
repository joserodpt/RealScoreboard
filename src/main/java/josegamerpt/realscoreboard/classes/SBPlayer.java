package josegamerpt.realscoreboard.classes;

import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.config.Data;
import josegamerpt.realscoreboard.fastscoreboard.FastBoard;
import josegamerpt.realscoreboard.utils.Placeholders;
import josegamerpt.realscoreboard.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SBPlayer {

    private Player p;
    private FastBoard scoreboard;
    private BukkitTask br;

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
                    try {
                        List<String> lista = Config.file()
                                .getStringList("Config.Scoreboard." + Data.getCorrectPlace(p) + ".Lines");

                        List<String> send = new ArrayList<>();

                        for (String it : lista) {
                            if (it.equalsIgnoreCase("%blank%")) {
                                send.add(Text.randomColor() + "§r" + Text.randomColor());
                            } else {
                                send.add(Placeholders.setPlaceHolders(p, it));
                            }
                        }

                        scoreboard.updateTitle(RealScoreboard.getAnimationManager().getTitleAnimation(p.getWorld().getName()));
                        scoreboard.updateLines(send);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }.runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("RealScoreboard"), 0L, Config.file().getInt("Config.Scoreboard-Refresh"));
    }


    public void toggle() {
        if (!Config.file().getBoolean("PlayerData." + p.getName() + ".ScoreboardON")) {
            Config.file().set("PlayerData." + p.getName() + ".ScoreboardON", true);
            Config.save();
            if (!Config.file().getList("Config.Disabled-Worlds").contains(p.getWorld().getName())) {
                start();
            }
            Text.send(p, Config.file().getString("Config.Messages.Scoreboard-Toggle.ON"));
        } else {
            Config.file().set("PlayerData." + p.getName() + ".ScoreboardON", false);
            Config.save();
            stop();
            Text.send(p, Config.file().getString("Config.Messages.Scoreboard-Toggle.OFF"));
        }
    }

    public Player getPlayer() {
        return this.p;
    }
}