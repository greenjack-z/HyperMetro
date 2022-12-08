package metro;

import java.util.ArrayList;
import java.util.List;

public class Metro {
    private final List<Line> lines;

    public Metro() {
        this.lines = new ArrayList<>();
    }

    public List<Line> getLines() {
        return lines;
    }

    public Line getLine(String lineName) {
        for (Line line : lines) {
            if (line.getName().equals(lineName)) {
                return line;
            }
        }
        return null;
    }

    public void addLine(Line line) {
        if (!lines.contains(line)) {
            lines.add(line);
        }
    }

}
