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

/**
 * The tab completion sources RealScoreboard commands share. Each one is wired to a provider in
 * {@link RSBCommandManager}, and pulled onto a parameter with {@link SuggestFrom}.
 */
public enum RSBSuggestion {
    /** Every loaded scoreboard, by the name it is configured under. */
    SCOREBOARDS
}
