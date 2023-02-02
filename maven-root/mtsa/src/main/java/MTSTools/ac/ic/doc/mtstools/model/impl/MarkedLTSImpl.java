package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.HashSet;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.MarkedLTS;

public class MarkedLTSImpl<State, Action> extends LTSImpl<State, Action> implements MarkedLTS<State, Action> {

	private Set<State> markedStates;
	
	public MarkedLTSImpl(State initialState) {
		super(initialState);
		markedStates = new HashSet<>();
	}
	
	public Set<State> getMarkedStates() {
		return markedStates;
	}
	
	public boolean mark(State state) {
		return markedStates.add(state);
	}
	
	public boolean unmark(State state) {
		return markedStates.remove(state);
	}
	
	public boolean isMarked(State state) {
		return markedStates.contains(state);
	}
	
}
