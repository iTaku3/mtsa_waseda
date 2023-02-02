package MTSSynthesis.controller.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.Validate;


public abstract class StateBasedGame<State> implements Game<State> {

	protected Map<State, Set<State>> predecessors;
	protected Map<State, Set<State>> uncontrollableSuccessors;
	protected Map<State, Set<State>> controllableSuccessors;
	protected Set<State> uncontrollable;
	protected Set<State> states;
	protected Set<State> initialStates;

	public StateBasedGame(Set<State> initialStates, Set<State> states) {
		if(!states.containsAll(initialStates))
			Logger.getAnonymousLogger().warning("StateBasedGame.constructor:: initialStates should be contained within provided states");
		this.states = states;
		this.initialStates = initialStates;
		this.predecessors = new HashMap<State, Set<State>>();
		this.uncontrollable = new HashSet<State>();
		this.uncontrollableSuccessors = new HashMap<State, Set<State>>();
		this.controllableSuccessors = new HashMap<State, Set<State>>();
		this.basicInitialisation();
	}

	public Set<State> getInitialStates(){
		return initialStates;
	}

	protected void basicInitialisation() {
		for (State state : this.states) {
			this.predecessors.put(state, new HashSet<State>());
			this.uncontrollableSuccessors.put(state, new HashSet<State>());
			this.controllableSuccessors.put(state, new HashSet<State>());
		}
	}

	@Override
	public boolean isUncontrollable(State state) {
		return this.uncontrollable.contains(state);
	}

	@Override
	public Set<State> getUncontrollableSuccessors(State state) {
		return this.uncontrollableSuccessors.get(state);
	}

	@Override
	public Set<State> getControllableSuccessors(State state) {
		return this.controllableSuccessors.get(state);
	}

	@Override
	public Set<State> getPredecessors(State state) {
		return this.predecessors.get(state);
	}

	@Override
	public Set<State> getStates() {
		return this.states;
	}

	@Override
	public void addControllableSuccessor(State state1, State state2) {
		this.validateNewState(state1);
		if (!this.controllableSuccessors.containsKey(state1)) {
			this.controllableSuccessors.put(state1, new HashSet<State>());
		}
		this.controllableSuccessors.get(state1).add(state2);
	}

	private void validateNewState(State state1) {
		Validate.isTrue(this.states.contains(state1), "State:" + state1 + "it is not part of the game.");
	}

	public void addPredecessor(State predeccessor, State successor) {
		Validate.isTrue(this.states.contains(predeccessor));
		Validate.isTrue(this.states.contains(successor));
		this.predecessors.get(successor).add(predeccessor);
	}

	@Override
	public void addUncontrollableSuccessor(State predecessor, State successor) {
		if (!this.uncontrollableSuccessors.containsKey(predecessor)) {
			this.uncontrollableSuccessors.put(predecessor, new HashSet<State>());
		}
		this.uncontrollableSuccessors.get(predecessor).add(successor);
		this.uncontrollable.add(predecessor);
		this.addPredecessor(predecessor, successor);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer()
		.append("Game:")
		.append("States: [").append(this.getStates()).append("]").append("\n")
		.append("Uncontrollable States: ").append(this.uncontrollable).append("\n")
		.append("Transitions: \n");
		for (State state : this.getStates()) {
			for (State succ : this.getControllableSuccessors(state)) {
				sb.append(" c[").append(state).append("->")
				.append(succ).append("]");
			}
			for (State succ : this.getUncontrollableSuccessors(state)) {
				sb.append(" u[").append(state).append("->")
				.append(succ).append("]");
			}
		}
		sb.append("\n");
		return sb.toString();
	}
	
	@Override
	public Set<State> getSuccessors(State state) {
		Set<State> result = new HashSet<State>();
		result.addAll(this.getUncontrollableSuccessors(state));
		result.addAll(this.getControllableSuccessors(state));
		return result;
	}
}