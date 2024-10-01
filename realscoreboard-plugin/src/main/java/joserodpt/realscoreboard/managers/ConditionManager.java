package joserodpt.realscoreboard.managers;


import joserodpt.realscoreboard.utils.Condition;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionManager{
    private final Map<String, Condition> conditions = new HashMap<>();
    private final File conditionsFile;

    public ConditionManager(File dataFolder) {
        this.conditionsFile = new File(dataFolder, "conditions.yml");
        loadConditions();  // Llamada al método loadConditions() al inicializar la clase
    }

    public void loadConditions() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(conditionsFile);

        for (String key : config.getConfigurationSection("Conditions").getKeys(false)) {
            String conditionString = config.getString("Conditions." + key + ".condition");
            List<String> results = config.getStringList("Conditions." + key + ".result");
            conditions.put(key, new Condition(conditionString, results));
        }
    }

    public Condition getCondition(String name) {
        return conditions.get(name);
    }

    public Map<String, Condition> getAllConditions() {
        return conditions;
    }
}
