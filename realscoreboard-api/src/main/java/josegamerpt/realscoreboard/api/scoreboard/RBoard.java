package josegamerpt.realscoreboard.api.scoreboard;

import java.util.List;

public class RBoard {

    private final String worldBoard;
    private final List<String> title;
    private final List<String> lines;

    public RBoard(String worldboard, List<String> title, List<String> lines)
    {
        this.worldBoard = worldboard;
        this.title = title;
        this.lines = lines;
    }

    public String getWorldBoard()
    {
        return this.worldBoard;
    }

    public List<String> getTitle() {
        return this.title;
    }

    public List<String> getLines() {
        return this.lines;
    }
}
