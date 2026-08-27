package joserodpt.realscoreboard.api.external;

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

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A scoreboard handed to RealScoreboard by another plugin, rendered in place of
 * the player's normal scoreboard until it is cleared.
 * <p>
 * Immutable, so it can be built on the main thread and read from
 * RealScoreboard's asynchronous refresh task.
 *
 * @see joserodpt.realscoreboard.api.managers.ExternalScoreboardManagerAPI
 */
public final class ExternalBoard {

    private final String title;
    private final List<String> lines;
    private final boolean forced;

    /**
     * @param title  the title to display; colour codes and placeholders are allowed
     * @param lines  the lines to display, top to bottom
     * @param forced whether to show this board even when the player has their
     *               scoreboard turned off or is standing in a world listed under
     *               {@code Config.Disabled-Worlds}
     */
    public ExternalBoard(String title, List<String> lines, boolean forced) {
        Preconditions.checkNotNull(title, "title");
        Preconditions.checkNotNull(lines, "lines");

        this.title = title;
        this.lines = Collections.unmodifiableList(new ArrayList<>(lines));
        this.forced = forced;
    }

    public ExternalBoard(String title, List<String> lines) {
        this(title, lines, false);
    }

    public String getTitle() {
        return this.title;
    }

    public List<String> getLines() {
        return this.lines;
    }

    /**
     * Whether this board outranks the player's own preference.
     * <p>
     * Intended for boards carrying information the player needs, such as a
     * minigame in progress. The saved preference is never modified, so it
     * applies again as soon as the board is cleared.
     */
    public boolean isForced() {
        return this.forced;
    }
}
