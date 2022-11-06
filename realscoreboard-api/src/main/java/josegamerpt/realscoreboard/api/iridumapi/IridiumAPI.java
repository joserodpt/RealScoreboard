package josegamerpt.realscoreboard.api.iridumapi;

import com.google.common.collect.ImmutableMap;
import josegamerpt.realscoreboard.api.iridumapi.patterns.Gradient;
import josegamerpt.realscoreboard.api.iridumapi.patterns.Patterns;
import josegamerpt.realscoreboard.api.iridumapi.patterns.Rainbow;
import josegamerpt.realscoreboard.api.iridumapi.patterns.SolidColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IridiumAPI {

    private static final int VERSION = Integer.parseInt(Bukkit.getBukkitVersion()
            .split("-")[0].split("\\.")[1]);

    private static final boolean SUPPORTS_RGB = VERSION > 15;

    private static final List<String> SPECIAL_COLORS = Arrays.asList("&l", "&n", "&o", "&k", "&m");

    private static final Map<Color, ChatColor> COLORS = ImmutableMap.<Color, ChatColor>builder()
            .put(new Color(0), ChatColor.getByChar('0'))
            .put(new Color(170), ChatColor.getByChar('1'))
            .put(new Color(43520), ChatColor.getByChar('2'))
            .put(new Color(43690), ChatColor.getByChar('3'))
            .put(new Color(11141120), ChatColor.getByChar('4'))
            .put(new Color(11141290), ChatColor.getByChar('5'))
            .put(new Color(16755200), ChatColor.getByChar('6'))
            .put(new Color(11184810), ChatColor.getByChar('7'))
            .put(new Color(5592405), ChatColor.getByChar('8'))
            .put(new Color(5592575), ChatColor.getByChar('9'))
            .put(new Color(5635925), ChatColor.getByChar('a'))
            .put(new Color(5636095), ChatColor.getByChar('b'))
            .put(new Color(16733525), ChatColor.getByChar('c'))
            .put(new Color(16733695), ChatColor.getByChar('d'))
            .put(new Color(16777045), ChatColor.getByChar('e'))
            .put(new Color(16777215), ChatColor.getByChar('f')).build();

    private static final List<Patterns> PATTERNS = Arrays.asList(new Gradient(), new SolidColor(), new Rainbow());

    
    public static String process(String string) {
        for (Patterns pattern : PATTERNS) string = pattern.process(string);
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    
    public static List<String> process(List<String> strings) {
        return strings.stream()
                .map(IridiumAPI::process)
                .collect(Collectors.toList());
    }

    
    public static String color( String string,  Color color) {
        return (SUPPORTS_RGB ? ChatColor.of(color) : getClosestColor(color)) + string;
    }

    
    public static String color( String string,  Color start,  Color end) {
        StringBuilder specialColors = new StringBuilder();
        for (String color : SPECIAL_COLORS) {
            if (!string.contains(color)) continue;
            specialColors.append(color);
            string = string.replace(color, "");
        }

        StringBuilder stringBuilder = new StringBuilder();
        ChatColor[] colors = createGradient(start, end, string.length());
        String[] characters = string.split("");

        for (int i = 0; i < string.length(); i++)
            stringBuilder.append(specialColors).append(colors[i]).append(characters[i]);
        return stringBuilder.toString();
    }

    
    public static String rainbow( String string, float saturation) {
        StringBuilder specialColors = new StringBuilder();
        for (String color : SPECIAL_COLORS) {
            if (!string.contains(color)) continue;
            specialColors.append(color);
            string = string.replace(color, "");
        }

        StringBuilder stringBuilder = new StringBuilder();
        ChatColor[] colors = createRainbow(string.length(), saturation);
        String[] characters = string.split("");

        for (int i = 0; i < string.length(); i++)
            stringBuilder.append(specialColors).append(colors[i]).append(characters[i]);
        return stringBuilder.toString();
    }

    
    public static ChatColor getColor( String string) {
        return SUPPORTS_RGB ? ChatColor.of(new Color(Integer.parseInt(string, 16)))
                : getClosestColor(new Color(Integer.parseInt(string, 16)));
    }

    
    public static String stripColor( String string) {
        return string.replaceAll("[&§][a-f0-9lnokm]|<[/]?\\w(:[0-9A-F]{6})?>|\\{#([0-9A-Fa-f]{6})}|" +
                "<#([0-9A-Fa-f]{6})>|&#([0-9A-Fa-f]{6})|#([0-9A-Fa-f]{6})", "");
    }

    
    private static ChatColor[] createRainbow(int step, float saturation) {
        ChatColor[] colors = new ChatColor[step];
        double colorStep = (1.00 / step);
        for (int i = 0; i < step; i++) {
            Color color = Color.getHSBColor((float) (colorStep * i), saturation, saturation);
            colors[i] = SUPPORTS_RGB ? ChatColor.of(color) : getClosestColor(color);
        }
        return colors;
    }

    
    private static ChatColor[] createGradient( Color start,  Color end, int step) {
        ChatColor[] colors = new ChatColor[step];
        int stepR = Math.abs(start.getRed() - end.getRed()) / (step - 1);
        int stepG = Math.abs(start.getGreen() - end.getGreen()) / (step - 1);
        int stepB = Math.abs(start.getBlue() - end.getBlue()) / (step - 1);
        int[] direction = new int[]{
                start.getRed() < end.getRed() ? +1 : -1,
                start.getGreen() < end.getGreen() ? +1 : -1,
                start.getBlue() < end.getBlue() ? +1 : -1
        };

        for (int i = 0; i < step; i++) {
            Color color = new Color(start.getRed() + ((stepR * i) * direction[0]),
                    start.getGreen() + ((stepG * i) * direction[1]),
                    start.getBlue() + ((stepB * i) * direction[2]));
            colors[i] = SUPPORTS_RGB ? ChatColor.of(color) : getClosestColor(color);
        }
        return colors;
    }

    
    private static ChatColor getClosestColor(Color color) {
        Color nearestColor = null;
        double nearestDistance = Integer.MAX_VALUE;

        for (Color constantColor : COLORS.keySet()) {
            double distance = Math.pow(color.getRed() - constantColor.getRed(), 2)
                    + Math.pow(color.getGreen() - constantColor.getGreen(), 2)
                    + Math.pow(color.getBlue() - constantColor.getBlue(), 2);
            if (nearestDistance > distance) {
                nearestColor = constantColor;
                nearestDistance = distance;
            }
        }
        return COLORS.get(nearestColor);
    }
} 