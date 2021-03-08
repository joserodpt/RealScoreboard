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

public class Config implements Listener {

    static FileConfiguration data;
    static File dfile;

    static File sqlFile;
    static FileConfiguration sql;
    Plugin p;

    public static void setup(Plugin p) {
        dfile = new File(p.getDataFolder(), "config.yml");
        sqlFile = new File(p.getDataFolder(), "sql.yml");
        if (!dfile.exists()) {
            try {
                dfile.createNewFile();
            } catch (IOException localIOException) {
            }
        }
        if (!sqlFile.exists()) {
            p.saveResource("sql.yml", false);
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

    public PluginDescriptionFile desc() {
        return this.p.getDescription();
    }
}