package joserodpt.realscoreboard.managers;

/*
 *   ____            _ ____                     _                         _
 *  |  _ \ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |
 *  | |_) / _ \/ _` | \___ \ / __/ _ \| '__/ _ \ '_ \ / _ \ / _` | '__/ _` |
 *  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |
 *  |_| \_\___|\__,_|_|____/ \___\___/|_|  \___|_.__/ \___/ \__,_|_|  \__,_|
 *
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealScoreboard
 */

import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.config.RSBScoreboards;
import joserodpt.realscoreboard.api.managers.AbstractScoreboardManager;
import joserodpt.realscoreboard.api.scoreboard.RBoard;
import joserodpt.realscoreboard.api.scoreboard.RScoreboard;
import joserodpt.realscoreboard.api.scoreboard.RScoreboardBoards;
import joserodpt.realscoreboard.api.scoreboard.RScoreboardSingle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardManager implements AbstractScoreboardManager {
    private final Map<String, RScoreboard> scoreboards = new HashMap<>();
    private final RealScoreboardAPI rsa;

    public ScoreboardManager(RealScoreboardAPI rsa) {
        this.rsa = rsa;
    }

    @Override
    public void loadScoreboards() {
        //starting from version 1.4, scoreboards are stored in the scoreboards.yml file and have a new structure,
        //this next part of the code is responsible for the conversion of those old scoreboards in the config.yml
        if (RSBConfig.file().contains("Config.Scoreboard")) {
            rsa.getLogger().warning("Starting scoreboard conversion to scoreboards.yml...");
            convertOldScoreboardsV1dot4();
            return;
        }

        if (!RSBScoreboards.file().contains("Scoreboards") || RSBScoreboards.file().getSection("Scoreboards") == null) {
            rsa.getLogger().severe("There seems to be no valid scoreboards in the scoreboards.yml file!");
            return;
        }

        for (String scoreboardName : RSBScoreboards.file().getSection("Scoreboards").getRoutesAsStrings(false)) {
            String key = "Scoreboards." + scoreboardName + ".";
            String w = RSBScoreboards.file().getString(key + "Default-World");

            boolean def = RSBScoreboards.file().getBoolean(key + "Default");
            String displayName = RSBScoreboards.file().getString(key + "Display-Name");
            String permission = RSBScoreboards.file().getString(key + "Permission");

            int titleRefresh = RSBScoreboards.file().getInt(key + "Refresh.Title");
            int titleLoopDelay = RSBScoreboards.file().getInt(key + "Refresh.Title-Loop-Delay");
            int globalScoreboardRefresh = RSBScoreboards.file().getInt(key + "Refresh.Scoreboard");

            //if config has lines, it has only one board, else, two or more
            if (RSBScoreboards.file().contains(key + "Lines")) {
                List<String> title = RSBScoreboards.file().getStringList(key + "Title");
                List<String> lines = RSBScoreboards.file().getStringList(key + "Lines");
                this.scoreboards.put(scoreboardName, new RScoreboardSingle(scoreboardName, displayName, permission, w, title, lines,
                        titleRefresh, titleLoopDelay, globalScoreboardRefresh, def));
            } else {
                this.scoreboards.put(scoreboardName, new RScoreboardBoards(scoreboardName, displayName, permission, w,
                        titleRefresh, titleLoopDelay, globalScoreboardRefresh, RSBScoreboards.file().getInt(key + "Refresh.Board-Loop-Delay"), def));
            }
        }

        // Use a Map to track if a world has a default scoreboard (true if it has at least one)
        Map<String, Boolean> worldHasDefault = new HashMap<>();

        for (RScoreboard rScoreboard : this.scoreboards.values()) {
            String worldName = rScoreboard.getDefaultWord().toLowerCase(); // Consistent case handling
            // If this is the first time we're seeing this world or if we find a default, update the map
            worldHasDefault.compute(worldName, (key, hasDefault) -> hasDefault == null ? rScoreboard.isDefault() : hasDefault || rScoreboard.isDefault());
        }

        // Now, check which worlds don't have a default scoreboard
        worldHasDefault.forEach((world, hasDefault) -> {
            if (!hasDefault) {
                rsa.getLogger().severe(world + " doesn't have a default scoreboard set!");
            }
        });

        this.scoreboards.values().forEach(RScoreboard::init);
        rsa.getLogger().info("Loaded " + this.scoreboards.keySet().size() + " scoreboards.");
    }

    private void convertOldScoreboardsV1dot4() {
        //remove the template scoreboards
        //RSBScoreboards.file().remove("Scoreboards");
        int counter = 0;

        for (String world : RSBConfig.file().getSection("Config.Scoreboard").getRoutesAsStrings(false)) {
            for (String permNode : RSBConfig.file().getSection("Config.Scoreboard." + world).getRoutesAsStrings(false)) {
                String scoreboardEntry = "Config.Scoreboard." + world + "." + permNode + ".";

                if (RSBConfig.file().getSection(scoreboardEntry + "Boards").getRoutesAsStrings(false).size() == 1) {
                    for (String boardName : RSBConfig.file().getSection(scoreboardEntry + "Boards").getRoutesAsStrings(false)) {
                        String boardEntry = scoreboardEntry + "Boards." + boardName;

                        List<String> title = RSBConfig.file().getStringList(boardEntry + ".Title");
                        List<String> lines = RSBConfig.file().getStringList(boardEntry + ".Lines");

                        this.scoreboards.put(permNode, new RScoreboardSingle(permNode, permNode.equalsIgnoreCase("default") ? "none" : ("realscoreboard.scoreboard" + permNode), world, title, lines,
                                20, 20, 20));
                        ++counter;
                    }
                } else {
                    List<RBoard> boards = new ArrayList<>();

                    RScoreboardBoards rsbb = new RScoreboardBoards(permNode, permNode.equalsIgnoreCase("default") ? "none" : ("realscoreboard.scoreboard" + permNode), world,
                            20, 20, 20 , RSBConfig.file().getInt(scoreboardEntry + "Switch-Timer"), permNode.equalsIgnoreCase("default")); ++counter;

                    for (String boardName : RSBConfig.file().getSection(scoreboardEntry + "Boards").getRoutesAsStrings(false)) {
                        String boardEntry = scoreboardEntry + "Boards." + boardName;

                        List<String> title = RSBConfig.file().getStringList(boardEntry + ".Title");
                        List<String> lines = RSBConfig.file().getStringList(boardEntry + ".Lines");
                        boards.add(new RBoard(title, lines));
                    }

                    boards.forEach(rBoard -> rBoard.setScoreboard(rsbb));
                    rsbb.setBoards(boards);

                    this.scoreboards.put(permNode, rsbb);
                }

                RSBConfig.file().remove("Config.Scoreboard." + world + "." + permNode);
                rsa.getLogger().warning("Converted Scoreboard " + permNode);
            }
        }

        this.scoreboards.values().forEach(RScoreboard::init);

        rsa.getLogger().warning("Converted " + counter + " scoreboards from the old config format.");

        RSBConfig.file().remove("Config.Scoreboard");
        RSBConfig.save();
    }

    @Override
    public void reload() {
        this.scoreboards.values().forEach(RScoreboard::stopTasks);
        this.scoreboards.clear();
        this.loadScoreboards();
    }

    @Override
    public Map<String, RScoreboard> getScoreboardMap() {
        return this.scoreboards;
    }

    @Override
    public List<RScoreboard> getScoreboards() {
        return new ArrayList<>(this.getScoreboardMap().values());
    }

    @Override
    public RScoreboard getScoreboardForPlayer(Player p) {
        for (RScoreboard sb : this.scoreboards.values()) {
            if (sb.getDefaultWord().equalsIgnoreCase(p.getWorld().getName()) && !sb.getPermission().equalsIgnoreCase("none") && p.hasPermission(sb.getPermission())) {
                return sb;
            }
        }

        for (RScoreboard sb : this.scoreboards.values()) {
            if (sb.getDefaultWord().equalsIgnoreCase(p.getWorld().getName()) && sb.isDefault()) {
                return sb;
            }
        }

        return null;
    }


    @Override
    public RScoreboard getScoreboard(String name) {
        return this.getScoreboardMap().get(name);
    }

    @Override
    public RScoreboard getDefaultScoreboard(World w) {
        return this.getScoreboards().stream().filter(rScoreboard -> rScoreboard.getDefaultWord().equals(w.getName()) && rScoreboard.isDefault()).findFirst().orElse(null);
    }
}