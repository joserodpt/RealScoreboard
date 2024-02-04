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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealScoreboard
 */

import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.config.RSBScoreboards;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class RScoreboardBoards extends RScoreboard {

    protected List<RBoard> boards = new ArrayList<>();
    protected final int boardLoopDelay;
    protected int boardIndex;
    private BukkitTask boardsLooperTask;

    //loading from normal config scoreboards.yml
    public RScoreboardBoards(final String name, final String displayName, final String permission, final String defaultWord,
                             final int titleRefresh, final int titleLoopDelay, final int globalScoreboardRefresh, final int boardLoopDelay) {
        super(name, displayName, permission, defaultWord, titleRefresh, titleLoopDelay, globalScoreboardRefresh);
        this.boardLoopDelay = boardLoopDelay;
        for (String boardNames : RSBScoreboards.file().getSection("Scoreboards." + name + "." + "Boards").getRoutesAsStrings(false)) {
            boards.add(new RBoard(this, RSBScoreboards.file().getStringList("Scoreboards." + name + "." + "Boards." + boardNames + ".Title"), RSBScoreboards.file().getStringList("Scoreboards." + name + "." + "Boards." + boardNames + ".Lines")));
        }
    }

    public RScoreboardBoards(final String name, final String displayName, final String permission, final String defaultWord,
                             final int titleRefresh, final int titleLoopDelay, final int globalScoreboardRefresh,  final int boardLoopDelay, final List<RBoard> boards) {
        super(name, displayName, permission, defaultWord, titleRefresh, titleLoopDelay, globalScoreboardRefresh);
        this.boardLoopDelay = boardLoopDelay;
        this.boards = boards;
    }

    //loading from old config format
    public RScoreboardBoards(final String name, final String permission, final String defaultWord,
                             final int titleRefresh, final int titleLoopDelay, final int globalScoreboardRefresh, final int boardLoopDelay) {
        super(name,"&7" + name, permission, defaultWord, titleRefresh, titleLoopDelay, globalScoreboardRefresh);
        this.boardLoopDelay = boardLoopDelay;
        //save in new format below in setBoards
    }

    @Override
    public void stopTasks() {
        //cancel board tasks
        this.boards.forEach(RBoard::stopTasks);

        if (this.boardsLooperTask != null) {
            this.boardsLooperTask.cancel();
        }
    }

    @Override
    public RSBType getType() {
        return RSBType.MULTIPLE_BOARDS;
    }

    @Override
    public void init() {
        //init board tasks
        this.boards.forEach(RBoard::init);

        boardIndex = 0;
        if (boards.size() > 1) {
            this.boardsLooperTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (boardIndex == boards.size() - 1) {
                        boardIndex = 0;
                    } else {
                        ++boardIndex;
                    }
                }
            }.runTaskTimerAsynchronously(RealScoreboardAPI.getInstance().getPlugin(), 0L, boardLoopDelay);
        }
    }

    public RBoard getCurrentBoard() {
        return this.boards.get(boardIndex);
    }

    @Override
    public String getTitle() {
        return this.getCurrentBoard().getTitle();
    }

    @Override
    public List<String> getLines() {
        return this.getCurrentBoard().getLines();
    }

    @Override
    public void saveScoreboard() {
        super.saveCommonData();
        RSBScoreboards.file().set(super.getConfigKey() + "Refresh.Board-Loop-Delay", this.boardLoopDelay);

        int ctr = 1;
        for (RBoard board : this.boards) {
            RSBScoreboards.file().set(super.getConfigKey() + "Boards.board" + ctr + ".Title", board.getTitleList());
            RSBScoreboards.file().set(super.getConfigKey() + "Boards.board" + ctr + ".Lines", board.getLines());
            ++ctr;
        }

        RSBScoreboards.save();
    }

    public void setBoards(List<RBoard> boards) {
        this.boards = boards;
        //save in new format
        this.saveScoreboard();
    }
}