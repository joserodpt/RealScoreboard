package josegamerpt.realscoreboard;

import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.utils.Text;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Command("realscoreboard")
@Alias({"rsb", "sb"})
public class Commands extends CommandBase {

    private RealScoreboard rs;
    public Commands(RealScoreboard r)
    {
        this.rs = r;
    }

    @Default
    public void defaultCommand(final CommandSender commandSender) {
        Text.send(commandSender, Arrays.asList("&7", Text.getPrefix() + "&a" + RealScoreboard.getVersion() + " &bHelp",
                "&f/realscoreboard toggle",
                "&7"));
    }

    @SubCommand("reload")
    @Permission("realscoreboard.admin")
    public void reloadcmd(final CommandSender commandSender) {
        rs.reload(commandSender);
        Text.send(commandSender, Config.file().getString("Config.Reloaded"));
    }

    @SubCommand("toggle")
    @Alias("t")
    @Permission("realscoreboard.toggle")
    public void togglecmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (!Config.file().getBoolean("PlayerData." + p.getName() + ".ScoreboardON")) {
                Config.file().set("PlayerData." + p.getName() + ".ScoreboardON", true);
                Config.save();
                Text.send(p, Config.file().getString("Config.Messages.Scoreboard-Toggle.ON"));
            } else {
                Config.file().set("PlayerData." + p.getName() + ".ScoreboardON", false);
                Config.save();
                Text.send(p, Config.file().getString("Config.Messages.Scoreboard-Toggle.OFF"));
            }
        }
    }

    @SubCommand("config")
    @Permission("realscoreboard.admin")
    public void configcmd(final CommandSender commandSender) {
        Text.send(commandSender, Arrays.asList(Text.getPrefix(),
                "&fConfig Version: &7" + Config.file().getInt("Version"),
                "&fScoreboard refresh: &7" + Config.file().getInt("Config.Scoreboard-Refresh"),
                "> &f&nAnimations",
                "  &fTitle Delay: &7" + Config.file().getInt("Config.Animations.Title-Delay"),
                "  &fLoop-Delay: &7" + Config.file().getInt("Config.Animations.Loop-Delay")));
    }
}
