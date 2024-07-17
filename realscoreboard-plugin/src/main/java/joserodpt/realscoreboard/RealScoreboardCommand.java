package joserodpt.realscoreboard;

/*
 *   ____            _ ____                     _                         _
 *  |  _ \ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |
 *  | |_) / _ \/ _` | \___ \ / __/ _ \| '__/ _ \ '_ \ / _ \ / _` | '__/ _` |
 *  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |
 *  |_| \_\___|\__,_|_|____/ \___\___/|_|  \___|_.__/ \___/ \__,_|_|  \__,_|
 *
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealScoreboard
 */

import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.scoreboard.RSBPlayer;
import joserodpt.realscoreboard.api.scoreboard.RScoreboard;
import joserodpt.realscoreboard.api.utils.GUIBuilder;
import joserodpt.realscoreboard.api.utils.Items;
import joserodpt.realscoreboard.api.utils.Text;
import joserodpt.realscoreboard.gui.SettingsGUI;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Completion;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Optional;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.annotations.WrongUsage;
import me.mattstudios.mf.base.CommandBase;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Command("realscoreboard")
@Alias({"rsb", "sb"})
public class RealScoreboardCommand extends CommandBase {

    private final String playerOnly = "Only players can use this command.";

    private final RealScoreboardAPI rsa;

    public RealScoreboardCommand(RealScoreboardAPI rsa) {
        this.rsa = rsa;
    }

