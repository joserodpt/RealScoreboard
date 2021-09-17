package josegamerpt.realscoreboard.utils;

import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.config.Config;
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

    private Method getHandleMethod;
    private Field pingField;

    public int getPing(Player player) {
        try {
            if (this.getHandleMethod == null) {
                this.getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
                this.getHandleMethod.setAccessible(true);
            }
            Object entityPlayer = this.getHandleMethod.invoke(player);
            if (this.pingField == null) {
                this.pingField = entityPlayer.getClass().getDeclaredField("ping");
                this.pingField.setAccessible(true);
            }
            int ping = this.pingField.getInt(entityPlayer);
            return Math.max(ping, 0);
        } catch (Exception e) {
            return 1;
        }
    }

    private String ram() {
        Runtime r = Runtime.getRuntime();
        return ((r.totalMemory() - r.freeMemory()) / 1048576) + "MB";
    }

    private int port() {
        return Bukkit.getPort();
    }

    private String serverIP() {
        return Bukkit.getIp();
    }

    private String time() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private String day() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }

    private String cords(Player player) {
        return "X: " + player.getLocation().getBlockX() + " Y: " + player.getLocation().getBlockY() + " Z: "
                + player.getLocation().getBlockZ();
    }

    private int onlinePlayers() {
        return Bukkit.getOnlinePlayers().size();
    }

    private int maxPlayers() {
        return Bukkit.getMaxPlayers();
    }

    private String getVersion() {
        return Bukkit.getBukkitVersion();
    }

    private String getVersionShort() {
        String a = Bukkit.getServer().getClass().getPackage().getName();
        return a.substring(a.lastIndexOf('.') + 1);
    }


    private String getWorldName(Player p) {
        return p.getLocation().getWorld().getName();
    }

    private String getGroup(Player p) {
        if (RealScoreboard.getInstance().getPerms() != null) {
            try {
                String w = RealScoreboard.getInstance().getPerms().getPrimaryGroup(p);
                if (w == null) {
                    return "None";
                }
                return w;
            } catch (UnsupportedOperationException e) {
                return "No Perm Plugin";
            }
        } else {
            return "Missing Vault";
        }
    }

    private String prefix(Player p) {
        if (RealScoreboard.getInstance().getChat() != null) {
            String grupo = RealScoreboard.getInstance().getChat().getPrimaryGroup(p);
            String prefix = RealScoreboard.getInstance().getChat().getGroupPrefix(p.getWorld(), grupo);
            if (grupo == null) {
                return "None";
            }
            if (prefix == null) {
                return "None";
            }
            return prefix;
        } else {
            return "Missing Vault";
        }
    }

    private String sufix(Player p) {
        if (RealScoreboard.getInstance().getChat() != null) {
            String grupo = RealScoreboard.getInstance().getChat().getPrimaryGroup(p);
            String prefix = RealScoreboard.getInstance().getChat().getGroupSuffix(p.getWorld(), grupo);
            if (grupo == null) {
                return "None";
            }
            if (prefix == null) {
                return "None";
            }
            return prefix;
        } else {
            return "Missing Vault";
        }
    }

    private double money(Player p) {
        if (RealScoreboard.getInstance().getEconomy() == null) {
            return -1D;
        }
        return RealScoreboard.getInstance().getEconomy().getBalance(p);
    }

    private int stats(Player p, Statistic s) {
        return p.getStatistic(s);
    }

    private String getKD(Player p) {
        int kills = p.getStatistic(Statistic.PLAYER_KILLS);
        int deaths = p.getStatistic(Statistic.DEATHS);

        if (deaths != 0) {
            String send = (kills / deaths) + "";
            return send.contains(".") ? send.substring(0, send.indexOf(".")) : send;
        }
        return "0";
    }

    private String lifeHeart(long round) {
        String heart = "❤";
        if (round <= 5) {
            return "&c" + heart;
        }
        if (round <= 10) {
            return "&e" + heart;
        }
        if (round <= 15) {
            return "&6" + heart;
        }
        if (round <= 20) {
            return "&a" + heart;
        }
        return heart;
    }

    public String setPlaceHolders(Player p, String s) {
        String placeholders = s.replaceAll("%playername%", p.getName())
                .replaceAll("%loc%", cords(p))
                .replaceAll("%life%", Math.round(p.getHealth()) + "")
                .replaceAll("%lifeheart%", lifeHeart(Math.round(p.getHealth())) + "")
                .replaceAll("%time%", time())
                .replaceAll("%day%", day())
                .replaceAll("%serverip%", serverIP())
                .replaceAll("%version%", getVersion())
                .replaceAll("%versionshort%", getVersion())
                .replaceAll("%ping%", getPing(p) + " ms")
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
                .replaceAll("%playerfood%", String.valueOf(p.getFoodLevel()))
                .replaceAll("%group%", getGroup(p))
                .replaceAll("%money%", Text.formatMoney(money(p)))
                .replaceAll("%moneylong%", Text.formatMoneyLong(money(p)))
                .replaceAll("%displayname%", p.getDisplayName())
                .replaceAll("%xp%", p.getTotalExperience() + "")
                .replaceAll("%x%", p.getLocation().getBlockX() + "")
                .replaceAll("%y%", p.getLocation().getBlockY() + "")
                .replaceAll("%z%", p.getLocation().getBlockZ() + "")
                .replaceAll("%rainbow%", RealScoreboard.getInstance().getAnimationManager().getLoopAnimation("rainbow"))
                .replaceAll("%playtime%", Text.formatTime(stats(p, Statistic.PLAY_ONE_MINUTE) / 20) + "");
        return placeholderAPI(p, placeholders);
    }


    private String placeholderAPI(Player p, String placeholders) {
        return RealScoreboard.getInstance().placeholderAPI ? PlaceholderAPI.setPlaceholders(p, placeholders) : placeholders;
    }
}
