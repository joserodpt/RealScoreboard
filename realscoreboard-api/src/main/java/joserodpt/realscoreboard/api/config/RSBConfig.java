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
 * @author José Rodrigues © 2016-2026
 * @link https://github.com/joserodpt/RealScoreboard
 */

import dev.dejvokep.boostedyaml.YamlDocument;
import joserodpt.realutils.config.YamlConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class RSBConfig {

    private static YamlConfig configFile;
    private static YamlConfig sqlConfigFile;

    /**
     * Configures configuration files for RealScoreboard
     * <b>Note! You shouldn't call this method because
     * RealScoreboard calls this itself unless you have real reason
     * to do it (Probably you don't have)</b>
     *
     * @param javaPlugin plugin related to this method
     */
    public static void setup(JavaPlugin javaPlugin) {
        configFile = YamlConfig.of(javaPlugin, "config.yml").versioned("Version").ignoring("16", "Config.Scoreboard")
                .useDefaults(false).load();
        sqlConfigFile = YamlConfig.of(javaPlugin, "sql.yml").load();
    }

    /**
     * Gets RealScoreboard configuration file
     *
     * @return yaml configuration file
     */
    public static YamlDocument file() {
        return configFile.file();
    }

    /**
     * Gets RealScoreboard sql configuration file
     *
     * @return sql configuration file
     */
    public static YamlDocument getSql() {
        return sqlConfigFile.file();
    }

    /**
     * Saves RealScoreboard configuration file
     */
    @SuppressWarnings("unused")
    public static void save() {
        configFile.save();
    }

    /**
     * Reloads RealScoreboard configuration file
     * <b>Note!</b> It's not possible to reload
     * sql configuration file due to instability
     */
    public static void reload() {
        configFile.reload();
    }
}