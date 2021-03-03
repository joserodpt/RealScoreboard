package josegamerpt.realscoreboard;

import josegamerpt.realscoreboard.classes.Metrics;
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
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RealScoreboard extends JavaPlugin {
    public static Boolean placeholderAPI = false;
    static Permission perms = null;
    static Economy economia = null;
    static Chat chat = null;
    static Logger log = Bukkit.getLogger();
    static Plugin pl;

    private static AnimationManager am;
    private ScoreboardTask sbTask;

    PluginManager pm = Bukkit.getPluginManager();

    CommandManager commandManager;

    String header = "------------------- RealScoreboard -------------------";
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

    public static Chat getChat() {
        return chat;
    }

    public static Permission getPerms() {
        return perms;
    }

    public static String getVersion() {
        return pl.getDescription().getVersion();
    }

    public static AnimationManager getAnimationManager()
    {
        return am;
    }

    public void reload(CommandSender cs) {
        stopTask();
        Config.reload();

        if (Configer.checkForErrors()) {
            String msg = "There are some problems with your config: " + Configer.getErrors() + "\nPlease check this errors. Plugin is disabled due to config errors.";
            Text.send(cs, msg);
        } else {
            am.reload();
            runTask();
        }
    }

    static void failMessage(String reason) {
        Arrays.asList("Failed to load RealScoreboard.", reason,
                "If you think this is a bug, please contact JoseGamer_PT.", "https://www.spigotmc.org/members/josegamer_pt.40267/").forEach(s -> log(Level.INFO, s));
    }

    static String getName(Class a) {
        Class<?> enclosingClass = a.getEnclosingClass();
        if (enclosingClass != null) {
            return enclosingClass.getName();
        } else {
            return a.getName();
        }
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
        Configer.updateConfig();

        if (Configer.checkForErrors()) {
            failMessage("There are some problems with your config: " + Configer.getErrors() + "\nPlease check this errors. Plugin is disabled due to config errors.");
            log(Level.INFO, header);
            disablePlugin();
        } else {

            pm.registerEvents(new PlayerManager(), this);

            commandManager = new CommandManager(this);
            commandManager.hideTabComplete(true);
            commandManager.register(new Commands(this));

            am = new AnimationManager(this);
            new Metrics(this, 10080);

            Bukkit.getOnlinePlayers().forEach(PlayerManager::load);

            runTask();
            Arrays.asList("Finished loading RealScoreboard.", "Server version: " + getServerVersion() + " | Plugin Version: " + getDescription().getVersion()).forEach(s -> log(Level.INFO, s));
            log(Level.INFO, header);
        }
    }

    public void runTask()
    {
        sbTask = new ScoreboardTask();
        sbTask.runTaskTimerAsynchronously(this, Config.file().getInt("Config.Scoreboard-Refresh"), Config.file().getInt("Config.Scoreboard-Refresh"));
    }

    public void stopTask()
    {
        if (sbTask != null)
        {
            sbTask.cancel();
        }
    }

    private void disablePlugin() {
        if (am != null) {
            am.cancelAnimationTasks();
        }

        HandlerList.unregisterAll(this);

        Bukkit.getPluginManager().disablePlugin(this);
    }

    public void onDisable() {

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