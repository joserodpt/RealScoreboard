package joserodpt.realscoreboard.api.managers;

/*
 *   ____            _ ____                     _                         _
 *  |  _ \ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |
 *  | |_) / _ \/ _` | \___ \ / __/ _ \| '__/ _ \ '_ \ / _ \ / _` | '__/ _` |
 *  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |
 *  |_| \_\___|\__,_|_|____/ \___\___/|_|  \___|_.__/ \___/ \__,_|_|  \__,_|
 *
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2016-2025
 * @link https://github.com/joserodpt/RealScoreboard
 */

import joserodpt.realscoreboard.api.external.ExternalBoard;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;

/**
 * Lets another plugin put its own scoreboard on a player's screen, with
 * RealScoreboard doing the rendering so only one plugin ever owns the sidebar.
 * <p>
 * Example, from a task that already builds the lines:
 * <pre>
 * RealScoreboardAPI.getInstance().getExternalScoreboardManagerAPI()
 *         .setBoard(myPlugin, player, title, lines, true);
 * </pre>
 * The board stays up until it is cleared, so it only has to be pushed again
 * when its contents change. Clear it when the player no longer needs it, and
 * call {@link #clearAll(Plugin)} in {@code onDisable} - although boards left
 * behind by a disabled plugin are dropped automatically.
 */
public interface ExternalScoreboardManagerAPI {

    /**
     * Announces the plugin in the console and lets it push boards. Call it once,
     * from {@code onEnable}: every other method here refuses a plugin that has
     * not registered, so RealScoreboard always knows who a board came from.
     */
    void registerHook(Plugin owner);

    /**
     * Every plugin that registered, minus any that have since been disabled.
     */
    Collection<Plugin> getHooks();

    /**
     * Shows this board to the player, replacing whatever was set before.
     *
     * @param forced whether to show it even when the player has their scoreboard
     *               turned off or is in a disabled world
     * @throws IllegalStateException if the plugin never called
     *                               {@link #registerHook(Plugin)}
     */
    void setBoard(Plugin owner, Player player, String title, List<String> lines, boolean forced);

    /**
     * @throws IllegalStateException if the plugin never called
     *                               {@link #registerHook(Plugin)}
     */
    void setBoard(Plugin owner, Player player, ExternalBoard board);

    /**
     * Hands the player back to RealScoreboard. Does nothing if their board
     * belongs to another plugin.
     *
     * @throws IllegalStateException if the plugin never called
     *                               {@link #registerHook(Plugin)}
     */
    void clearBoard(Plugin owner, Player player);

    /**
     * Clears every board this plugin set. Safe to call from onDisable.
     *
     * @throws IllegalStateException if the plugin never called
     *                               {@link #registerHook(Plugin)}
     */
    void clearAll(Plugin owner);

    /**
     * The board currently set for this player, or {@code null} if none is.
     * Called from RealScoreboard's asynchronous refresh task.
     */
    ExternalBoard getBoard(Player player);

    /**
     * The plugin whose board this player is being shown, or {@code null}.
     */
    Plugin getOwner(Player player);
}
