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
 * @author José Rodrigues © 2016-2026
 * @link https://github.com/joserodpt/RealScoreboard
 */

import joserodpt.realscoreboard.api.RealScoreboardAPI;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * The number and colour formats RealScoreboard's placeholders use. Colouring and sending text
 * goes through RealUtils' {@link joserodpt.realutils.text.Text}.
 */
public class Format {

    static final String[] time = {"s", "m", "h", "h", "h", "h", "h", "h", "h"};
    static String[] money = {"", "k", "m", "b", "t", "q", "qi", "s", "sep", "OC", "N", "DEC", "UN", "DUO", "TRE"};

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

    public static String randomColor() {
        return ChatColor.translateAlternateColorCodes('&', RealScoreboardAPI.getInstance().getAnimationManagerAPI().getLoopAnimation("rainbow") + "&6");
    }

    public static String formatMoneyLong(double money) {
        NumberFormat nf = DecimalFormat.getInstance(Locale.ENGLISH);
        DecimalFormat decimalFormatter = (DecimalFormat) nf;
        decimalFormatter.applyPattern("#,###,###.##");
        return decimalFormatter.format(money);
    }
}
