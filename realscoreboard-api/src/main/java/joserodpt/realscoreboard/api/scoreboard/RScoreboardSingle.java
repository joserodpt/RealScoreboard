package joserodpt.realscoreboard.api.scoreboard;

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

import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.config.RSBScoreboards;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RScoreboardSingle extends RScoreboard {

    private final RBoard board;

    public RScoreboardSingle(final String name, final String permission, final String defaultWord, final List<String> otherWorlds, final List<String> title, final List<String> lines,
                             final int titleRefresh, final int titleLoopDelay, final int globalScoreboardRefresh, final boolean defaultSB, boolean save) {
        super(name, "&7" + name, permission, defaultWord, otherWorlds, titleRefresh, titleLoopDelay, globalScoreboardRefresh, defaultSB);
        this.board = new RBoard(this, title, lines);

        //save in new format
        if (save) this.saveScoreboard();
    }

    //new data version
    public RScoreboardSingle(final String name, final String displayName, final String permission, final String defaultWord, final List<String> otherWorlds, final List<String> title, final List<String> lines,
                             final int titleRefresh, final int titleLoopDelay, final int globalScoreboardRefresh, final boolean defaultSB) {
        super(name, displayName, permission, defaultWord, otherWorlds, titleRefresh, titleLoopDelay, globalScoreboardRefresh, defaultSB);
        this.board = new RBoard(this, title, lines);
    }

    //for announcements
    public RScoreboardSingle(final String message) {
        super("", "", "", "", Collections.emptyList(), 40, 20, 20, false);
        this.board = new RBoard(this, Collections.singletonList(RSBConfig.file().getString("Config.Messages.Announce-Title")),
                Arrays.stream(message.split("(?<=\\G.{30})")).collect(Collectors.toList()));
    }

    @Override
    public void stopTasks() {
        this.board.stopTasks();
    }

    @Override
    public void init() {
        this.board.init();
    }

    @Override
    public String getTitle() {
        return this.board.getTitle();
    }

    @Override
    public List<String> getLines() {
        return this.board.getLines();
    }

    @Override
    public void saveScoreboard() {
        super.saveCommonData();
        RSBScoreboards.file().set(super.getConfigKey() + "Title", this.board.getTitleList());
        RSBScoreboards.file().set(super.getConfigKey() + "Lines", this.getLines());

        RSBScoreboards.save();
    }
}