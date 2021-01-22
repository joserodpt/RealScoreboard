package josegamerpt.realscoreboard.utils;

import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Text {
	static final List<String> lista = Arrays.asList("&c", "&6", "&e", "&a", "&b", "&9", "&3", "&d");
	static final Random random = new Random();

	public static String color(final String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static List<String> color(final List<String> messages) {
		return messages.parallelStream().map(message -> ChatColor.translateAlternateColorCodes('&', message)).collect(Collectors.toList());
	}

	static String[] money = {"", "k", "m", "b", "t", "q", "qi", "s", "sep", "OC", "N", "DEC", "UN", "DUO", "TRE"};
	static final String[] time = {"s", "m", "h", "h", "h", "h", "h", "h", "h"};

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
		return color(lista.get(random.nextInt(lista.size() - 1)));
	}
}
