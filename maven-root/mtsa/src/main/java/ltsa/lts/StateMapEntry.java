package ltsa.lts;

public abstract class StateMapEntry {
    byte[] key;
    int stateNumber;
    boolean marked;
    int depth;
}
