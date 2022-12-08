package metro;

import java.util.*;

public class CommandHandler {
    Scanner scanner;
    Metro metro;
    static final String INVALID_COMMAND = "Invalid command.";
    static final String INVALID_PARAMETERS_COUNT = "Invalid parameters count %d instead of %d.%n";
    static final String NO_LINE = "No such line %s in metro.%n";
    static final String NO_STATION = "No such station %s at line %s.%n";
    
    CommandHandler(Metro metro) {
        this.metro = metro;
        scanner = new Scanner(System.in);
    }
    
    public void readCommand() {
        List<String> parameters;
        parameters = parseInput(scanner.nextLine());
        Commands actualCommand = parseCommand(parameters.remove(0));
        switch (actualCommand) {
            case APPEND -> append("tail", parameters);
            case ADD_HEAD -> append("head", parameters);
            case REMOVE -> remove(parameters);
            case OUTPUT -> output(parameters);
            case CONNECT -> connect(parameters);
            case ROUTE, ROUTE_FAST -> route(parameters, actualCommand);
            case EXIT -> System.exit(1);
            default -> System.out.println(INVALID_COMMAND);
        }
        readCommand();
    }

    private void route(List<String> parameters, Commands command) {
        if (isParametersCountOK(parameters, 4)) {
            String lineName1 = parameters.get(0);
            String lineName2 = parameters.get(2);
            if (isLinePresent(lineName1) && isLinePresent(lineName2)) {
                Line line1 = metro.getLine(lineName1);
                Line line2 = metro.getLine(lineName2);
                String stationName1 = parameters.get(1);
                String stationName2 = parameters.get(3);
                if (isStationPresent(line1, stationName1) && isStationPresent(line2, stationName2)) {
                    Station station1 = line1.getStation(stationName1);
                    Station station2 = line2.getStation(stationName2);
                    Map<Station, Integer> distances = setMaxDistance();
                    if (command == Commands.ROUTE) {
                        printRoute(makeRoute(station1, station2));
                    }
                    if (command == Commands.ROUTE_FAST) {
                        List<Station> routes = makeFastRoute(station1, station2, distances);
                        int distance = distances.get(station2);
                        printRoute(routes, distance);
                    }
                }
            }
        }
    }

    private void printRoute(List<Station> route, int distance) {
        Station previous = route.get(0);
        for (Station station : route) {
            if (!previous.getTransfers().isEmpty()) {
                for (Station.Transfer t : previous.getTransfers()) {
                    if (t.stationName.equals(station.getName())) {
                        System.out.println("Transition to line " + t.lineName);
                    }
                }
            }
            System.out.println(station.getName());
            previous = station;
        }
        if (distance != 0) {
            System.out.println(distance);
        }
    }

    private void printRoute(List<Station> route) {
        printRoute(route, 0);
    }

