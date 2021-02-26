package josegamerpt.realscoreboard.utils;

import josegamerpt.realscoreboard.RealScoreboard;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Placeholders {

    private static Class<?> CPClass;

    public static int ping(Player p) {
        try {
            CPClass = Class.forName("org.bukkit.craftbukkit." + RealScoreboard.getServerVersion() + ".entity.CraftPlayer");
            Object CraftPlayer = CPClass.cast(p);

            Method getHandle = CraftPlayer.getClass().getMethod("getHandle", new Class[0]);
            Object EntityPlayer = getHandle.invoke(CraftPlayer, new Object[0]);

            Field ping = EntityPlayer.getClass().getDeclaredField("ping");

            return ping.getInt(EntityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static String ram() {
        Runtime r = Runtime.getRuntime();
        return ((r.totalMemory() - r.freeMemory()) / 1048576) + "MB";
    }

    private static int port() {
        return Bukkit.getPort();
    }

    private static String serverIP() {
        return Bukkit.getIp();
    }

    private static String time() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private static String day() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }

    private static String cords(Player player) {
        return "X: " + player.getLocation().getBlockX() + " Y: " + player.getLocation().getBlockY() + " Z: "
                + player.getLocation().getBlockZ();
    }

    private static int onlinePlayers() {
        return Bukkit.getOnlinePlayers().size();
    }

    private static int maxPlayers() {
        return Bukkit.getMaxPlayers();
    }

    private static String getVersion() {
        return Bukkit.getBukkitVersion();
    }

    private static String getWorldName(Player p) {
        return p.getLocation().getWorld().getName();
    }

    private static String getGroup(Player p) {
        if (RealScoreboard.getPerms() != null)
        {
            try {
                String w = RealScoreboard.getPerms().getPrimaryGroup(p);
                if (w == null) {
                    return "None";
                }
                return Text.color(w);
            } catch (UnsupportedOperationException e)
            {
                return "No Perm Plugin";
            }
        } else {
            return "Missing Vault";
        }

    }

    private static String prefix(Player p) {
        if (RealScoreboard.getChat() != null) {
            String grupo = RealScoreboard.getChat().getPrimaryGroup(p);
            String prefix = RealScoreboard.getChat().getGroupPrefix(p.getWorld(), grupo);
            if (grupo == null) {
                return "None";
            }
            if (prefix == null) {
                return "None";
            }
            return Text.color(prefix);
        } else {
            return "Missing Vault";
        }
    }

    private static String sufix(Player p) {
        if (RealScoreboard.getChat() != null) {
            String grupo = RealScoreboard.getChat().getPrimaryGroup(p);
            String prefix = RealScoreboard.getChat().getGroupSuffix(p.getWorld(), grupo);
            if (grupo == null) {
                return "None";
            }
            if (prefix == null) {
                return "None";
            }
            return Text.color(prefix);
        } else {
            return "Missing Vault";
        }
    }

    private static double money(Player p) {
        if (RealScoreboard.getEconomy() == null) {
            return -1D;
        }
        return RealScoreboard.getEconomy().getBalance(p);
    }

    private static int stats(Player p, Statistic s) {
        return p.getStatistic(s);
    }

    private static String getKD(Player p) {
        int kills = p.getStatistic(Statistic.PLAYER_KILLS);
        int deaths = p.getStatistic(Statistic.DEATHS);
        if (deaths != 0) {
          String send = (kills / deaths) + "";
          return send.contains(".") ? send.substring(0, send.indexOf(".")) : send;
        } 
        return "0";
    }

    public static String setPlaceHolders(Player p, String s) {
        try {
            String placeholders = s.replaceAll("&", "§").replaceAll("%playername%", p.getName())
                    .replaceAll("%loc%", cords(p))
                    .replaceAll("%life%", p.getHealth() + "")
                    .replaceAll("%time%", time())
                    .replaceAll("%day%", day())
                    .replaceAll("%serverip%", serverIP())
                    .replaceAll("%version%", getVersion())
                    .replaceAll("%ping%", ping(p) + " ms")
                    .replaceAll("%ram%", ram())
                    .replaceAll("%jumps%", "" + stats(p, Statistic.JUMP))
                    .replaceAll("%mobkills%", "" + stats(p, Statistic.MOB_KILLS))
                    .replaceAll("%world%", getWorldName(p)).replaceAll("%port%", String.valueOf(port()))
                    .replaceAll("%maxplayers%", String.valueOf(maxPlayers()))
                    .replaceAll("%online%", String.valueOf(onlinePlayers()))
                    .replaceAll("%prefix%", prefix(p))
                    .replaceAll("%suffix%", sufix(p)).replaceAll("%yaw%", String.valueOf(p.getLocation().getYaw()))
                    .replaceAll("%kills%", String.valueOf(stats(p, Statistic.PLAYER_KILLS)))
                    .replaceAll("%deaths%", String.valueOf(stats(p, Statistic.DEATHS)))
                    .replaceAll("%kd%", getKD(p))
                    .replaceAll("%pitch%", String.valueOf(p.getLocation().getPitch()))
                    .replaceAll("%group%", getGroup(p))
                    .replaceAll("%money%", Text.formatMoney(money(p)))
                    .replaceAll("%displayname%", p.getDisplayName())
                    .replaceAll("%xp%", p.getTotalExperience() + "")
                    .replaceAll("%x%", p.getLocation().getBlockX() + "")
                    .replaceAll("%y%", p.getLocation().getBlockY() + "")
                    .replaceAll("%z%", p.getLocation().getBlockZ() + "")
                    .replaceAll("%playtime%", Text.formatTime(stats(p, Statistic.PLAY_ONE_MINUTE) / 20) + "");
            return placeholderAPI(p, placeholders);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return "RealScoreboard";
    }

    private static String placeholderAPI(Player p, String placeholders) {
        return RealScoreboard.placeholderAPI ? PlaceholderAPI.setPlaceholders(p, placeholders) : placeholders;
    }
}
