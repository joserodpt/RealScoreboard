package josegamerpt.realscoreboard.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Config implements Listener {

    private static FileConfiguration data;
    private static File dfile;

    private static File sqlFile;
    private static FileConfiguration sql;

    public static void setup(JavaPlugin javaPlugin) {
        dfile = new File(javaPlugin.getDataFolder(), "config.yml");
        sqlFile = new File(javaPlugin.getDataFolder(), "sql.yml");
        if (!dfile.exists()) {
            javaPlugin.saveResource("config.yml", false);
        }
        if (!sqlFile.exists()) {
            javaPlugin.saveResource("sql.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(dfile);
        sql = YamlConfiguration.loadConfiguration(sqlFile);
    }

    public static FileConfiguration file() {
        return data;
    }

    public static FileConfiguration getSql() {
        return sql;
    }

    public static void save() {
        try {
            data.save(dfile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save config.yml!");
        }
    }

    public static void reload() {
        data = YamlConfiguration.loadConfiguration(dfile);
    }
}