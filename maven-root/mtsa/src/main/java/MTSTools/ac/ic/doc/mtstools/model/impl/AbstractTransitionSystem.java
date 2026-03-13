package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.lang.Validate;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.MapSetBinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.TransitionSystem;

abstract class AbstractTransitionSystem<State, Action> implements TransitionSystem<State, Action> {

	private Set<Action> actions;
	private State initialState;
	private Set<State> states;
		
	protected abstract BinaryRelation<Action, State> getTransitionsFrom(State toProcess); 
	
	protected AbstractTransitionSystem(State initialState) {
		this.actions = new HashSet<Action>();
		this.states = new HashSet<State>();
		this.initialState = initialState;
		this.addState(initialState);
	}
	
	public boolean addAction(Action action) {
		return this.getInternalActions().add(action);
	}

	public boolean addActions(Collection<? extends Action> actions) {
		boolean result = false;
		for (Action action : actions) {
			result |= this.addAction(action);
		}
		return result;
	}

	public boolean addState(State state) {
		return this.getInternalState().add(state);
	}

	public boolean addStates(Collection<? extends State> states) {
		boolean result = false;
		for (State state : states) {
			result |= this.addState(state);
		}
		return result;
	}

	public Set<Action> getActions() {
		return Collections.unmodifiableSet(this.getInternalActions());
	}


	public State getInitialState() {
		return initialState;
	}

	protected Set<Action> getInternalActions() {
		return this.actions;
	}

	protected Set<State> getInternalState() {
		return this.states;
	}

	public Set<State> getStates() {
		return Collections.unmodifiableSet(this.getInternalState());
	}

	protected BinaryRelation<Action, State> newRelation() {
		return new MapSetBinaryRelation<Action, State>();
	}

	protected void validateNewTransition(State from, Action label, State to) {
		Validate.isTrue(this.actions.contains(label), "Action: " + label + " is not in the alphabet");
		Validate.isTrue(this.states.contains(from), "State: " + from + " doesn't exists.");
		Validate.isTrue(this.states.contains(to), "State: " + to + " doesn't exists.");

	}
	
	protected boolean removeInternalState(State state) {
		return getInternalState().remove(state);
	}

	public boolean removeUnreachableStates() {
		
		State state = getInitialState();
		Collection<State> reachableStates = getReachableStatesBy(state);
		Collection<State> unreachable = CollectionUtils.subtract(getInternalState(), reachableStates);
		this.removeStates(unreachable);
		return !unreachable.isEmpty();
		
	}
	
	protected Collection<State> getReachableStatesBy(State state) {
		Collection<State> reachableStates = new HashSet<State>((int)(this.getStates().size()/.75f + 1),0.75f);
		Queue<State> toProcess = new LinkedList<State>();
		toProcess.offer(state);
		reachableStates.add(state);
		while(!toProcess.isEmpty()) {
			for (Pair<Action, State> transition : getTransitionsFrom(toProcess.poll())) {
				if (!reachableStates.contains(transition.getSecond())) {
					toProcess.offer(transition.getSecond());
					reachableStates.add(transition.getSecond());
				}
			}
		}
		return reachableStates;
	}

	protected void removeStates(Collection<State> unreachableStates) {
		this.removeTransitions(unreachableStates);
		this.getInternalState().removeAll(unreachableStates);
	}
	
	protected void removeTransitions(Map<State, BinaryRelation<Action, State>> transitions, Collection<State> unreachableStates) {
		transitions.keySet().removeAll(unreachableStates);
		for (BinaryRelation<Action, State> rel : transitions.values()) {
			for (Iterator<Pair<Action, State>> iter = rel.iterator(); iter.hasNext();) {
				State state = iter.next().getSecond();
				if (unreachableStates.contains(state)) {
					iter.remove();
				}				
			}			
		}
	}

	/**
	 * removes all transitions, which has one of the passesed states as source or target.
	 * @param unreachableStates
	 */
	abstract protected void removeTransitions(Collection<State> unreachableStates);

	public void setInitialState(State state)
	{
	  initialState = state;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AbstractTransitionSystem<?, ?> that = (AbstractTransitionSystem<?, ?>) o;

		if (actions != null ? !actions.equals(that.actions) : that.actions != null) {
			return false;
		}
		return !(states != null ? !states.equals(that.states) : that.states != null);

	}

	@Override
	public int hashCode() {
		int result = actions != null ? actions.hashCode() : 0;
		result = 31 * result + (states != null ? states.hashCode() : 0);
		return result;
	}
}
