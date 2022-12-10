package josegamerpt.realscoreboard.api.scoreboard;

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