package MTSTools.ac.ic.doc.mtstools.model;

import java.util.Set;

public interface MarkedLTS<State, Action> extends LTS<State, Action> {

	public Set<State> getMarkedStates();
	public boolean mark(State state);
	public boolean unmark(State state);
	public boolean isMarked(State state);
	
}
