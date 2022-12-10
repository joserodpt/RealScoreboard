package josegamerpt.realscoreboard;

import josegamerpt.realscoreboard.api.RealScoreboardAPI;
import josegamerpt.realscoreboard.api.config.Config;
import josegamerpt.realscoreboard.commands.Commands;
import josegamerpt.realscoreboard.listeners.McMMOScoreboardListener;
import josegamerpt.realscoreboard.managers.PlayerManager;
import josegamerpt.realscoreboard.utils.Metrics;
import josegamerpt.realscoreboard.utils.UpdateChecker;
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
        CommandManager commandManager = new CommandManager(this);
        commandManager.hideTabComplete(true);
        commandManager.register(new Commands(this, realScoreboard));
        new Metrics(this, 10080);
        Bukkit.getOnlinePlayers().forEach(player -> new PlayerManager(realScoreboard).check(player));
        if (Config.file().getBoolean("Config.mcMMO-Support")) {
            Bukkit.getPluginManager().registerEvents(new McMMOScoreboardListener(realScoreboard), this);
        }
        if (Config.file().getBoolean("Config.Check-for-Updates")) {
            new UpdateChecker(this, 22928).getVersion(version -> {
                if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    getLogger().info("The plugin is updated to the latest version.");
                } else {
                    newUpdate = true;
                    getLogger().info("There is a new update available! https://www.spigotmc.org/resources/realscoreboard-1-13-to-1-19-2.22928/");
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
        String s = Bukkit.getServer().getClass().getPackage().getName();
        return s.substring(s.lastIndexOf(".") + 1).trim();
    }

    public String getVersion() {
        return getDescription().getVersion();
    }
}