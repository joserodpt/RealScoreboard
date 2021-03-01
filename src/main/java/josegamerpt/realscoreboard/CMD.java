package josegamerpt.realscoreboard;

import josegamerpt.realscoreboard.classes.SBPlayer;
import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.managers.PlayerManager;
import josegamerpt.realscoreboard.utils.Text;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Command("realscoreboard")
@Alias({"rsb", "sb"})
public class CMD extends CommandBase {

    @Default
    public void defaultCommand(final CommandSender commandSender) {
        Text.send(commandSender, Arrays.asList("&7", Text.getPrefix() + "&a" + RealScoreboard.getVersion() + " &bHelp",
                "&f/realscoreboard toggle",
                "&7"));
    }

    @SubCommand("reload")
    @Permission("realscoreboard.admin")
    public void reloadcmd(final CommandSender commandSender) {
        RealScoreboard.reload(commandSender);
        Text.send(commandSender, Config.file().getString("Config.Reloaded"));
    }

    @SubCommand("toggle")
    @Alias("t")
    public void togglecmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            SBPlayer sb = PlayerManager.getPlayer(Bukkit.getPlayer(commandSender.getName()));
            if (sb != null) {
                sb.toggle();
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
                "  &fRainbow Delay: &7" + Config.file().getInt("Config.Animations.Rainbow-Delay")));
    }
}
