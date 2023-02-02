package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.*;

import org.apache.commons.lang.Validate;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

public class MTSImpl<State, Action> extends AbstractTransitionSystem<State, Action> implements MTS<State, Action> {

	private EnumMap<TransitionType, Map<State, BinaryRelation<Action, State>>> transitionsByType;

	public MTSImpl(State initialState) {
		super(initialState);
	}

	public boolean addPossible(State from, Action label, State to) {
		this.validateNewTransition(from, label, to);
		boolean added = this.getTransitionsForInternalUpdate(from, TransitionType.POSSIBLE).addPair(label, to);
		if (added) {
			this.getTransitionsForInternalUpdate(from, TransitionType.MAYBE).addPair(label, to);
		}
		return added;
	}

	public boolean addRequired(State from, Action label, State to) {
		this.validateNewTransition(from, label, to);
		this.getTransitionsForInternalUpdate(from, TransitionType.POSSIBLE).addPair(label, to);
		boolean added = this.getTransitionsForInternalUpdate(from, TransitionType.REQUIRED).addPair(label, to);
		if (added) {
			this.getTransitionsForInternalUpdate(from, TransitionType.MAYBE).removePair(label, to);
		}
		return added;
	}

	public boolean addState(State state) {
		if (super.addState(state)) {
			for (TransitionType type : TransitionType.values()) {
				this.getTransitionsByType().get(type).put(state, this.newRelation());
			}
			return true;
		}
		return false;
	}

	public boolean addTransition(State from, Action label, State to, TransitionType type) {
		this.validateNewTransition(from, label, to);
		return type.addTransition(this, from, label, to);
	}

	public BinaryRelation<Action, State> getTransitions(State state, TransitionType type) {
		return this.getTransitions(type).get(state);
	}

	protected BinaryRelation<Action, State> getTransitionsForInternalUpdate(State state, TransitionType type) {
		return this.getTransitions(state, type);
	}

	public Map<State, BinaryRelation<Action, State>> getTransitions(TransitionType type) {
		return this.getTransitionsByType().get(type);
	}

	protected EnumMap<TransitionType, Map<State, BinaryRelation<Action, State>>> getTransitionsByType() {
		if (this.transitionsByType == null) {
			this.setTransitionsByType(new EnumMap<TransitionType, Map<State, BinaryRelation<Action, State>>>
					(TransitionType.class));
			for (TransitionType type : TransitionType.values()) {
				this.transitionsByType.put(type, new HashMap<State, BinaryRelation<Action, State>>());
			}
		}
		return this.transitionsByType;
	}

	protected void setTransitionsByType(EnumMap<TransitionType, Map<State, BinaryRelation<Action, State>>>
			                                    transitionsByType) {
		this.transitionsByType = transitionsByType;
	}

	private void validateExistingTransition(State from, Action label, State to, TransitionType possible) {
		Validate.isTrue(getTransitions(from, possible).contains(Pair.create(label, to)));
	}

	/**
	 * Elimina la transicion si existe y ademas elimina los posibles estados que
	 * quedaran huerfanos luego de borrar la transicion.
	 * 
	 */
	public boolean removeTransition(State from, Action label, State to, TransitionType type) {
		this.validateNewTransition(from, label, to);
		this.validateExistingTransition(from, label, to, type);
		return type.removeTransition(this, from, label, to);
	}

	public boolean removePossible(State from, Action label, State to) {
		boolean removed = this.getTransitionsForInternalUpdate(from, TransitionType.POSSIBLE).removePair(label, to);
		if (removed) {
			this.getTransitionsForInternalUpdate(from, TransitionType.MAYBE).removePair(label, to);
			this.getTransitionsForInternalUpdate(from, TransitionType.REQUIRED).removePair(label, to);
		}
		return removed;
	}

	public boolean removeRequired(State from, Action label, State to) {
		boolean removed = this.getTransitionsForInternalUpdate(from, TransitionType.REQUIRED).removePair(label, to);
		if (removed) {
			removed &= this.getTransitionsForInternalUpdate(from, TransitionType.POSSIBLE).removePair(label, to);
		}
		return removed;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("States: ").append(this.getStates()).append("\r\n");
		sb.append("Actions: ").append(this.getActions()).append("\r\n");
		sb.append("Required Transitions: ").append(this.getTransitions(TransitionType.REQUIRED)).append("\r\n");
		sb.append("Maybe Transitions: ").append(this.getTransitions(TransitionType.MAYBE)).append("\r\n");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		MTSImpl<?, ?> mts = (MTSImpl<?, ?>) o;
		boolean maybeEquals = !(getTransitions(TransitionType.MAYBE) != null ? !getTransitions(TransitionType.MAYBE)
				.equals(mts.getTransitions(TransitionType.MAYBE)) : mts.getTransitions(TransitionType.MAYBE) != null);

		boolean requiredEquals = !(getTransitions(TransitionType.REQUIRED) != null ? !getTransitions(TransitionType
				.REQUIRED).equals(mts.getTransitions(TransitionType.REQUIRED)) : mts.getTransitions(TransitionType
				.REQUIRED) != null);
		return maybeEquals && requiredEquals;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (getTransitions(TransitionType.MAYBE) != null ? getTransitions(TransitionType.MAYBE)
				.hashCode() : 0) + (getTransitions(TransitionType.REQUIRED) != null ? getTransitions(TransitionType
				.REQUIRED).hashCode() : 0);
		return result;
	}

	protected BinaryRelation<Action, State> getTransitionsFrom(State state) {
		return getTransitions(state, TransitionType.POSSIBLE);
	}

	public void removeAction(Action action) {
		if (!hasTransitionOn(action)) {
			getInternalActions().remove(action);
		}

	}

	private boolean hasTransitionOn(Action action) {
		for (State state : getStates()) {
			if (!getTransitionsFrom(state).getImage(action).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	// TODO Dipi, refactorizar
	// public Set<State> getReachableStatesBy(State state, TransitionType
	// transitionType) {
	// Set<State> reachableStates = new
	// HashSet<State>((int)(this.getStates().size()/.75f + 1),0.75f);
	// Queue<State> toProcess = new LinkedList<State>();
	// toProcess.offer(state);
	// reachableStates.add(state);
	// while(!toProcess.isEmpty()) {
	// for (Pair<Action, State> transition : getTransitions(toProcess.poll(),
	// transitionType)) {
	// if (!reachableStates.contains(transition.getSecond())) {
	// toProcess.offer(transition.getSecond());
	// reachableStates.add(transition.getSecond());
	// }
	// }
	// }
	// return reachableStates;
	// }
	//
	// @Override
	// protected Collection<State> getReachableStatesBy(State state) {
	// return getReachableStatesBy(state, TransitionType.POSSIBLE);
	// }

	@Override
	protected void removeTransitions(Collection<State> unreachableStates) {
		for (Map<State, BinaryRelation<Action, State>> transitions : this.transitionsByType.values()) {
			this.removeTransitions(transitions, unreachableStates);
		}
	}
}
