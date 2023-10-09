package joserodpt.realscoreboard.utils;

/*
 *   ____            _ ____                     _                         _
 *  |  _ \ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |
 *  | |_) / _ \/ _` | \___ \ / __/ _ \| '__/ _ \ '_ \ / _ \ / _` | '__/ _` |
 *  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |
 *  |_| \_\___|\__,_|_|____/ \___\___/|_|  \___|_.__/ \___/ \__,_|_|  \__,_|
 *
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealScoreboard
 */

import joserodpt.realscoreboard.RealScoreboard;
import joserodpt.realscoreboard.RealScoreboardPlugin;
import joserodpt.realscoreboard.api.IPlaceholders;
import joserodpt.realscoreboard.api.config.Config;
import joserodpt.realscoreboard.api.utils.Text;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Placeholders implements IPlaceholders {

    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private String nmsVersion;
    private Method getHandleMethod;
    private Method getPingMethod;
    private Field pingField;

    @Override
    public int getPing(Player player) {
        try {
            if (this.getHandleMethod == null) {
                this.getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
                this.getHandleMethod.setAccessible(true);
            }
            Object entityPlayer = this.getHandleMethod.invoke(player);
            if (this.pingField == null && this.getPingMethod == null) {
                try {
                    this.pingField = entityPlayer.getClass().getDeclaredField("ping");
                } catch (NoSuchFieldError | NoSuchFieldException e) {
                    this.getPingMethod = Class.forName("org.bukkit.craftbukkit." + this.getNmsVersion() + ".entity.CraftPlayer").getDeclaredMethod("getPing");
                }
                if (this.pingField != null)
                    this.pingField.setAccessible(true);
            }
            int ping;
            if (this.getPingMethod != null)
                ping = (int) this.getPingMethod.invoke(player);
            else
                ping = this.pingField.getInt(entityPlayer);
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
        DateFormat dateFormat = new SimpleDateFormat(Config.file().getString("Config.Hours.Formatting"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, Config.file().getInt("Config.Hours.Offset"));
        cal.getTime();
        return dateFormat.format(cal.getTime());
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

    private String getWorldName(Player p) {
        return p.getLocation().getWorld().getName();
    }

    private String getGroup(Player p) {
        if (RealScoreboardPlugin.getInstance().getPerms() != null) try {
            String w = RealScoreboardPlugin.getInstance().getPerms().getPrimaryGroup(p);
            if (w == null) return "None";
            return w;
        } catch (UnsupportedOperationException e) {
            return "No Perm Plugin";
        }
        else return "Missing Vault";
    }

    private String prefix(Player p) {
        if (RealScoreboardPlugin.getInstance().getChat() != null) {
            String grupo = RealScoreboardPlugin.getInstance().getChat().getPrimaryGroup(p);
            String prefix = RealScoreboardPlugin.getInstance().getChat().getGroupPrefix(p.getWorld(), grupo);
            if (grupo == null) return "None";
            if (prefix == null) return "None";
            return prefix;
        } else return "Missing Vault";
    }

    private String sufix(Player p) {
        if (RealScoreboardPlugin.getInstance().getChat() != null) {
            String grupo = RealScoreboardPlugin.getInstance().getChat().getPrimaryGroup(p);
            String prefix = RealScoreboardPlugin.getInstance().getChat().getGroupSuffix(p.getWorld(), grupo);
            if (grupo == null) return "None";
            if (prefix == null) return "None";
            return prefix;
        } else return "Missing Vault";
    }

    private double money(Player p) {
        if (RealScoreboardPlugin.getInstance().getEconomy() == null) return -1D;
        return RealScoreboardPlugin.getInstance().getEconomy().getBalance(p);
    }

    private int stats(Player p, Statistic s) {
        return p.getStatistic(s);
    }

    private String getKD(Player p) {
        int kills = p.getStatistic(Statistic.PLAYER_KILLS);
        int deaths = p.getStatistic(Statistic.DEATHS);

        if (deaths != 0) {
            double kd = (double) kills / deaths;

            return decimalFormat.format(kd);
        }
        return "0";
    }

    private String lifeHeart(long round) {
        if (round <= 5) return "&c❤";
        if (round <= 10) return "&e❤";
        if (round <= 15) return "&6❤";
        if (round <= 20) return "&a❤";
        return "❤";
    }

    @Override
    public String setPlaceHolders(Player p, String s) {
        String placeholders = s.replaceAll("%playername%", p.getName())
                .replaceAll("%loc%", this.cords(p))
                .replaceAll("%life%", Math.round(p.getHealth()) + "")
                .replaceAll("%lifeheart%", this.lifeHeart(Math.round(p.getHealth())))
                .replaceAll("%time%", this.time())
                .replaceAll("%day%", this.day())
                .replaceAll("%serverip%", this.serverIP())
                .replaceAll("%version%", this.getVersion())
                .replaceAll("%versionshort%", this.getVersion())
                .replaceAll("%ping%", this.getPing(p) + " ms")
                .replaceAll("%ram%", this.ram())
                .replaceAll("%jumps%", "" + this.stats(p, Statistic.JUMP))
                .replaceAll("%mobkills%", "" + this.stats(p, Statistic.MOB_KILLS))
                .replaceAll("%world%", this.getWorldName(p)).replaceAll("%port%", String.valueOf(this.port()))
                .replaceAll("%maxplayers%", String.valueOf(this.maxPlayers()))
                .replaceAll("%online%", String.valueOf(this.onlinePlayers()))
                .replaceAll("%prefix%", this.prefix(p))
                .replaceAll("%suffix%", this.sufix(p)).replaceAll("%yaw%", String.valueOf(p.getLocation().getYaw()))
                .replaceAll("%kills%", String.valueOf(this.stats(p, Statistic.PLAYER_KILLS)))
                .replaceAll("%deaths%", String.valueOf(this.stats(p, Statistic.DEATHS)))
                .replaceAll("%kd%", this.getKD(p))
                .replaceAll("%pitch%", String.valueOf(p.getLocation().getPitch()))
                .replaceAll("%playerfood%", String.valueOf(p.getFoodLevel()))
                .replaceAll("%group%", this.getGroup(p))
                .replaceAll("%money%", Text.formatMoney(this.money(p)))
                .replaceAll("%moneylong%", Text.formatMoneyLong(this.money(p)))
                .replaceAll("%displayname%", p.getDisplayName())
                .replaceAll("%xp%", p.getTotalExperience() + "")
                .replaceAll("%x%", p.getLocation().getBlockX() + "")
                .replaceAll("%y%", p.getLocation().getBlockY() + "")
                .replaceAll("%z%", p.getLocation().getBlockZ() + "")
                .replaceAll("%rainbow%", RealScoreboard.getInstance().getAnimationManager().getLoopAnimation("rainbow"))
                .replaceAll("%playtime%", Text.formatTime(this.stats(p, Statistic.PLAY_ONE_MINUTE) / 20));
        return Text.color(this.placeholderAPI(p, placeholders));
    }


    private String placeholderAPI(Player p, String placeholders) {
        return RealScoreboardPlugin.getInstance().isPlaceholderAPI() ? PlaceholderAPI.setPlaceholders(p, placeholders) : placeholders;
    }

    private String getNmsVersion() {
        if (this.nmsVersion == null)
            this.nmsVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        return this.nmsVersion;
    }
}