    @Default
    @SuppressWarnings("unused")
    public void defaultcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player p && (p.isOp() || p.hasPermission("realscoreboard.admin"))) {
            //TODO: CONFIG GUI
            SettingsGUI s = new SettingsGUI(p, rsa);
            s.openInventory(p);
        } else {
            commandSender.sendMessage(Text.color("&fReal&dScoreboard &7| &fv" + this.rsa.getVersion()));
        }
    }

    @SubCommand("reload")
    @Permission("realscoreboard.admin")
    @SuppressWarnings("unused")
    public void reloadcmd(final CommandSender commandSender) {
        this.rsa.reload();
        commandSender.sendMessage(Text.color("&fReal&dScoreboard &7| &f" + RSBConfig.file().getString("Config.Reloaded")));
    }

    @SubCommand("toggle")
    @Alias("t")
    @Permission("realscoreboard.toggle")
    @SuppressWarnings("unused")
    public void togglecmd(final CommandSender commandSender) {
        if (commandSender instanceof Player p) {
            RSBPlayer hook = rsa.getPlayerManagerAPI().getPlayer(p.getUniqueId());
            hook.setRealScoreboardVisible(!hook.isRealScoreboardVisible());
            Text.send(p, RSBConfig.file().getString("Config.Messages.Scoreboard-Toggle." + (hook.isRealScoreboardVisible() ? "ON" : "OFF")));
        } else {
            Text.send(commandSender, playerOnly);
        }
    }

    @SubCommand("toggleo")
    @Alias({"to", "toggleother"})
    @Permission("realscoreboard.admin")
    @SuppressWarnings("unused")
    public void toggleothercmd(final CommandSender commandSender, final Player player) {
        if (player == null) {
            Text.send(commandSender, "Player not found.");
            return;
        }
        RSBPlayer hook = rsa.getPlayerManagerAPI().getPlayer(player.getUniqueId());
        hook.setRealScoreboardVisible(!hook.isRealScoreboardVisible());
        Text.send(commandSender, RSBConfig.file().getString("Config.Messages.Scoreboard-Toggle." + (hook.isRealScoreboardVisible() ? "ON" : "OFF")));
    }

    @SubCommand("off")
    @Permission("realscoreboard.toggle")
    @SuppressWarnings("unused")
    public void offcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player p) {
            RSBPlayer hook = rsa.getPlayerManagerAPI().getPlayer(p.getUniqueId());
            hook.setRealScoreboardVisible(false);
            Text.send(p, RSBConfig.file().getString("Config.Messages.Scoreboard-Toggle.OFF"));
        } else {
            Text.send(commandSender, playerOnly);
        }
    }

    @SubCommand("on")
    @Permission("realscoreboard.toggle")
    @SuppressWarnings("unused")
    public void oncmd(final CommandSender commandSender) {
        if (commandSender instanceof Player p) {
            RSBPlayer hook = rsa.getPlayerManagerAPI().getPlayer(p.getUniqueId());
            hook.setRealScoreboardVisible(true);
            Text.send(p, RSBConfig.file().getString("Config.Messages.Scoreboard-Toggle.ON"));
        } else {
            Text.send(commandSender, playerOnly);
        }
    }

    @SubCommand("selectscoreboard")
    @Alias("selectsb")
    @Completion("#players")
    @Permission("realscoreboard.selectscoreboard")
    @SuppressWarnings("unused")
    public void selectscoreboardcmd(final CommandSender commandSender, Player target) {
        if (commandSender instanceof Player p) {
            if (target == null) {
                Text.send(commandSender, "Player not found.");
                return;
            }

            final GUIBuilder inventory = new GUIBuilder("Choose board for " + target.getName(), 27, target.getUniqueId());

            final Collection<RScoreboard> list = rsa.getScoreboardManagerAPI().getScoreboards();
            int i = 0;
            for (RScoreboard sb : list) {
                inventory.addItem(e -> {
                    target.closeInventory();
                    rsa.getPlayerManagerAPI().getPlayer(target.getUniqueId()).setScoreboard(sb);
                    Text.send(p, "Scoreboard &b" + sb.getName() + " &fapplied to &b" + target.getName());
                }, Items.createItemLore(Material.FILLED_MAP, 1, sb.getDisplayName(), Collections.singletonList("&7Click to apply.")), i);
                ++i;
            }

            inventory.openInventory(target);
        } else {
            Text.send(commandSender, playerOnly);
        }
    }

    @SubCommand("setscoreboard")
    @Alias("setsb")
    @Completion({"#scoreboards", "#players"})
    @Permission("realscoreboard.setscoreboard")
    @SuppressWarnings("unused")
    public void setscoreboardcmd(final CommandSender commandSender, final String name, Player target) {
        RScoreboard sb = rsa.getScoreboardManagerAPI().getScoreboard(name);
        if (sb == null) {
            Text.send(commandSender, "Scoreboard not found with that name.");
            return;
        }

        if (target == null) {
            Text.send(commandSender, "Player not found.");
            return;
        }

        if (rsa.getPlayerManagerAPI().getPlayer(target.getUniqueId()).getScoreboard() == sb) {
            Text.send(commandSender, target.getName() + " &calready has that scoreboard applied.");
        } else {
            rsa.getPlayerManagerAPI().getPlayer(target.getUniqueId()).setScoreboard(sb);
            Text.send(commandSender, name + " scoreboard applied to " + target.getName());
        }
    }

    @SubCommand("announce")
    @Alias("broadcast")
    @Permission("realscoreboard.setscoreboard")
    @SuppressWarnings("unused")
    @WrongUsage("&cUsage: /rsb announce <seconds> <player>")
    public void announcecmd(final CommandSender commandSender, Integer seconds, @Optional Player p) {
        if (commandSender instanceof Player myPlayer) {
            new AnvilGUI.Builder()
                    .onClose(stateSnapshot -> {
                    })
                    .onClick((slot, stateSnapshot) -> { // Either use sync or async variant, not both
                        if (slot != AnvilGUI.Slot.OUTPUT) {
                            return Collections.emptyList();
                        }

                        if (p == null) {
                            rsa.getPlayerManagerAPI().getPlayerMap().values().forEach(rsbPlayer -> rsbPlayer.announce(stateSnapshot.getText(), seconds));
                        } else {
                            rsa.getPlayerManagerAPI().getPlayer(p.getUniqueId()).announce(stateSnapshot.getText(), seconds);
                        }

                        return List.of(AnvilGUI.ResponseAction.close());
                    })
                    .preventClose()                                                    //prevents the inventory from being closed
                    .text("Sample Message")                              //sets the text the GUI should start with
                    .title("Enter announce message:")                                    //set the title of the GUI (only works in 1.14+)
                    .plugin(rsa.getPlugin())                                          //set the plugin instance
                    .open(myPlayer);
        } else {
            Text.send(commandSender, playerOnly);
        }


    }

    @SubCommand("debug")
    @Permission("realscoreboard.admin")
    @SuppressWarnings("unused")
    public void debugcmd(final CommandSender commandSender) {
        Text.send(commandSender, Arrays.asList("", "", Text.getPrefix(),
                "> &b&lPLUGIN info",
                "&fPlugin Version: &b" + this.rsa.getVersion(),
                "> &b&lSERVER info",
                "&fServer Name: &b" + Bukkit.getName(),
                "&fServer Version: &b" + Bukkit.getVersion(),
                "> &b&lHOST info",
                "&fJava Version: &b" + System.getProperty("java.version"),
                "&fOS Name: &b" + System.getProperty("os.name"),
                "&fOS Architecture: &b" + System.getProperty("os.arch"),
                "&fOS Version: &b" + System.getProperty("os.version"),
                "> &b&lDATABASE info",
                "&fDB Driver: &b" + RSBConfig.getSql().getString("driver"),
                "> &b&lSCOREBOARD info",
                "&fLoaded Scoreboards: &b" + this.rsa.getScoreboardManagerAPI().getScoreboards().size(),
                "> &b&lCONFIG info",
                "&fConfig Version: &b" + RSBConfig.file().getInt("Version"),
                "&e&lNOTE: &fThis information is intended to be shared with the developer in order to provide additional assistance."));
    }
}