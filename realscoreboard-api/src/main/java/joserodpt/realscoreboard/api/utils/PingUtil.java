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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PingUtil {

    private static final int pingVersion;

    static {
        String version = Bukkit.getServer().getBukkitVersion().split("-")[0];

        switch (version) {
            case "1.16.5":
            case "1.16.4":
            case "1.16.3":
            case "1.16.2":
            case "1.16.1":
            case "1.16":
            case "1.15.2":
            case "1.15.1":
            case "1.15":
            case "1.14.4":
            case "1.14.3":
            case "1.14.2":
            case "1.14.1":
            case "1.14":
            case "1.13.2":
            case "1.13.1":
            case "1.13":
                Bukkit.getLogger().info("[RealScoreboard] Using 1.13-1.16.5 ping method.");
                pingVersion = 0;
                break;
            default:
                Bukkit.getLogger().info("[RealScoreboard] Using 1.17+ ping method.");
                pingVersion = 1;
                break;
        }
    }
    
    public static int getPing(Player p) {
        switch (pingVersion) {
            case 1:
                return getNewPing(p);
            case 0:
                return getPingReflection(p);
            default:
                return -3;
        }
    }

    public static int getNewPing(Player p) {
        try {
            return p.getPing();
        } catch (Exception e) {
            return -2;
        }
    }

    // reflection ping

    private static String nmsVersion;
    private static Method getHandleMethod;
    private static Method getPingMethod;
    private static Field pingField;

    public static int getPingReflection(Player p) {
        try {
            if (getHandleMethod == null) {
                getHandleMethod = p.getClass().getDeclaredMethod("getHandle");
                getHandleMethod.setAccessible(true);
            }
            Object entityPlayer = getHandleMethod.invoke(p);
            if (pingField == null && getPingMethod == null) {
                try {
                    pingField = entityPlayer.getClass().getDeclaredField("ping");
                } catch (NoSuchFieldError | NoSuchFieldException e) {
                    getPingMethod = Class.forName("org.bukkit.craftbukkit." + getNmsVersion() + ".entity.CraftPlayer").getDeclaredMethod("getPing");
                }
                if (pingField != null)
                    pingField.setAccessible(true);
            }
            int ping;
            if (getPingMethod != null)
                ping = (int) getPingMethod.invoke(p);
            else
                ping = pingField.getInt(entityPlayer);
            return Math.max(ping, 0);
        } catch (Exception e) {
            return -1;
        }
    }

    private static String getNmsVersion() {
        if (nmsVersion == null)
            nmsVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        return nmsVersion;
    }
}

