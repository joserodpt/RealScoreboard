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
import joserodpt.realscoreboard.api.config.Config;
import joserodpt.realscoreboard.api.utils.Text;
import joserodpt.realscoreboard.listeners.McMMOScoreboardListener;
import joserodpt.realscoreboard.managers.PlayerManager;
import joserodpt.realscoreboard.utils.Metrics;
import joserodpt.realscoreboard.utils.UpdateChecker;
import lombok.Getter;
import me.mattstudios.mf.base.CommandManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

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
        instance = this;
        Config.setup(this);
        realScoreboard = new RealScoreboard(this);
        RealScoreboardAPI.setInstance(realScoreboard);
        String header = "------------------- RealScoreboard PT -------------------".replace("PT", this.getDescription().getVersion());
        getLogger().info(header);
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
        getLogger().info("Your config version is: " + Config.file().getString("Version"));
        Bukkit.getPluginManager().registerEvents(new PlayerManager(realScoreboard), this);
        CommandManager cm = new CommandManager(this);

        cm.getMessageHandler().register("cmd.no.permission", (sender) -> Text.send(sender, Text.getPrefix() + "&cYou don't have permission to execute this command!"));
        cm.getMessageHandler().register("cmd.no.exists", (sender) -> Text.send(sender, Text.getPrefix() + "&cThe command you're trying to use doesn't exist."));
        cm.getMessageHandler().register("cmd.wrong.usage", (sender) -> Text.send(sender, Text.getPrefix() + "&cWrong usage for the command!"));
        cm.getMessageHandler().register("cmd.no.console", sender -> Text.send(sender, Text.getPrefix() + "&cCommand can only be executed by a player."));

        cm.hideTabComplete(true);
        cm.register(new Commands(this, realScoreboard));
        new Metrics(this, 10080);
        Bukkit.getOnlinePlayers().forEach(player -> new PlayerManager(realScoreboard).check(player));
        if (Config.file().getBoolean("Config.xmcMMO-Support")) {
            Bukkit.getPluginManager().registerEvents(new McMMOScoreboardListener(realScoreboard), this);
        }
        if (Config.file().getBoolean("Config.Check-for-Updates")) {
            new UpdateChecker(this, 22928).getVersion(version -> {
                if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    getLogger().info("The plugin is updated to the latest version.");
                } else {
                    newUpdate = true;
                    getLogger().info("There is a new update available! https://www.spigotmc.org/resources/realscoreboard-1-13-to-1-20-1.22928/");
                }
            });
        }
        Arrays.asList("Finished loading RealScoreboard.", "Server version: " + getServerVersion() + " | Plugin Version: " + getDescription().getVersion()).forEach(s -> getLogger().info(s));
        getLogger().info(header);
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