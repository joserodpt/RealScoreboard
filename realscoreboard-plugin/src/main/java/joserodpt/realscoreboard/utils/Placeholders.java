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
 * @author José Rodrigues © 2016-2024
 * @link https://github.com/joserodpt/RealScoreboard
 */

import joserodpt.realscoreboard.RealScoreboard;
import joserodpt.realscoreboard.RealScoreboardPlugin;
import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.conditions.Condition;
import joserodpt.realscoreboard.api.utils.IPlaceholders;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.utils.PingUtil;
import joserodpt.realscoreboard.api.utils.Text;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholders implements IPlaceholders {

    private final RealScoreboardAPI rsa;

    public Placeholders(RealScoreboardAPI rsa) {
        this.rsa = rsa;
    }

    DecimalFormat decimalFormat = new DecimalFormat("#.##");

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
        DateFormat dateFormat = new SimpleDateFormat(RSBConfig.file().getString("Config.Hours.Formatting"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, RSBConfig.file().getInt("Config.Hours.Offset"));
        cal.getTime();
        return dateFormat.format(cal.getTime());
    }

    private String day() {
        DateFormat dateFormat = new SimpleDateFormat(RSBConfig.file().getString("Config.Days.Formatting"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, RSBConfig.file().getInt("Config.Days.Offset"));
        cal.getTime();
        return dateFormat.format(cal.getTime());
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
            if (w == null || w.isEmpty()) return "None";
            return w;
        } catch (UnsupportedOperationException e) {
            return "No Perm Plugin";
        }
        else return "Missing Vault";
    }

    private String prefix(Player p) {
        if (RealScoreboardPlugin.getInstance().getChat() != null) {
            String grupo = RealScoreboardPlugin.getInstance().getChat().getPrimaryGroup(p);
            if (grupo == null || grupo.isEmpty()) return "None";
            String prefix = RealScoreboardPlugin.getInstance().getChat().getGroupPrefix(p.getWorld(), grupo);
            if (prefix == null || prefix.isEmpty()) return "None";
            return Text.color(prefix);
        } else return "Missing Vault";
    }

    private String sufix(Player p) {
        if (RealScoreboardPlugin.getInstance().getChat() != null) {
            String grupo = RealScoreboardPlugin.getInstance().getChat().getPrimaryGroup(p);
            if (grupo == null || grupo.isEmpty()) return "None";
            String sufix = RealScoreboardPlugin.getInstance().getChat().getGroupSuffix(p.getWorld(), grupo);
            if (sufix == null || sufix.isEmpty()) return "None";
            return Text.color(sufix);
        } else return "Missing Vault";
    }

    private double money(Player p) {
        return RealScoreboardPlugin.getInstance().getEconomy() == null ? -1D : RealScoreboardPlugin.getInstance().getEconomy().getBalance(p);
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
    public String setPlaceholders(Player p, String s, boolean skipCond) {
        Pattern pattern = Pattern.compile("%(.*?)%");
        Matcher matcher = pattern.matcher(s);

        while (matcher.find()) {
            String placeholder = matcher.group(1);

            if (!skipCond && placeholder.startsWith("cond:")) {
                String condName = placeholder.substring(5);
                Condition condition = rsa.getConditionManager().getCondition(condName);
                if (condition == null) {
                    s = s.replace("%" + placeholder + "%", "&cInvalid condition '" + condName + "'");
                } else {
                    s = s.replace("%" + placeholder + "%", condition.evaluate(p));
                }
            } else {
                // Handle RealScoreboard native placeholders
                switch (placeholder.toLowerCase()) {
                    case "playername":
                        s = s.replace("%" + placeholder + "%", p.getName());
                        break;
                    case "loc":
                        s = s.replace("%" + placeholder + "%", this.cords(p));
                        break;
                    case "life":
                        s = s.replace("%" + placeholder + "%", Math.round(p.getHealth()) + "");
                        break;
                    case "lifeheart":
                        s = s.replace("%" + placeholder + "%", this.lifeHeart(Math.round(p.getHealth())));
                        break;
                    case "time":
                        s = s.replace("%" + placeholder + "%", this.time());
                        break;
                    case "day":
                        s = s.replace("%" + placeholder + "%", this.day());
                        break;
                    case "serverip":
                        s = s.replace("%" + placeholder + "%", this.serverIP());
                        break;
                    case "version":
                        s = s.replace("%" + placeholder + "%", this.getVersion());
                        break;
                    case "versionshort":
                        s = s.replace("%" + placeholder + "%", this.getVersion());
                        break;
                    case "ping":
                        s = s.replace("%" + placeholder + "%", PingUtil.getPing(p) + " ms");
                        break;
                    case "ram":
                        s = s.replace("%" + placeholder + "%", this.ram());
                        break;
                    case "jumps":
                        s = s.replace("%" + placeholder + "%", "" + this.stats(p, Statistic.JUMP));
                        break;
                    case "mobkills":
                        s = s.replace("%" + placeholder + "%", "" + this.stats(p, Statistic.MOB_KILLS));
                        break;
                    case "world":
                        s = s.replace("%" + placeholder + "%", this.getWorldName(p));
                        break;
                    case "port":
                        s = s.replace("%" + placeholder + "%", String.valueOf(this.port()));
                        break;
                    case "maxplayers":
                        s = s.replace("%" + placeholder + "%", String.valueOf(this.maxPlayers()));
                        break;
                    case "online":
                        s = s.replace("%" + placeholder + "%", String.valueOf(this.onlinePlayers()));
                        break;
                    case "prefix":
                        s = s.replace("%" + placeholder + "%", this.prefix(p));
                        break;
                    case "suffix":
                        s = s.replace("%" + placeholder + "%", this.sufix(p));
                        break;
                    case "yaw":
                        s = s.replace("%" + placeholder + "%", String.valueOf(p.getLocation().getYaw()));
                        break;
                    case "kills":
                        s = s.replace("%" + placeholder + "%", String.valueOf(this.stats(p, Statistic.PLAYER_KILLS)));
                        break;
                    case "deaths":
                        s = s.replace("%" + placeholder + "%", String.valueOf(this.stats(p, Statistic.DEATHS)));
                        break;
                    case "kd":
                        s = s.replace("%" + placeholder + "%", this.getKD(p));
                        break;
                    case "pitch":
                        s = s.replace("%" + placeholder + "%", String.valueOf(p.getLocation().getPitch()));
                        break;
                    case "playerfood":
                        s = s.replace("%" + placeholder + "%", String.valueOf(p.getFoodLevel()));
                        break;
                    case "group":
                        s = s.replace("%" + placeholder + "%", this.getGroup(p));
                        break;
                    case "money":
                        s = s.replace("%" + placeholder + "%", Text.formatMoney(this.money(p)));
                        break;
                    case "moneylong":
                        s = s.replace("%" + placeholder + "%", Text.formatMoneyLong(this.money(p)));
                        break;
                    case "displayname":
                        s = s.replace("%" + placeholder + "%", p.getDisplayName());
                        break;
                    case "xp":
                        s = s.replace("%" + placeholder + "%", p.getTotalExperience() + "");
                        break;
                    case "x":
                        s = s.replace("%" + placeholder + "%", p.getLocation().getBlockX() + "");
                        break;
                    case "y":
                        s = s.replace("%" + placeholder + "%", p.getLocation().getBlockY() + "");
                        break;
                    case "z":
                        s = s.replace("%" + placeholder + "%", p.getLocation().getBlockZ() + "");
                        break;
                    case "rainbow":
                        s = s.replace("%" + placeholder + "%", RealScoreboard.getInstance().getAnimationManagerAPI().getLoopAnimation("rainbow"));
                        break;
                    case "playtime":
                        s = s.replace("%" + placeholder + "%", Text.formatTime(this.stats(p, Statistic.PLAY_ONE_MINUTE) / 20));
                        break;
                    default:
                        s = s.replace("%" + placeholder + "%", this.placeholderAPI(p, "%" + placeholder + "%"));
                        break;
                }
            }
        }

        return Text.color(s);
    }

    private String placeholderAPI(Player p, String placeholders) {
        return RealScoreboardPlugin.getInstance().isPlaceholderAPI() ? PlaceholderAPI.setPlaceholders(p, placeholders) : placeholders;
    }
}