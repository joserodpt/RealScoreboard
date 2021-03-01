package josegamerpt.realscoreboard.classes;

import com.google.common.base.Strings;
import josegamerpt.realscoreboard.utils.Text;

import java.util.List;

public class TextLooper {

    private List<String> list;
    private String id;
    private String get;

    private int i = 0;

    public TextLooper(String id, List<String> s) {
        this.id = id; this.list = s;
    }

    public void next() {
        if (this.i >= this.list.size()) {
            this.i = 0;
        }
        this.get = Text.color(list.get(i));
        this.i++;
    }

    public String get() {
        return Strings.isNullOrEmpty(this.get) ? id + " err" : this.get;
    }

}