    private List<Station> makeRoute(Station station1, Station station2) {
        Set<Station> visited = new HashSet<>();
        visited.add(null);
        Deque<LinkedList<Station>> routes = new ArrayDeque<>();
        LinkedList<Station> startRoute = new LinkedList<>();
        startRoute.add(station1);
        routes.addLast(startRoute);
        while (!routes.isEmpty()) {
            LinkedList<Station> currentRoute = routes.removeFirst();
            if (currentRoute.getLast() == station2) {
                return currentRoute;
            }
            List<Station> neighbours = getNeighbours(currentRoute.getLast());
            for (Station neighbor : neighbours) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    LinkedList<Station> newRoute = new LinkedList<>(currentRoute);
                    newRoute.add(neighbor);
                    routes.addLast(newRoute);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<Station> makeFastRoute(Station station1, Station station2, Map<Station, Integer> distances) {
        Map<Station, List<Station>> routes = new HashMap<>();
        distances.put(station1, 0);
        Set<Station> visited = new HashSet<>();
        visited.add(null);
        Set<Station> toVisit = new HashSet<>();
        toVisit.add(station1);
        while (!toVisit.isEmpty()) {
            Station current = getNearestStation(toVisit, distances);
            toVisit.remove(current);
            for (Map.Entry<Station, Integer> neighbor : getNeighborsWeighted(current).entrySet()) {
                if (!visited.contains(neighbor.getKey())) {
                    checkRoute(neighbor.getKey(), neighbor.getValue(), current, distances, routes);
                    toVisit.add(neighbor.getKey());
                }
            }
            visited.add(current);
        }
        List<Station> route = routes.get(station2);
        route.add(station2);
        return route;
    }

    private void checkRoute(Station next, int travelTime, Station current, Map<Station, Integer> distances, Map<Station, List<Station>> routes) {
        int currentDistance = distances.get(current);
        int nextDistance = distances.get(next);
        if (currentDistance + travelTime < nextDistance) {
            distances.put(next, currentDistance + travelTime);
            List<Station> newRoute = new ArrayList<>();
            if (routes.containsKey(current)) {
                newRoute.addAll(routes.get(current));
            }
            newRoute.add(current);
            routes.put(next, newRoute);
        }
    }

    private Station getNearestStation(Set<Station> toVisit, Map<Station, Integer> distances) {
        Station nearestStation = null;
        int minDistance = Integer.MAX_VALUE;
        for (Station station : toVisit) {
            int distance = distances.get(station);
            if (distance < minDistance) {
                minDistance = distance;
                nearestStation = station;
            }
        }
        return nearestStation;
    }

    private Map<Station, Integer> setMaxDistance() {
        Map<Station, Integer> distances = new HashMap<>();
        for (Line line : metro.getLines()) {
            Station current = line.getHead();
            while (current != null) {
                distances.put(current, Integer.MAX_VALUE);
                current = current.getNext();
            }
        }
        return distances;
    }

    private List<Station> getNeighbours (Station station) {
        List<Station> neighbors = new ArrayList<>();
        if (!station.getTransfers().isEmpty()) {
            for (Station.Transfer transfer : station.getTransfers()) {
                neighbors.add(metro.getLine(transfer.lineName).getStation(transfer.stationName));
            }
        }
        neighbors.add(station.getNext());
        neighbors.add(station.getPrevious());
        return neighbors;
    }

    private Map<Station, Integer> getNeighborsWeighted (Station station) {
        if (station == null) {
            return new HashMap<>();
        }
        Map<Station, Integer> neighbors = new HashMap<>();
        if (!station.getTransfers().isEmpty()) {
            for (Station.Transfer transfer : station.getTransfers()) {
                neighbors.put(metro.getLine(transfer.lineName).getStation(transfer.stationName), 5);
            }
        }
        if (station.getNext() != null) {
            neighbors.put(station.getNext(), station.getDistanceToNext());
        }
        if (station.getPrevious() != null) {
            neighbors.put(station.getPrevious(), station.getPrevious().getDistanceToNext());
        }
        return neighbors;
    }

    private void connect(List<String> parameters) {
        if (isParametersCountOK(parameters, 4)) {
            String lineName1 = parameters.get(0);
            String lineName2 = parameters.get(2);
            if (isLinePresent(lineName1) && isLinePresent(lineName2)) {
                Line line1 = metro.getLine(lineName1);
                Line line2 = metro.getLine(lineName2);
                String stationName1 = parameters.get(1);
                String stationName2 = parameters.get(3);
                if (isStationPresent(line1, stationName1) && isStationPresent(line2, stationName2)) {
                    line1.getStation(stationName1).setTransfer(lineName2, stationName2);
                    line2.getStation(stationName2).setTransfer(lineName1, stationName1);
                }
            }
        }
    }

    private void output(List<String> parameters) {
        if (isParametersCountOK(parameters, 1)) {
            String lineName = parameters.get(0);
            if (isLinePresent(lineName)) {
                System.out.println(metro.getLine(lineName));
            }
        }
    }

    private void append(String side, List<String> parameters) {
        if (isParametersCountOK(parameters, 2) || isParametersCountOK(parameters, 3)) {
            String lineName = parameters.get(0);
            if (isLinePresent(lineName)) {
                Line line = metro.getLine(lineName);
                String stationName = parameters.get(1);
                int distanceToNext;
                if (isParametersCountOK(parameters, 3)) {
                    distanceToNext = Integer.parseInt(parameters.get(2));
                } else {
                    distanceToNext = 0;
                }
                if (side.equals("tail")) {
                    line.addTail(stationName, distanceToNext);
                } else {
                    line.addHead(stationName, distanceToNext);
                }
            }
        }
    }

    private void remove(List<String> parameters) {
        if (isParametersCountOK(parameters, 2)) {
            String lineName = parameters.get(0);
            if (isLinePresent(lineName)) {
                Line line = metro.getLine(lineName);
                String stationName = parameters.get(1);
                if (isStationPresent(line, stationName)) {
                    Station toRemove = line.getStation(stationName);
                    if (!toRemove.getTransfers().isEmpty()) {
                        for (Station.Transfer transfer : toRemove.getTransfers()) {
                            Line transferLine = metro.getLine(transfer.lineName);
                            Station transferStation = transferLine.getStation(transfer.stationName);
                            transferStation.removeTransfer(stationName);
                        }
                    }
                    line.remove(stationName);
                }
            }

        }
    }

    private boolean isParametersCountOK(List<String> parameters, int count) {
        if (parameters.size() != count) {
            System.out.println(INVALID_PARAMETERS_COUNT);
            return false;
        }
        return true;
    }

    private boolean isLinePresent(String lineName) {
         if (metro.getLine(lineName) == null) {
             System.out.printf(NO_LINE, lineName);
             return false;
         }
         return true;
    }

    private boolean isStationPresent(Line line, String stationName) {
        Station station = line.getStation(stationName);
        if (station == null) {
            System.out.printf(NO_STATION, stationName, line.getName());
            return false;
        }
        return true;
    }

    private Commands parseCommand(String input) {
        for (Commands command : Commands.values()) {
            if (command.getInputString().equals(input)){
                return command;
            }
        }
        return Commands.UNKNOWN;
    }

    private List<String> parseInput(String line) {
        List<String> list = new ArrayList<>();
        try(Scanner inputScanner = new Scanner(line)) {
            while (inputScanner.hasNext()) {
                String token = inputScanner.next();
                if (token.matches("\"\\w+")) {
                    String appendix;
                    do {
                        appendix = inputScanner.next();
                        token = new StringBuilder().append(token).append(" ").append(appendix).toString();
                    } while (!appendix.matches("\\w+\""));
                }
                list.add(token.replace("\"", ""));
            }
        }
        return list;
    }
}
