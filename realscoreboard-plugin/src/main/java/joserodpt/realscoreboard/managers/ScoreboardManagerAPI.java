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
 * @author José Rodrigues © 2016-2024
 * @link https://github.com/joserodpt/RealScoreboard
 */

import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.config.RSBScoreboards;
import joserodpt.realscoreboard.api.scoreboard.*;
import joserodpt.realscoreboard.utils.Condition;
import joserodpt.realscoreboard.utils.ConditionEvaluator;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardManagerAPI implements joserodpt.realscoreboard.api.managers.ScoreboardManagerAPI {
    private final Map<String, RScoreboard> scoreboards = new HashMap<>();
    private final RealScoreboardAPI rsa;
    private final ConditionEvaluator conditionEvaluator; // Instancia de ConditionEvaluator
    private final PlayerManagerAPI playerManager; // Instancia de PlayerManagerAPI
    private final ConditionManager conditionManager; // Instancia de ConditionManagerAPI

    public ScoreboardManagerAPI(RealScoreboardAPI rsa, PlayerManagerAPI playerManager, ConditionManager conditionManager) {
        this.rsa = rsa;
        this.playerManager = playerManager; // Inicializar playerManager
        this.conditionManager = conditionManager; // Inicializar conditionManagerApi
        this.conditionEvaluator = new ConditionEvaluator();
    }

    @Override
    public void loadScoreboards() {
        //starting from version 1.4, scoreboards are stored in the scoreboards.yml file and have a new structure,
        //this next part of the code is responsible for the conversion of those old scoreboards in the config.yml
        if (RSBConfig.file().contains("Config.Scoreboard")) {
            rsa.getLogger().warning("Starting scoreboard conversion to the new scoreboards.yml file...");
            convertOldScoreboardsV1dot4();
        }

        if (!RSBScoreboards.file().contains("Scoreboards") || RSBScoreboards.file().getSection("Scoreboards") == null) {
            rsa.getLogger().severe("There seems to be no valid scoreboards in the scoreboards.yml file!");
            return;
        }

        for (String scoreboardName : RSBScoreboards.file().getSection("Scoreboards").getRoutesAsStrings(false)) {
            //verify that this scoreboard is not loaded
            if (this.scoreboards.containsKey(scoreboardName)) {
                continue;
            }

            String key = "Scoreboards." + scoreboardName + ".";
            String w = RSBScoreboards.file().getString(key + "Default-World");

            boolean def = RSBScoreboards.file().getBoolean(key + "Default");
            String displayName = RSBScoreboards.file().getString(key + "Display-Name");
            String permission = RSBScoreboards.file().getString(key + "Permission");

            int titleRefresh = RSBScoreboards.file().getInt(key + "Refresh.Title");
            int titleLoopDelay = RSBScoreboards.file().getInt(key + "Refresh.Title-Loop-Delay");
            int globalScoreboardRefresh = RSBScoreboards.file().getInt(key + "Refresh.Scoreboard");

            List<String> otherWorlds = RSBScoreboards.file().getStringList(key + "Other-Worlds");

            // if config has lines, it has only one board, else, two or more

            if (RSBScoreboards.file().contains(key + "Lines")) {
                List<String> title = RSBScoreboards.file().getStringList(key + "Title");
                List<String> lines = RSBScoreboards.file().getStringList(key + "Lines");

                // Lista para almacenar las líneas finales
                List<String> finalLines = new ArrayList<>();

                // Iterar a través de los jugadores en el mapa
                for (RSBPlayer rsbPlayer : playerManager.getPlayerMap().values()) {
                    Player player = rsbPlayer.getPlayer(); // Asumiendo que tienes un método para obtener el Player

                    // Procesar las líneas para el jugador actual
                    for (String line : lines) {
                        // Comprobar si la línea empieza con $condition:
                        if (line.startsWith("$condition:")) {
                            // Extraer el nombre de la condición
                            String conditionName = line.substring(11, line.indexOf(" ", 11) != -1 ? line.indexOf(" ", 11) : line.length()).trim();

                            // Obtener la condición utilizando su nombre
                            Condition condition = conditionManager.getCondition(conditionName); // Asegúrate de que este método esté implementado

                            // Evaluar la condición y obtener los resultados
                            List<String> results = conditionEvaluator.getResultsIfConditionMet(player, condition);
                            if (results != null) {
                                finalLines.addAll(results); // Agregar los resultados si la condición se cumple
                            }
                        } else {
                            // Si la línea no tiene condición, agregarla directamente
                            finalLines.add(line);
                        }
                    }
                }

                // Agregar el scoreboard con las líneas procesadas
                this.scoreboards.put(scoreboardName, new RScoreboardSingle(scoreboardName, displayName, permission, w, otherWorlds, title, finalLines,
                        titleRefresh, titleLoopDelay, globalScoreboardRefresh, def));
            } else {
                this.scoreboards.put(scoreboardName, new RScoreboardBoards(scoreboardName, displayName, permission, w, otherWorlds,
                        titleRefresh, titleLoopDelay, globalScoreboardRefresh, RSBScoreboards.file().getInt(key + "Refresh.Board-Loop-Delay"), def));
            }

            // if config has lines, it has only one board, else, two or more

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
                RSBScoreboards.file().remove("Scoreboards." + permNode);
                String oldScoreboardEntry = "Config.Scoreboard." + world + "." + permNode + ".";
                String newPermission = permNode.equalsIgnoreCase("default") ? "none" : ("realscoreboard.scoreboard" + permNode);

                if (RSBConfig.file().getSection(oldScoreboardEntry + "Boards").getRoutesAsStrings(false).size() == 1) {
                    for (String boardName : RSBConfig.file().getSection(oldScoreboardEntry + "Boards").getRoutesAsStrings(false)) {
                        String boardEntry = oldScoreboardEntry + "Boards." + boardName;

                        List<String> title = RSBConfig.file().getStringList(boardEntry + ".Title");
                        List<String> lines = RSBConfig.file().getStringList(boardEntry + ".Lines");

                        this.scoreboards.put(permNode, new RScoreboardSingle(permNode, newPermission, world, Collections.emptyList(), title, lines,
                                RSBConfig.file().getInt("Config.Animations.Title-Delay"), RSBConfig.file().getInt("Config.Animations.Loop-Delay"), RSBConfig.file().getInt("Config.Scoreboard-Refresh"), permNode.equalsIgnoreCase("default"), true));
                        ++counter;
                    }
                } else {
                    List<RBoard> boards = new ArrayList<>();

                    RScoreboardBoards rsbb = new RScoreboardBoards(permNode, newPermission, world, Collections.emptyList(),
                            RSBConfig.file().getInt("Config.Animations.Title-Delay"), RSBConfig.file().getInt("Config.Animations.Loop-Delay"), RSBConfig.file().getInt("Config.Scoreboard-Refresh"), RSBConfig.file().getInt(oldScoreboardEntry + "Switch-Timer"), permNode.equalsIgnoreCase("default")); ++counter;

                    for (String boardName : RSBConfig.file().getSection(oldScoreboardEntry + "Boards").getRoutesAsStrings(false)) {
                        String boardEntry = oldScoreboardEntry + "Boards." + boardName;

                        List<String> title = RSBConfig.file().getStringList(boardEntry + ".Title");
                        List<String> lines = RSBConfig.file().getStringList(boardEntry + ".Lines");
                        boards.add(new RBoard(title, lines));
                    }

                    boards.forEach(rBoard -> rBoard.setScoreboard(rsbb));
                    rsbb.setBoards(boards);

                    this.scoreboards.put(permNode, rsbb);
                }

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
    public Collection<RScoreboard> getScoreboards() {
        return this.getScoreboardMap().values();
    }

    @Override
    public RScoreboard getScoreboardForPlayer(Player p) {
        for (RScoreboard sb : this.scoreboards.values()) {
            if (sb.isInWorld(p.getWorld()) && !sb.getPermission().equalsIgnoreCase("none") && p.hasPermission(sb.getPermission())) {
                return sb;
            }
        }

        for (RScoreboard sb : this.scoreboards.values()) {
            if (sb.isInWorld(p.getWorld()) && sb.isDefault()) {
                return sb;
            }
        }

        return null;
    }


    @Override
    public RScoreboard getScoreboard(String name) {
        return this.getScoreboardMap().get(name);
    }
}