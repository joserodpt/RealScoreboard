package josegamerpt.realscoreboard.commands;

import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.RealScoreboardPlugin;
import josegamerpt.realscoreboard.api.config.PlayerData;
import josegamerpt.realscoreboard.api.config.Config;
import josegamerpt.realscoreboard.api.utils.Text;
import lombok.AllArgsConstructor;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@AllArgsConstructor
@SuppressWarnings("unused")
@Command("realscoreboard")
@Alias({"rsb", "sb"})
public class Commands extends CommandBase {

    private final RealScoreboardPlugin plugin;
    private final RealScoreboard instance;

    @Default
    public void defaultCommand(final CommandSender commandSender) {
        Text.send(commandSender, Arrays.asList("&7", Text.getPrefix() + "&a" + this.plugin.getVersion() + " &bHelp",
                "&f/rsb toggle"));
    }

    @SubCommand("reload")
    @Permission("realscoreboard.admin")
    public void reloadCommand(final CommandSender commandSender) {
        this.instance.reload();
        Text.send(commandSender, Config.file().getString("Config.Reloaded"));
    }

    @SubCommand("toggle")
    @Alias("t")
    @Permission("realscoreboard.toggle")
    public void toggleCommand(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            PlayerData playerData = this.instance.getDatabaseManager().getPlayerData(p.getUniqueId());
            playerData.setScoreboardON(!playerData.isScoreboardON());
            this.instance.getDatabaseManager().savePlayerData(playerData, true);
            Text.send(p, Config.file().getString("Config.Messages.Scoreboard-Toggle." + (playerData.isScoreboardON() ? "ON" : "OFF")));
        }
    }

    @SubCommand("off")
    @Permission("realscoreboard.toggle")
    public void offCommand(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            PlayerData playerData = this.instance.getDatabaseManager().getPlayerData(p.getUniqueId());
            playerData.setScoreboardON(false);
            this.instance.getDatabaseManager().savePlayerData(playerData, true);
            Text.send(p, Config.file().getString("Config.Messages.Scoreboard-Toggle.OFF"));
        }
    }

    @SubCommand("on")
    @Permission("realscoreboard.toggle")
    public void onCommand(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            PlayerData playerData = this.instance.getDatabaseManager().getPlayerData(p.getUniqueId());
            playerData.setScoreboardON(true);
            this.instance.getDatabaseManager().savePlayerData(playerData, true);
            Text.send(p, Config.file().getString("Config.Messages.Scoreboard-Toggle.ON"));
        }
    }

    @SubCommand("config")
    @Permission("realscoreboard.admin")
    public void configCommand(final CommandSender commandSender) {
        Text.send(commandSender, Arrays.asList(Text.getPrefix(),
                "&fConfig Version: &b" + Config.file().getInt("Version"),
                "&fScoreboard refresh: &b" + Config.file().getInt("Config.Scoreboard-Refresh"),
                "&f&nAnimations:",
                "- &fTitle Delay: &b" + Config.file().getInt("Config.Animations.Title-Delay"),
                "- &fLoop-Delay: &b" + Config.file().getInt("Config.Animations.Loop-Delay")));
    }


    @SubCommand("debug")
    @Permission("realscoreboard.admin")
    public void debug(final CommandSender commandSender) {
        Text.send(commandSender, Arrays.asList("", "", Text.getPrefix(),
                "> &b&lPLUGIN info",
                "&fPlugin Version: &b" + this.plugin.getVersion(),
                "> &b&lSERVER info",
                "&fServer Name: &b" + Bukkit.getName(),
                "&fServer Version: &b" + Bukkit.getVersion(),
                "> &b&lHOST info",
                "&fJava Version: &b" + System.getProperty("java.version"),
                "&fOS Name: &b" + System.getProperty("os.name"),
                "&fOS Architecture: &b" + System.getProperty("os.arch"),
                "&fOS Version: &b" + System.getProperty("os.version"),
                "> &b&lDATABASE info",
                "&fDB Driver: &b" + Config.getSql().getString("driver"),
                "> &b&lSCOREBOARD info",
                "&fLoaded Scoreboards: &b" + this.instance.getScoreboardManager().getScoreboards().size(),
                "&fLoaded Boards: &b" + this.instance.getScoreboardManager().getBoards().size(),
                "> &b&lCONFIG info",
                "&fConfig Version: &b" + Config.file().getInt("Version"),
                "&fScoreboard refresh: &b" + Config.file().getInt("Config.Scoreboard-Refresh"),
                "&f&nAnimations:",
                "- &fTitle Delay: &b" + Config.file().getInt("Config.Animations.Title-Delay"),
                "- &fLoop-Delay: &b" + Config.file().getInt("Config.Animations.Loop-Delay"),
                "&e&lNOTE: &fThis information is intended to be shared with the developer in order to provide additional assistance."));
    }
}