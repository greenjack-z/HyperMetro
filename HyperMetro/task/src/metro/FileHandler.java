package metro;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHandler {
    private final Path path;
    private Map<String, Map<Integer, StationAdapter>> stations;

    FileHandler(String filename) {
        this.path = Path.of(filename);
        this.stations = new HashMap<>();
    }

    static class StationAdapter {
        String name;
        List<Station.Transfer> transfer;
        int time;
    }

    public void readFile() {
        Gson gson = new Gson();
        TypeToken<Map<String, Map<Integer, StationAdapter>>> typeToken = new TypeToken<>(){};
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            this.stations = gson.fromJson(bufferedReader, typeToken);
        } catch (IOException e) {
            System.out.println("Error! Such a file doesn't exist!");
        }

    }

    public List<Line> getStations() {
        List<Line> lines = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, StationAdapter>> entry : this.stations.entrySet()) {
            Line line = new Line(entry.getKey());
            for (StationAdapter stationAdapter : entry.getValue().values()) {
                line.addTail(stationAdapter.name, stationAdapter.time);
                if (!stationAdapter.transfer.isEmpty()) {
                    stationAdapter.transfer.forEach(line.getTail()::setTransfer);
                }
            }
            lines.add(line);
        }
        return lines;
    }
}
