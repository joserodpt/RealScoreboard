package joserodpt.realscoreboard.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

public class ConditionEvaluator {

    // Método para evaluar la condición
    public boolean evaluateCondition(Player player, String condition) {
        // Reemplaza los placeholders utilizando PlaceholderAPI
        String evaluatedCondition = PlaceholderAPI.setPlaceholders(player, condition);

        // Crear un motor de script para evaluar la expresión
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        try {
            // Evalúa la expresión
            Object operation = engine.eval(evaluatedCondition);

            // Verifica que el resultado es booleano
            if (operation instanceof Boolean) {
                return (Boolean) operation;
            } else {
                System.out.println("La condición no devolvió un valor booleano: " + operation);
                return false;
            }
        } catch (ScriptException e) {
            // Manejo de excepciones, loguear o manejar errores
            e.printStackTrace();
            return false;
        }
    }

    // Método para obtener los resultados si la condición se cumple
    public List<String> getResultsIfConditionMet(Player player, Condition condition) {
        if (evaluateCondition(player, condition.getCondition())) {
            return condition.getResult(); // Retorna los resultados si la condición se cumple
        }
        return null; // Retorna null si la condición no se cumple
    }
}