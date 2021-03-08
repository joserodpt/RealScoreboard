package josegamerpt.realscoreboard.config;

import josegamerpt.realscoreboard.RealScoreboard;

import java.util.ArrayList;
import java.util.logging.Level;

public class Configer {

    private final static int latest = 5;
    private static String errors;

    public static int getConfigVersion() {
        if (Config.file().getInt("Version") == 0) {
            return 1;
        } else {
            return Config.file().getInt("Version");
        }
    }

    public static void updateConfig() {
        while (getConfigVersion() != latest) {
            int newconfig = 0;
            switch (getConfigVersion()) {
                case 1:
                    //update to 2
                    newconfig = 2;
                    Config.file().set("Version", newconfig);
                    Config.file().set("Config.Messages.Scoreboard-Toggle.ON", "&fScoreboard turned &aON&f.");
                    Config.file().set("Config.Messages.Scoreboard-Toggle.OFF", "&fScoreboard turned &cOFF&f.");
                    Config.save();
                    break;
                case 2:
                    //update to 3
                    newconfig = 3;
                    Config.file().set("Version", newconfig);
                    Config.file().set("Config.Prefix", "&bReal&9Scoreboard &7| &r");
                    Config.file().set("Config.Reloaded", "&fConfig &areloaded&f.");
                    Config.save();
                    break;
                case 3:
                    //update to 4
                    newconfig = 4;
                    Config.file().set("Version", newconfig);
                    int val = Config.file().getInt("Config.Animations.Rainbow-Delay");
                    Config.file().set("Config.Animations.Loop-Delay", val);
                    Config.file().set("Config.Animations.Rainbow-Delay", null);
                    Config.file().set("Debug", false);
                    Config.save();
                    break;
                case 4:
                    //update to 5
                    newconfig = 5;
                    Config.file().set("Version", newconfig);
                    Config.file().set("Config.Use-Placeholders-In-Scoreboard-Titles", false);
                    Config.file().set("PlayerData", null);
                    Config.save();
                    break;
            }
            RealScoreboard.log(Level.INFO, "Config file updated to version " + newconfig + ".");
        }
        if (getConfigVersion() == latest) {
            RealScoreboard.log(Level.INFO, "Your config file is updated to the latest version.");
        }
    }

    public static String getErrors() {
        return errors;
    }

    public static boolean checkForErrors() {
        ArrayList<String> errs = new ArrayList<>();
        if (!Config.file().contains("Config.Disabled-Worlds")) {
            errs.add("Missing Disabled-Worlds Section on config.yml | Please look at the original config.yml");
        }

        errors = String.join(", ", errs);
        return errs.size() > 0;
    }
}
