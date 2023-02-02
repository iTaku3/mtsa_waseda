package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking;

/** This enum contains the possible status for a state. */
enum Status {
    ERROR(-1, "ERROR"),
    NONE(0, "NONE"),
    GOAL(1, "GOAL");
    private final int precedence;
    private final String name;
    Status(int p, String n) {
        precedence = p;
        name = n;
    }
    @Override
    public String toString() {
        return name;
    }
}