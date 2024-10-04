package joserodpt.realscoreboard.api.conditions;

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

import java.util.HashMap;
import java.util.Map;

public class ConditionManager {

    private final RealScoreboardAPI rsa;
    private final Map<String, Condition> conditions = new HashMap<>();

    public ConditionManager(RealScoreboardAPI rsa) {
        this.rsa = rsa;
        loadConditions();
    }

    public void loadConditions() {
        conditions.clear();
        for (String key : RSBConfig.file().getSection("Config.Conditions").getRoutesAsStrings(false)) {
            String conditionString = RSBConfig.file().getString("Config.Conditions." + key + ".Condition");
            String met = RSBConfig.file().getString("Config.Conditions." + key + ".Met");
            String notMet = RSBConfig.file().getString("Config.Conditions." + key + ".Not-Met");
            conditions.put(key, new Condition(rsa, conditionString, met, notMet));
        }
        if (!conditions.isEmpty()) {
            rsa.getLogger().info("Loaded " + conditions.size() + " conditions.");
        }
    }

    public Condition getCondition(String name) {
        return conditions.get(name);
    }
}
