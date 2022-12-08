package metro;

public enum Commands {
    UNKNOWN ("null"),
    EXIT ("/exit"),
    ROUTE ("/route"),

    ROUTE_FAST ("/fastest-route"),
    CONNECT ("/connect"),
    OUTPUT ("/output"),
    APPEND ("/append"),
    ADD_HEAD ("/add-head"),
    REMOVE("/remove");

    private final String inputString;

    Commands(String input) {
        inputString = input;
    }

    public String getInputString() {
        return inputString;
    }
}
