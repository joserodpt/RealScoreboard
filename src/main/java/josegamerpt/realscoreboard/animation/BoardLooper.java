package josegamerpt.realscoreboard.animation;

import josegamerpt.realscoreboard.RealScoreboard;
import josegamerpt.realscoreboard.scoreboard.RBoard;

import java.util.List;
import java.util.logging.Level;

public class BoardLooper {

    private List<RBoard> list;
    private String id;
    private RBoard get;

    private int i = 0;

    public BoardLooper(String id, List<RBoard> s) {
        this.id = id;
       this.list = s;
    }

    public void next() {
        try {
            if (this.i >= this.list.size()) {
                this.i = 0;
            }
            this.get = list.get(i);
            this.i++;
        } catch (Exception e)
        {
            RealScoreboard.getInstance().getLogger().log(Level.WARNING, "There is something wrong with this board loop: " + this.id);
        }
    }

    public RBoard getBoard() {
        return this.get;
    }


}
