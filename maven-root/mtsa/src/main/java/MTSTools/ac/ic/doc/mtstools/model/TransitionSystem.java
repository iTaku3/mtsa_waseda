package MTSTools.ac.ic.doc.mtstools.model;

import java.util.Collection;
import java.util.Set;

public interface TransitionSystem<State, Action> {
	
	public Set<State> getStates();
	public Set<Action> getActions();
	public State getInitialState();
	public boolean addState(State state);
	public boolean addStates(Collection<? extends State> states);
	public boolean addAction(Action action);
	public boolean addActions(Collection<? extends Action> actions);
	public void removeAction(Action action);
	
	public boolean removeUnreachableStates();
	
	public void setInitialState(State state);
}
