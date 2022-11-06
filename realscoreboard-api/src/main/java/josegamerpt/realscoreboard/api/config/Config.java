package josegamerpt.realscoreboard.api.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import josegamerpt.realscoreboard.api.RealScoreboardAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Config {

    private static YamlDocument configFile;
    private static YamlDocument sqlConfigFile;

    public static void setup(JavaPlugin javaPlugin) {
        try {
            configFile = YamlDocument.create(new File(javaPlugin.getDataFolder(), "config.yml"), javaPlugin.getResource("config.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("Version")).build());
            sqlConfigFile = YamlDocument.create(new File(javaPlugin.getDataFolder(), "sql.yml"), javaPlugin.getResource("sql.yml"));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't setup config files!");
        }
    }

    public static YamlDocument file() {
        return configFile;
    }

    public static YamlDocument getSql() {
        return sqlConfigFile;
    }

    public static void save() {
        try {
            configFile.save();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't save config.yml!");
        }
    }

    public static void reload() {
        try {
            configFile.reload();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't reload config.yml!");
        }
    }
}