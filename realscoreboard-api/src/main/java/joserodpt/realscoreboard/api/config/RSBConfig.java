package joserodpt.realscoreboard.api.config;

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

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class RSBConfig {

    private static YamlDocument configFile;
    private static YamlDocument sqlConfigFile;

    /**
     * Configures configuration files for RealScoreboard
     * <b>Note! You shouldn't call this method because
     * RealScoreboard calls this itself unless you have real reason
     * to do it (Probably you don't have)</b>
     *
     * @param javaPlugin plugin related to this method
     */
    @SuppressWarnings("ConstantConditions")
    public static void setup(JavaPlugin javaPlugin) {
        try {
            configFile = YamlDocument.create(new File(javaPlugin.getDataFolder(), "config.yml"), javaPlugin.getResource("config.yml"),
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("Version")).addIgnoredRoute("16", "Config.Scoreboard", '.').build());
            sqlConfigFile = YamlDocument.create(new File(javaPlugin.getDataFolder(), "sql.yml"), javaPlugin.getResource("sql.yml"));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't setup config files!");
        }
    }

    /**
     * Gets RealScoreboard configuration file
     *
     * @return yaml configuration file
     */
    public static YamlDocument file() {
        return configFile;
    }

    /**
     * Gets RealScoreboard sql configuration file
     *
     * @return sql configuration file
     */
    public static YamlDocument getSql() {
        return sqlConfigFile;
    }

    /**
     * Saves RealScoreboard configuration file
     */
    @SuppressWarnings("unused")
    public static void save() {
        try {
            configFile.save();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't save config.yml!");
        }
    }

    /**
     * Reloads RealScoreboard configuration file
     * <b>Note!</b> It's not possible to reload
     * sql configuration file due to instability
     */
    public static void reload() {
        try {
            configFile.reload();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't reload config.yml!");
        }
    }
}