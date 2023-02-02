package MTSSynthesis.controller.model;

import java.util.Set;

import MTSSynthesis.controller.model.Goal;

public interface Game<State> {

	public abstract boolean isUncontrollable(State state);

	public abstract Set<State> getUncontrollableSuccessors(State state);

	public abstract Set<State> getControllableSuccessors(State state);
	
	public abstract Set<State> getSuccessors(State state);

	public abstract Set<State> getPredecessors(State state);

	public abstract Set<State> getStates();

	public abstract void addControllableSuccessor(State state1, State state2);

	public abstract void addUncontrollableSuccessor(State predecessor, State successor);
	
	public abstract Set<State> getInitialStates();
	
	public abstract Goal getGoal();

}