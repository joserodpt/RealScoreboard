package josegamerpt.realscoreboard.managers;

import java.util.HashMap;

import org.bukkit.entity.Player;

import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.config.Data;
import josegamerpt.realscoreboard.utils.Placeholders;

public class TitleManager {

    static HashMap<String, String> texto = new HashMap<String, String>();
    static int i = 0;

    public static void startAnimation(Player p) {
        String go = "Config.Scoreboard." + Data.getCorrectPlace(p) + ".Title";
		try {
            if (TitleManager.i >= Config.file().getStringList(go).size()) {
                TitleManager.i = 0;
            }
            texto.put(p.getName(), Config.file().getStringList(go).get(TitleManager.i).replaceAll("&", "§"));
            ++TitleManager.i;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getTitleAnimation(Player p) {
        return TitleManager.texto.get(p.getName());
    }
}
