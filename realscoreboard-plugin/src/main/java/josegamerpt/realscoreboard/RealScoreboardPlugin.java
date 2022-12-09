package josegamerpt.realscoreboard;

import josegamerpt.realscoreboard.api.RealScoreboardAPI;
import josegamerpt.realscoreboard.api.config.Config;
import josegamerpt.realscoreboard.listeners.McMMOScoreboardListener;
import josegamerpt.realscoreboard.managers.PlayerManager;
import josegamerpt.realscoreboard.utils.Metrics;
import josegamerpt.realscoreboard.utils.UpdateChecker;
import me.mattstudios.mf.base.CommandManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Arrays;

public class RealScoreboardPlugin extends JavaPlugin {

    public boolean placeholderAPI = false;
    public static Boolean newUpdate = false;
    private Permission perms = null;
    private Economy economy = null;
    private Chat chat = null;
    private static RealScoreboardPlugin instance;
    private static RealScoreboard realScoreboard;

    @Override
    public void onLoad() {
        realScoreboard = new RealScoreboard(this);
    }

    public void onEnable() {
        instance = this;

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
            placeholderAPI = true;
        } else {
            getLogger().warning("PlaceholderAPI is not installed on the server.");
        }
        Config.setup(this);
        getLogger().info("Your config version is: " + Config.file().getString("Version"));

        Bukkit.getPluginManager().registerEvents(new PlayerManager(), this);

        CommandManager commandManager = new CommandManager(this);
        commandManager.hideTabComplete(true);
        commandManager.register(new Commands());

        new Metrics(this, 10080);

        Bukkit.getOnlinePlayers().forEach(player -> new PlayerManager().check(player));

        if (Config.file().getBoolean("Config.mcMMO-Support")) {
            Bukkit.getPluginManager().registerEvents(new McMMOScoreboardListener(), this);
        }

        if (Config.file().getBoolean("Config.Check-for-Updates"))
        {
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
        RealScoreboardAPI.setInstance(realScoreboard);
    }

    // Vault
    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager()
                .getRegistration(Permission.class);
        if (permissionProvider != null) {
            perms = permissionProvider.getProvider();
        }
    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
    }

    public String getServerVersion() {
        String s = Bukkit.getServer().getClass().getPackage().getName();
        return s.substring(s.lastIndexOf(".") + 1).trim();
    }

    public Economy getEconomy() {
        return economy;
    }

    public Chat getChat() {
        return chat;
    }

    public Permission getPerms() {
        return perms;
    }

    public String getVersion() {
        return instance.getDescription().getVersion();
    }

    public static RealScoreboardPlugin getInstance() {
        return instance;
    }
}