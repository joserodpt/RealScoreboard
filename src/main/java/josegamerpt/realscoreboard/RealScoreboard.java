package josegamerpt.realscoreboard;

import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.config.Configer;
import josegamerpt.realscoreboard.managers.AnimationManager;
import josegamerpt.realscoreboard.managers.PlayerManager;
import josegamerpt.realscoreboard.utils.Text;
import me.mattstudios.mf.base.CommandManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RealScoreboard extends JavaPlugin {
    static Permission perms = null;
    static Economy economia = null;
    static Chat chat = null;
    public static Boolean placeholderAPI = false;

    static Logger log = Bukkit.getLogger();
    static Plugin pl;
    PluginManager pm = Bukkit.getPluginManager();

    CommandManager commandManager;

    String header = "------------------- RealScoreboard -------------------";

    public static String getPrefix() {
        return Text.color(Config.file().getString("Config.Prefix"));
    }

    public static void log(Level l, String s) {
        log.log(l, s);
    }

    public static String getServerVersion() {
        String s = Bukkit.getServer().getClass().getPackage().getName();
        return s.substring(s.lastIndexOf(".") + 1).trim();
    }

    public static Economy getEconomy() {
        return economia;
    }

    public static Plugin getPL() {
        return pl;
    }

    public static Chat getChat() {
        return chat;
    }

    public static Permission getPerms() {
        return perms;
    }

    public static String getVersion() {
        return pl.getDescription().getVersion();
    }

    public void onEnable() {
        pl = this;

        log(Level.INFO, header);

        log(Level.INFO, "Checking the server version.");
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            setupEconomy();
            setupPermissions();
            setupChat();
        } else {
            log(Level.WARNING, "Vault is not installed on the server.");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderAPI = true;
        } else {
            log(Level.WARNING, "PlaceholderAPI is not installed on the server.");
        }

        saveDefaultConfig();
        Config.setup(this);
        log(Level.INFO, "Your config version is: " + Configer.getConfigVersion());
        Configer.updateConfig(Configer.getConfigVersion());

        if (Configer.checkForErrors()) {
            failMessage("There are some problems with your config: " + Configer.getErrors() + "\nPlease check this errors. Plugin is disabled due to config errors.");
            log(Level.INFO, header);
            disablePlugin();
        } else {
            AnimationManager.refresh = Config.file().getInt("Config.Scoreboard-Refresh");

            pm.registerEvents(new PlayerManager(), this);

            commandManager = new CommandManager(this);
            commandManager.register(new CMD());

            AnimationManager.startAnimations();

            Bukkit.getOnlinePlayers().forEach(PlayerManager::loadPlayer);

            new Metrics(this, 10080);

            Arrays.asList("Finished loading RealScoreboard.", "Server version: " + getServerVersion(), "Plugin Version: " + getDescription().getVersion()).forEach(s -> log(Level.INFO, s));
            log(Level.INFO, header);
        }
    }

    private void failMessage(String reason) {
        Arrays.asList("Failed to load RealScoreboard.", reason,
                "If you think this is a bug, please contact JoseGamer_PT.", "https://www.spigotmc.org/members/josegamer_pt.40267/").forEach(s -> log(Level.INFO, s));
    }

    private void disablePlugin() {
        HandlerList.unregisterAll(this);

        Bukkit.getPluginManager().disablePlugin(this);
    }

    public void onDisable() {
        PlayerManager.players.forEach(SBPlayer::stop);
        PlayerManager.players.clear();
    }

    // Vault
    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economia = rsp.getProvider();
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

}