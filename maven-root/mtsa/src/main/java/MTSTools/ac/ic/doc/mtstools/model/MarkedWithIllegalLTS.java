package MTSTools.ac.ic.doc.mtstools.model;

import java.util.Set;

public interface MarkedWithIllegalLTS<State, Action> extends MarkedLTS<State, Action> {
    public Set<State> getIllegalStates();
    public boolean makeIllegal(State state);
    public boolean notIllegal(State state);
    public boolean isIllegal(State state);
}
