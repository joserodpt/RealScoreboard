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

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

/**
 * Asks a player to type something, through RealUtils' prompt: a dialog's text box on servers that
 * have them, the chat everywhere else. Kept here, with its own constructor and
 * {@link InputRunnable}, so every screen that asks for input is unchanged.
 */
public class PlayerInput {

    public PlayerInput(final Player p, final InputRunnable correct, final InputRunnable cancel) {
        new joserodpt.realutils.input.PlayerInput(p, true, correct::run, cancel::run);
    }

    /** Where the prompt's words come from. Called once the plugin is enabled. */
    public static void setup(final Plugin plugin) {
        joserodpt.realutils.input.PlayerInput.setup(plugin,
                p -> Arrays.asList("&l&9Type in chat your input", "&fType &4cancel &fto cancel"),
                p -> Arrays.asList("&dInput", "&fType your input below."),
                p -> Text.send(p, "&cInput cancelled."),
                p -> Text.send(p, "&cAn error occurred."));
    }

    public static Listener getListener() {
        return joserodpt.realutils.input.PlayerInput.getListener();
    }

    @FunctionalInterface
    public interface InputRunnable {
        void run(String input);
    }
}
