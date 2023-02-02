package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking;

/**
 * This enum contains the possible status for a state.
 */
public enum Status {
    ERROR(-1, "ERROR"),
    NONE(0, "NONE"),
    GOAL(1, "GOAL");
    private final String name;

    Status(int p, String n) {
        name = n;
    }

    @Override
    public String toString() {
        return name;
    }
}
