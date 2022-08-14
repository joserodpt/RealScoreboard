package josegamerpt.realscoreboard.config;

import josegamerpt.realscoreboard.RealScoreboard;

import java.util.ArrayList;
import java.util.Collections;

public class Configer {

    private final static int latest = 13;
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
                case 5:
                    //update to 6
                    newconfig = 6;
                    Config.file().set("Version", newconfig);
                    Config.file().set("Config.Bypass-Worlds", Collections.singletonList("skywarsLobby"));
                    Config.save();
                    break;
                case 6:
                    //update to 7
                    newconfig = 7;
                    Config.file().set("Version", newconfig);
                    Config.file().set("Config.RealScoreboard-Disabled-By-Default", false);
                    Config.save();
                    break;
                case 7:
                    //update to 8
                    //major breaking config
                    newconfig = 8;
                    break;
                case 8:
                    //update to 9
                    //major breaking config
                    newconfig = 10;
                    break;
                case 10:
                    //update to 11
                    newconfig = 11;
                    Config.file().set("Version", newconfig);
                    Config.file().set("Config.Hours.Formatting", "HH:mm:ss");
                    Config.file().set("Config.Hours.Offset", 0);
                    Config.save();
                    break;
                case 11:
                    newconfig = 12;
                    Config.file().set("Config.Check-for-Updates", true);
                    Config.save();
                    break;
                case 12:
                    newconfig = 13;
                    Config.file().set("Version", newconfig);
                    Config.file().set("Config.ItemAdder-Support", true);
                    Config.save();
                    break;
            }
            RealScoreboard.getInstance().getLogger().info("Config file updated to version " + newconfig + ".");
        }
        if (getConfigVersion() == latest) {
            RealScoreboard.getInstance().getLogger().info("Your config file is updated to the latest version.");
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
        if (!Config.file().contains("Config.Bypass-Worlds")) {
            errs.add("Missing Bypassed-Worlds Section on config.yml | Please look at the original config.yml");
        }
        switch (Config.file().getInt("Version"))
        {
            case 7:
            case 8:
                errs.add("The new config model is required for the latest version to work without problems. Please backup your current config and delete it to generate a new file.");
                break;
        }

        errors = String.join(", ", errs);
        return errs.size() > 0;
    }
}
