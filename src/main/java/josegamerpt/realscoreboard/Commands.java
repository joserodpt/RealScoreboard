package josegamerpt.realscoreboard;

import josegamerpt.realscoreboard.config.PlayerData;
import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.utils.Text;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Command("realscoreboard")
@Alias({"rsb", "sb"})
public class Commands extends CommandBase {

    @Default
    public void defaultCommand(final CommandSender commandSender) {
        Text.send(commandSender, Arrays.asList("&7", Text.getPrefix() + "&a" + RealScoreboard.getInstance().getVersion() + " &bHelp",
                "&f/rsw toggle"));
    }

    @SubCommand("reload")
    @Permission("realscoreboard.admin")
    public void reloadCommand(final CommandSender commandSender) {
        RealScoreboard.getInstance().reload(commandSender);
        Text.send(commandSender, Config.file().getString("Config.Reloaded"));
    }

    @SubCommand("toggle")
    @Alias("t")
    @Permission("realscoreboard.toggle")
    public void toggleCommand(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            PlayerData playerData = RealScoreboard.getInstance().getDatabaseManager().getPlayerData(p.getUniqueId());
            playerData.setScoreboardON(!playerData.isScoreboardON());
            RealScoreboard.getInstance().getDatabaseManager().savePlayerData(playerData, true);
            Text.send(p, Config.file().getString("Config.Messages.Scoreboard-Toggle." + (playerData.isScoreboardON() ? "ON" : "OFF")));
        }
    }

    @SubCommand("off")
    @Permission("realscoreboard.toggle")
    public void offCommand(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            PlayerData playerData = RealScoreboard.getInstance().getDatabaseManager().getPlayerData(p.getUniqueId());
            playerData.setScoreboardON(false);
            RealScoreboard.getInstance().getDatabaseManager().savePlayerData(playerData, true);
            Text.send(p, Config.file().getString("Config.Messages.Scoreboard-Toggle.OFF"));
        }
    }

    @SubCommand("on")
    @Permission("realscoreboard.toggle")
    public void onCommand(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            PlayerData playerData = RealScoreboard.getInstance().getDatabaseManager().getPlayerData(p.getUniqueId());
            playerData.setScoreboardON(true);
            RealScoreboard.getInstance().getDatabaseManager().savePlayerData(playerData, true);
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
                "&fPlugin Version: &b" + RealScoreboard.getInstance().getVersion(),
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
                "&fLoaded Scoreboards: &b" + RealScoreboard.getInstance().getScoreboardManager().getScoreboards().size(),
                "&fLoaded Boards: &b" + RealScoreboard.getInstance().getScoreboardManager().getBoards().size(),
                "> &b&lCONFIG info",
                "&fConfig Version: &b" + Config.file().getInt("Version"),
                "&fScoreboard refresh: &b" + Config.file().getInt("Config.Scoreboard-Refresh"),
                "&f&nAnimations:",
                "- &fTitle Delay: &b" + Config.file().getInt("Config.Animations.Title-Delay"),
                "- &fLoop-Delay: &b" + Config.file().getInt("Config.Animations.Loop-Delay"),
                "&e&lNOTE: &fThis information is intended to be shared with the developer in order to provide additional assistance."));
    }
}
