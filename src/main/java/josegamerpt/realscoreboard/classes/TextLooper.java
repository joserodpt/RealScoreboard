package josegamerpt.realscoreboard.classes;

import com.google.common.base.Strings;
import josegamerpt.realscoreboard.RealScoreboard;

import java.util.List;
import java.util.logging.Level;

public class TextLooper {

    private List<String> list;
    private String id;
    private String get;

    private int i = 0;

    public TextLooper(String id, List<String> s) {
        this.id = id; this.list = s;
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
            RealScoreboard.getInstance().getLogger().log(Level.WARNING, "There is something wrong with this text loop: " + this.id);
        }
    }

    public String get() {
        return Strings.isNullOrEmpty(this.get) ? id + " err" : this.get;
    }

}
