package metro;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Station {
    private final String name;
    private Station next;
    private Station previous;

    private final List<Transfer> transfers;

    private final int distanceToNext;

    static class Transfer {
        @SerializedName("line")
        String lineName;
        @SerializedName("station")
        String stationName;

        Transfer(String lineName, String stationName) {
            this.lineName = lineName;
            this.stationName = stationName;
        }

        @Override
        public String toString() {
            return stationName + "(" + lineName + ")";
        }
    }

    public Station(String name, Station previous, Station next, int distanceToNext) {
        this.name = name;
        this.distanceToNext = distanceToNext;
        this.previous = previous;
        this.next = next;
        transfers = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Station getNext() {
        return next;
    }

    public Station getPrevious() {
        return previous;
    }

    public List<Transfer> getTransfers() {
        return transfers;
    }

    public int getDistanceToNext() {
        return distanceToNext;
    }

    public void setNext(Station next) {
        this.next = next;
    }

    public void setPrevious(Station previous) {
        this.previous = previous;
    }


    public void setTransfer(String lineName, String stationName) {
        Transfer transfer = new Transfer(lineName, stationName);
        setTransfer(transfer);
    }
    public void setTransfer(Transfer transfer) {
        if (!transfers.contains(transfer)) {
            transfers.add(transfer);
        }
    }

    public void removeTransfer(String stationName) {
        transfers.removeIf(transfer -> transfer.stationName.equals(stationName));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name);
        if (!transfers.isEmpty()) {
            transfers.forEach(t -> stringBuilder.append(String.format(" - %s (%s)", t.stationName, t.lineName)));
        }
        return stringBuilder.toString();
    }
}
