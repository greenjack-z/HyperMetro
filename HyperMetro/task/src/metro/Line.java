package metro;

public class Line {
    private final String name;

    private Station head;

    private Station tail;

    public Line(String name) {
        this.name = name;
        this.head = null;
        this.tail = null;
    }

    public String getName() {
        return name;
    }

    public Station getHead() {
        return head;
    }

    public Station getTail() {
        return tail;
    }

    public void addHead(String name, int distanceToNext) {
        Station oldHead = head;
        Station newStation = new Station(name, null, oldHead, distanceToNext);
        head = newStation;
        if (oldHead == null) {
            tail = newStation;
        } else {
            oldHead.setPrevious(newStation);
        }
    }

    public void addTail(String name, int distanceToNext) {
        Station oldTail = tail;
        Station newStation = new Station(name, tail, null, distanceToNext);
        tail = newStation;
        if (oldTail == null) {
            head = newStation;
        } else {
            oldTail.setNext(newStation);
        }
    }

    public void remove(String name) {
        Station stationToRemove = getStation(name);
        stationToRemove.getPrevious().setNext(stationToRemove.getNext());
        stationToRemove.getNext().setPrevious(stationToRemove.getPrevious());
    }

    public Station getStation(String name) {
        Station current = head;
        while (current != null) {
            if (current.getName().equals(name)) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("depot").append("\n");
        Station current = head;
        while (current != null) {
            stringBuilder.append(current).append("\n");
            current = current.getNext();
        }
        stringBuilder.append("depot").append("\n");
        return stringBuilder.toString();
    }
}