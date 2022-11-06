package josegamerpt.realscoreboard.api;

import org.bukkit.entity.Player;

public abstract class Placeholders {

    public abstract int getPing(Player player);

    public abstract String setPlaceHolders(Player p, String s);
}
