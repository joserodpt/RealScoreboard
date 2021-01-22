package josegamerpt.realscoreboard;

import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.managers.AnimationManager;
import josegamerpt.realscoreboard.managers.PlayerManager;
import josegamerpt.realscoreboard.utils.Text;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

@Command("realscoreboard")
@Alias({"rsb", "sb"})
public class CMD extends CommandBase {

    @Default
    public void defaultCommand(final CommandSender commandSender) {
        Arrays.asList("&7", RealScoreboard.getPrefix() + "&6" + RealScoreboard.getVersion() + " &bHelp",
                "&f/realscoreboard toggle", "&f/realscoreboard reload",
                "&7").forEach(s -> commandSender.sendMessage(Text.color(s)));
    }

    @SubCommand("reload")
    @Permission("realscoreboard.reload")
    public void reloadcmd(final CommandSender commandSender) {
        PlayerManager.players.forEach(sbPlayer -> sbPlayer.stop());
        Config.reload();
        AnimationManager.refresh = Config.file().getInt("Config.Scoreboard-Refresh");
        commandSender.sendMessage(Text.color(RealScoreboard.getPrefix() + Config.file().getString("Config.Reloaded")));
        PlayerManager.players.forEach(sbPlayer -> sbPlayer.start());
    }

    @SubCommand("toggle")
    @Alias("t")
    @Permission("realscoreboard.toggle")
    public void togglecmd(final CommandSender commandSender) {
        PlayerManager.getPlayer(Bukkit.getPlayer(commandSender.getName())).toggle();
    }
}
