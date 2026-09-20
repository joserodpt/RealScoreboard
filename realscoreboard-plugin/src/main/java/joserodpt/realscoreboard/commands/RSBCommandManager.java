package joserodpt.realscoreboard.commands;

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
import joserodpt.realscoreboard.api.scoreboard.RScoreboard;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builds the Lamp instance every RealScoreboard command hangs off, and hands it the shared tab
 * completions and the plugin's own error messages. Lamp registers the commands straight onto the
 * server's command map, which is why none of them appear in plugin.yml.
 */
public final class RSBCommandManager {

    private final Lamp<BukkitCommandActor> lamp;

    public RSBCommandManager(final JavaPlugin plugin, final RealScoreboardAPI rsa) {
        final Map<RSBSuggestion, SuggestionProvider<BukkitCommandActor>> suggestions = suggestions(rsa);

        //Brigadier stays on. Lamp's own matcher treats leftover input as merely a worse match, so
        //`/rsb reload junk` would quietly fall back to the bare `/rsb` handler; Brigadier's tree
        //refuses it outright. Where it can't attach (Spigot on 1.19.1+) Lamp falls back on its own
        //and RSBExceptionHandler's @Usage messages are what players see instead.
        this.lamp = BukkitLamp.builder(plugin)
                .exceptionHandler(new RSBExceptionHandler())
                .suggestionProviders(providers -> providers.addProviderForAnnotation(
                        SuggestFrom.class, annotation -> suggestions.get(annotation.value())))
                .build();

        this.lamp.register(new RealScoreboardCommand(rsa));
    }

    private static Map<RSBSuggestion, SuggestionProvider<BukkitCommandActor>> suggestions(final RealScoreboardAPI rsa) {
        final Map<RSBSuggestion, SuggestionProvider<BukkitCommandActor>> sources = new EnumMap<>(RSBSuggestion.class);

        sources.put(RSBSuggestion.SCOREBOARDS, context -> rsa.getScoreboardManagerAPI().getScoreboards().stream()
                .map(RScoreboard::getName)
                .collect(Collectors.toList()));

        return sources;
    }

    public Lamp<BukkitCommandActor> getLamp() {
        return this.lamp;
    }
}
