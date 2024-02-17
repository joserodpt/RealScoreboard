package joserodpt.realscoreboard.api.utils;

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

import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.config.RSBConfig;
import net.melion.rgbchat.api.RGBApi;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Text {

    static final String[] time = {"s", "m", "h", "h", "h", "h", "h", "h", "h"};
    static String[] money = {"", "k", "m", "b", "t", "q", "qi", "s", "sep", "OC", "N", "DEC", "UN", "DUO", "TRE"};

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&',
                RGBApi.INSTANCE.toColoredMessage(string));
    }

    public static List<String> color(final List<?> list) {
        final List<String> color = new ArrayList<>();
        list.forEach(o -> color.add(Text.color((String) o)));
        return color;
    }

    public static String formatMoney(double value) {
        int index = 0;
        while (value / 1000.0D >= 1.0D) {
            value /= 1000.0D;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), money[index]);
    }

    public static String formatTime(int value) {
        int index = 0;
        while (value / 1000 >= 1) {
            value /= 1000;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), time[index]);
    }

    public static String getPrefix() {
        return Text.color(RSBConfig.file().getString("Config.Prefix"));
    }

    public static String randomColor() {
        return ChatColor.translateAlternateColorCodes('&', RealScoreboardAPI.getInstance().getAnimationManager().getLoopAnimation("rainbow") + "&6");
    }

    public static void send(CommandSender commandSender, List<String> asList) {
        asList.forEach(s -> commandSender.sendMessage(color(s)));
    }

    public static void send(Player commandSender, String msg) {
        commandSender.sendMessage(getPrefix() + color(msg));
    }

    public static void send(CommandSender commandSender, String msg) {
        commandSender.sendMessage(getPrefix() + color(msg));
    }

    public static String formatMoneyLong(double money) {
        NumberFormat nf = DecimalFormat.getInstance(Locale.ENGLISH);
        DecimalFormat decimalFormatter = (DecimalFormat) nf;
        decimalFormatter.applyPattern("#,###,###.##");
        return decimalFormatter.format(money);
    }
}