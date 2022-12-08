package metro;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        String fileName;
        if (args.length > 0) {
            fileName = args[0];
        } else {
            fileName = "prague_w_time.json";
        }
        FileHandler fileHandler = new FileHandler(fileName);
        fileHandler.readFile();
        Metro metro = new Metro();
        fileHandler.getStations().forEach(metro::addLine);
        CommandHandler commandHandler = new CommandHandler(metro);
        commandHandler.readCommand();
    }
}
