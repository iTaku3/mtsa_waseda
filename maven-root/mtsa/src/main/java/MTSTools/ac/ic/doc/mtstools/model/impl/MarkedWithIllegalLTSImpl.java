package MTSTools.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.mtstools.model.MarkedLTS;
import MTSTools.ac.ic.doc.mtstools.model.MarkedWithIllegalLTS;

import java.util.HashSet;
import java.util.Set;

public class MarkedWithIllegalLTSImpl<State, Action> extends MarkedLTSImpl<State, Action> implements MarkedWithIllegalLTS<State, Action> {

    private Set<State> illegalStates;

    public MarkedWithIllegalLTSImpl(State initialState) {
        super(initialState);
        illegalStates = new HashSet<>();
    }

    public Set<State> getIllegalStates() {
        return illegalStates;
    }

    public boolean makeIllegal(State state) {
        return illegalStates.add(state);
    }

    public boolean notIllegal(State state) {
        return illegalStates.remove(state);
    }

    public boolean isIllegal(State state) {
        return illegalStates.contains(state);
    }
}
