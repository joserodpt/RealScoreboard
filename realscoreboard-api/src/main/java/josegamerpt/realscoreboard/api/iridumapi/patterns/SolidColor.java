package josegamerpt.realscoreboard.api.iridumapi.patterns;

import josegamerpt.realscoreboard.api.iridumapi.IridiumAPI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SolidColor implements Patterns {

    Pattern pattern = Pattern.compile("\\{#([0-9A-Fa-f]{6})}|<#([0-9A-Fa-f]{6})>|&#([0-9A-Fa-f]{6})|#([0-9A-Fa-f]{6})");

    public String process(String string) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String color = matcher.group(1);
            if (color == null) color = matcher.group(2);
            if (color == null) color = matcher.group(3);
            if (color == null) color = matcher.group(4);
            string = string.replace(matcher.group(),
                    IridiumAPI.getColor(color) + ""
            );
        }
        return string;
    }
}