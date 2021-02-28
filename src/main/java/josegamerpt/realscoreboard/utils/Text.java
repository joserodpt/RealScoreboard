package josegamerpt.realscoreboard.utils;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import josegamerpt.realscoreboard.config.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Text {
    static final List<String> lista = Arrays.asList("&c", "&6", "&e", "&a", "&b", "&9", "&3", "&d");
    static final Random random = new Random();
    static final String[] time = {"s", "m", "h", "h", "h", "h", "h", "h", "h"};
    static String[] money = {"", "k", "m", "b", "t", "q", "qi", "s", "sep", "OC", "N", "DEC", "UN", "DUO", "TRE"};
    private static int i = 1;
    private static String texto = "";

    public static String color(final String message) {
        return IridiumColorAPI.process(message);
    }

    public static void startAnimation() {
        int s = lista.size();
        try {
            if (i >= s)
                i = 0;
            texto = lista.get(i);
            i++;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        return color(lista.get(random.nextInt(lista.size() - 1)));
    }

    public static String getRainbow() {
        return texto;
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
