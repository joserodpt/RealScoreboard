package joserodpt.realscoreboard.api.utils;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.Arrays;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// CREDIT: https://github.com/ForestTechMC/ForestColorAPI

public class ForestColorAPI {

    private static final List<String> legacyColors = Arrays.asList("&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a", "§b", "§c", "§d", "§e");
    private static final List<String> specialChars = Arrays.asList("&l", "&n", "&o", "&k", "&m", "§l", "§n", "§o", "§k", "§m");
    private static final Pattern patternNormal = Pattern.compile("\\{#([0-9A-Fa-f]{6})}");
    private static final Pattern patternGrad = Pattern.compile("\\{#([0-9A-Fa-f]{6})>}(.*?)\\{#([0-9A-Fa-f]{6})<}");
    private static final Pattern patternOneFromTwo = Pattern.compile("\\{#([0-9A-Fa-f]{6})<>}");
    private static Matcher matcher;

    /**
     *
     * This method clear whole string
     *
     * @return string without patterns, legacy colors, and special chars
     */
    public static String clear(String input) {
        input = removePatterns(input);
        input = removeLegacyColors(input);
        input = removeSpecialChars(input);
        return input;
    }

    /**
     *
     * In this method we remove specialChars like "&n", "&r"...
     * Example:
     * "&kForestTech ❤" -> "ForestTech ❤"
     *
     * @param input message
     * @return output
     */
    public static String removeSpecialChars(String input) {
        for (String chars : specialChars) {
            if (!input.contains(chars)) {
                continue;
            }
            input = input.replaceAll(chars, "");
        }

        return input;
    }

    /**
     *
     * In this method we remove legacyColors like "&2", "&c"...
     * Example:
     * "&2ForestTech ❤" -> "ForestTech ❤"
     *
     * @param input message
     * @return output
     */
    public static String removeLegacyColors(String input) {
        for (String color : legacyColors) {
            if (!input.contains(color)) {
                continue;
            }
            input = input.replaceAll(color, "");
        }
        return input;
    }

    /**
     *
     * If we want to remove patterns from message we can use this
     * Example:
     * "{#00e64e}ForestTech ❤" -> "ForestTech ❤"
     *
     * @param input message with patterns
     * @return string without patterns
     */
    public static String removePatterns(String input) {
        input = input.replaceAll("\\{#([0-9A-Fa-f]{6})>}", "");
        input = input.replaceAll("\\{#([0-9A-Fa-f]{6})<}", "");
        input = input.replaceAll("\\{#([0-9A-Fa-f]{6})}", "");
        return input;
    }

    /**
     *
     * Universal colorize method for add colors into message
     *
     * @param input message
     * @return colored message
     */
    public static String colorize(String input) {
        return colorizeRGB(colorizeGradient(input));
    }

    /**
     *
     * Method for normal translate colors from spigot
     *
     */
    public static String colorizeClassic(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    /**
     *
     * Here we can call for gradient colorize
     * Group 1 = First gradient
     * Group 3 = Second gradient
     * Group 2 = content
     *
     * @param input message
     * @return colored output with gradient
     */
    public static String colorizeGradient(String input) {
        Matcher matcher = patternOneFromTwo.matcher(input);
        StringBuffer output = new StringBuffer();

        // First pass to handle the patternOneFromTwo matches
        while (matcher.find()) {
            String text = matcher.group(1);
            matcher.appendReplacement(output, "{#" + text + "<}{#" + text + ">}");
        }
        matcher.appendTail(output);
        input = output.toString();

        // Clear the StringBuffer for the next phase
        output.setLength(0);

        // Second pass to handle the patternGrad matches
        matcher = patternGrad.matcher(input);
        while (matcher.find()) {
            String replacement = color(
                    matcher.group(2),
                    new Color(Integer.parseInt(matcher.group(1), 16)),
                    new Color(Integer.parseInt(matcher.group(3), 16))
            );
            matcher.appendReplacement(output, replacement);
        }
        matcher.appendTail(output);

        return ChatColor.translateAlternateColorCodes('&', output.toString());
    }

    /**
     *
     * This method only do normal RGB without gradient
     *
     * @param input message
     * @return colored rgb output
     */
    public static String colorizeRGB(String input) {
        Matcher matcher = patternNormal.matcher(input);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String color = matcher.group(1);
            // Replace the entire matched substring with the colored text
            matcher.appendReplacement(result, getColor(color).toString());
        }
        matcher.appendTail(result);
        return result.toString();
    }


    /**
     *
     * Color method for gradient for example this take
     * the first one gradient, and second one, and do gradient in the message
     *
     * @param input  whole message
     * @param first  first gradient
     * @param second second gradient
     */
    public static String color(String input, Color first, Color second) {
        ChatColor[] colors = createGradient(first, second, removeSpecialChars(input).length());
        return apply(input, colors);
    }

    private static String apply(String input, ChatColor[] colors) {
        StringBuilder specialColors = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        String[] characters = input.split("");
        int outIndex = 0;

        for (int i = 0; i < characters.length; i++) {
            if (!characters[i].equals("&") && !characters[i].equals("§")) {
                stringBuilder.append(colors[outIndex++]).append(specialColors).append(characters[i]);
                continue;
            }
            if (i + 1 >= characters.length) {
                stringBuilder.append(colors[outIndex++]).append(specialColors).append(characters[i]);
                continue;
            }
            if (characters[i + 1].equals("r")) {
                specialColors.setLength(0);
            } else {
                specialColors.append(characters[i]);
                specialColors.append(characters[i + 1]);
            }
            i++;
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @return colors for string
     *
     */
    private static ChatColor[] createGradient(Color first, Color second, int amount) {
        ChatColor[] colors = new ChatColor[amount];
        int amountR = Math.abs(first.getRed() - second.getRed()) / (amount - 1);
        int amountG = Math.abs(first.getGreen() - second.getGreen()) / (amount - 1);
        int amountB = Math.abs(first.getBlue() - second.getBlue()) / (amount - 1);
        int[] colorDir = new int[]{first.getRed() < second.getRed() ? +1 : -1, first.getGreen() < second.getGreen() ? +1 : -1, first.getBlue() < second.getBlue() ? +1 : -1};

        for (int i = 0; i < amount; i++) {
            Color color = new Color(first.getRed() + ((amountR * i) * colorDir[0]), first.getGreen() + ((amountG * i) * colorDir[1]), first.getBlue() + ((amountB * i) * colorDir[2]));
            colors[i] = ChatColor.of(color);
        }
        return colors;
    }

    public static ChatColor getColor(String matcher) {
        return ChatColor.of(new Color(Integer.parseInt(matcher, 16)));
    }

}