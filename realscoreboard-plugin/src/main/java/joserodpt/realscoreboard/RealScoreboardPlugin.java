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
 * @author José Rodrigues © 2016-2025
 * @link https://github.com/joserodpt/RealScoreboard
 */

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import joserodpt.realpermissions.api.RealPermissionsAPI;
import joserodpt.realpermissions.api.pluginhook.ExternalPlugin;
import joserodpt.realpermissions.api.pluginhook.ExternalPluginPermission;
import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.config.RSBScoreboards;
import joserodpt.realscoreboard.api.scoreboard.RScoreboard;
import joserodpt.realscoreboard.api.utils.GUIBuilder;
import joserodpt.realscoreboard.api.utils.Text;
import joserodpt.realscoreboard.gui.SettingsGUI;
import joserodpt.realscoreboard.listeners.McMMOScoreboardListener;
import joserodpt.realscoreboard.listeners.PlayerListener;
import joserodpt.realscoreboard.utils.Metrics;
import joserodpt.realscoreboard.utils.UpdateChecker;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RealScoreboardPlugin extends JavaPlugin {

    @Getter
    private Permission perms;
    @Getter
    private Economy economy;
    @Getter
    private Chat chat;
    @Getter
    private static RealScoreboardPlugin instance;
    private static RealScoreboard realScoreboard;
    @Getter
    private boolean placeholderAPI = false;
    @Getter
    private static Boolean newUpdate = false;

    @Override
    public void onEnable() {
        printASCII();
        new Metrics(this, 10080);

        final long start = System.currentTimeMillis();
        RSBConfig.setup(this);
        RSBScoreboards.setup(this);

        instance = this;
        realScoreboard = new RealScoreboard(this);
        RealScoreboardAPI.setInstance(realScoreboard);
        realScoreboard.getScoreboardManagerAPI().loadScoreboards();

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            setupEconomy();
            setupPermissions();
            setupChat();
        } else {
            getLogger().warning("Vault is not installed on the server.");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.placeholderAPI = true;
        } else {
            getLogger().warning("PlaceholderAPI is not installed on the server.");
        }

        Bukkit.getPluginManager().registerEvents(new PlayerListener(realScoreboard), this);
        Bukkit.getPluginManager().registerEvents(SettingsGUI.getListener(), this);
        Bukkit.getPluginManager().registerEvents(GUIBuilder.getListener(), this);

        BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this);

        commandManager.registerSuggestion(SuggestionKey.of("#scoreboards"), (sender, context) -> realScoreboard.getScoreboardManagerAPI().getScoreboards().stream().map(RScoreboard::getName).collect(Collectors.toList()));

        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> Text.send(sender, "&cYou don't have permission to execute this command!"));
        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> Text.send(sender, "&cThe command you're trying to run doesn't exist."));
        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> Text.send(sender, "&cWrong usage for the command!"));

        commandManager.registerCommand(new RealScoreboardCommand(realScoreboard));

        if (RSBConfig.file().getBoolean("Config.mcMMO-Support") && Bukkit.getPluginManager().isPluginEnabled("mcMMO")) {
            Bukkit.getPluginManager().registerEvents(new McMMOScoreboardListener(realScoreboard), this);
        }
        if (RSBConfig.file().getBoolean("Config.Check-for-Updates")) {
            new UpdateChecker(this, 22928).getVersion(version -> {
                if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    getLogger().info("The plugin is updated to the latest version.");
                } else {
                    newUpdate = true;
                    getLogger().warning("There is a new update available! https://www.spigotmc.org/resources/22928/");
                }
            });
        }

        if (getServer().getPluginManager().getPlugin("RealPermissions") != null) {
            //register RealMines permissions onto RealPermissions
            try {
                List<ExternalPluginPermission> perms = new ArrayList<>(Arrays.asList(
                        new ExternalPluginPermission("realscoreboard.admin", "Allow access to the main operator commands of RealScoreboard.", Arrays.asList("rsb config", "rsb debug", "rsb reload")),
                        new ExternalPluginPermission("realscoreboard.setscoreboard", "Allow access to the setscoreboard command of RealScoreboard.", Collections.singletonList("rsb view <name> <target?>")),
                        new ExternalPluginPermission("realscoreboard.toggle", "Allow permission to toggle the scoreboard.", Arrays.asList("rsb on", "rsb off", "rsb toggle", "rsb t"))));
                realScoreboard.getScoreboardManagerAPI().getScoreboards().stream().filter(rScoreboard -> !rScoreboard.getPermission().equalsIgnoreCase("none")).forEach(rScoreboard -> perms.add(new ExternalPluginPermission(rScoreboard.getPermission(), "Permission for viewing the scoreboard: " + rScoreboard.getDisplayName())));

                RealPermissionsAPI.getInstance().getHooksAPI().addHook(new ExternalPlugin(this.getDescription().getName(), "&fReal&dScoreboard", this.getDescription().getDescription(), Material.PAINTING, perms, realScoreboard.getVersion()));

            } catch (Exception e) {
                getLogger().warning("Error while trying to register RealScoreboard permissions onto RealPermissions.");
                e.printStackTrace();
            }
        }

        Arrays.asList("Server version: " + getServerVersion(), "Finished loading in " + ((System.currentTimeMillis() - start) / 1000F) + " seconds.").forEach(s -> getLogger().info(s));
        getLogger().info("<------------------ RealScoreboard vPT ------------------>".replace("PT", this.getDescription().getVersion()));
    }

    private void printASCII() {
        logWithColor("&d   ____            _ ____                     _                         _");
        logWithColor("&d  |  _ \\ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |");
        logWithColor("&d  | |_) / _ \\/ _` | \\___ \\ / __/ _ \\| '__/ _ \\ '_ \\ / _ \\ / _` | '__/ _` |");
        logWithColor("&d  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |");
        logWithColor("&d  |_| \\_\\___|\\__,_|_|____/ \\___\\___/|_|  \\___|_.__/ \\___/ \\__,_|_|  \\__,_|");
        logWithColor("  &8Made by: &9JoseGamer_PT 		                      &8Version: &9" + this.getVersion());
        getLogger().info("");
    }

    public void logWithColor(String s) {
        getServer().getConsoleSender().sendMessage("[" + this.getDescription().getName() + "] " + Text.color(s));
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        this.economy = rsp.getProvider();
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager()
                .getRegistration(Permission.class);
        if (permissionProvider != null) {
            this.perms = permissionProvider.getProvider();
        }
    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            this.chat = chatProvider.getProvider();
        }
    }

    public String getServerVersion() {
        return Bukkit.getServer().getBukkitVersion();
    }

    public String getVersion() {
        return getDescription().getVersion();
    }
}