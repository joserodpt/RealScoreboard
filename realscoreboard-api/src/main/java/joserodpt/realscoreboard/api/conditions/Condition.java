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
 * @author José Rodrigues © 2016-2025
 * @link https://github.com/joserodpt/RealScoreboard
 */

import joserodpt.realscoreboard.api.RealScoreboardAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
@Setter
public class Condition {
    private String condition, met, notMet;
    private RealScoreboardAPI rsa;

    public Condition(RealScoreboardAPI rsa, String condition, String met, String notMet) {
        this.rsa = rsa;
        this.condition = condition;
        this.met = met;
        this.notMet = notMet;
    }

    public boolean parseExpression(String expression) {
        // Trim and tokenize the input string
        String[] tokens = expression.trim().split("\\s+");

        // Ensure the expression is in the correct form: "operand operator operand"
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Invalid expression format. Expected format: 'operand operator operand'");
        }

        // Get the operands and operator
        String leftOperand = tokens[0];
        String operator = tokens[1];
        String rightOperand = tokens[2];

        // Parse the operands
        Object left = parseValue(leftOperand);
        Object right = parseValue(rightOperand);

        // Evaluate the expression based on the operator
        return evaluate(left, operator, right);
    }

    // Helper method to parse operands as integers, doubles, booleans, or strings
    private Object parseValue(String value) {
        // Try to parse as an integer
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // Not an integer, continue
        }

        // Try to parse as a double
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // Not a double, continue
        }

        // Try to parse as a boolean
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }

        // Fallback to string
        return value;
    }

    // Helper method to evaluate the expression based on the operator
    private boolean evaluate(Object left, String operator, Object right) {
        // Handle numeric comparisons (Integer and Double)
        if (left instanceof Number && right instanceof Number) {
            return evaluateNumbers((Number) left, operator, (Number) right);
        }

        // Handle string comparisons
        if (left instanceof String && right instanceof String) {
            return evaluateStrings((String) left, operator, (String) right);
        }

        // Handle equality operators for other types
        return switch (operator) {
            case "==" -> left.equals(right);
            case "!=" -> !left.equals(right);
            default -> throw new UnsupportedOperationException("Unsupported operator: " + operator);
        };
    }

    // Helper method to evaluate numeric comparisons, handling both Integer and Double
    private boolean evaluateNumbers(Number left, String operator, Number right) {
        // Convert both left and right to double for comparison
        double leftDouble = left.doubleValue();
        double rightDouble = right.doubleValue();

        // Compare based on the operator
        return switch (operator) {
            case "==" -> leftDouble == rightDouble;
            case "!=" -> leftDouble != rightDouble;
            case ">" -> leftDouble > rightDouble;
            case "<" -> leftDouble < rightDouble;
            case ">=" -> leftDouble >= rightDouble;
            case "<=" -> leftDouble <= rightDouble;
            default -> throw new UnsupportedOperationException("Unsupported operator for numeric comparison: " + operator);
        };
    }

    // Helper method to evaluate string comparisons
    private boolean evaluateStrings(String left, String operator, String right) {
        return switch (operator) {
            case "==" -> left.equals(right);
            case "!=" -> !left.equals(right);
            case "contains" -> left.contains(right);
            case "startsWith" -> left.startsWith(right);
            case "endsWith" -> left.endsWith(right);
            default -> throw new UnsupportedOperationException("Unsupported operator for string comparison: " + operator);
        };
    }

    public String evaluate(Player player) {
        try {
            String evaluatedCondition = rsa.getPlaceholders().setPlaceholders(player, condition, true);

            boolean conditionMet = parseExpression(evaluatedCondition);
            String result = conditionMet ? getMet() : getNotMet();
            return rsa.getPlaceholders().setPlaceholders(player, result, false);

        } catch (Exception e) {
            Bukkit.getLogger().warning("Error parsing condition: " + condition + " for player: " + player.getName());
            e.printStackTrace();
            return "Error parsing. See console";
        }
    }
}
