package josegamerpt.realscoreboard.utils;

import josegamerpt.iridiumapi.IridiumAPI;
import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class Text {
    static final String[] time = {"s", "m", "h", "h", "h", "h", "h", "h", "h"};
    static String[] money = {"", "k", "m", "b", "t", "q", "qi", "s", "sep", "OC", "N", "DEC", "UN", "DUO", "TRE"};

    public static String color(final String message) {
        return IridiumAPI.process(message);
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
        return Text.color(Config.file().getString("Config.Prefix"));
    }

    public static String randomColor() {
        return ChatColor.translateAlternateColorCodes('&', RealScoreboard.getInstance().getAnimationManager().getLoopAnimation("rainbow") + "&6");
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
