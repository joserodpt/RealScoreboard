package josegamerpt.realscoreboard.api.scoreboard;

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

import java.util.List;

public class RBoard {

    private final String worldBoard;
    private final List<String> title;
    private final List<String> lines;

    public RBoard(String worldBoard, List<String> title, List<String> lines) {
        this.worldBoard = worldBoard;
        this.title = title;
        this.lines = lines;
    }

    /**
     * Gets scoreboard world name
     *
     * @return value of world name
     */
    public String getWorldBoard() {
        return this.worldBoard;
    }

    /**
     * Gets list of scoreboard titles
     *
     * @return list of scoreboard titles
     */
    public List<String> getTitle() {
        return this.title;
    }

    /**
     * Gets list of scoreboard lines
     *
     * @return list of scoreboard lines
     */
    public List<String> getLines() {
        return this.lines;
    }
}