package MTSSynthesis.controller.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;


public class SubsetConstructionBuilder<S, A> {

	private MTS<S, A> nonDet;

	public SubsetConstructionBuilder(MTS<S, A> nonDet) {
		this.nonDet = nonDet;
	}

	public MTS<Set<S>, A> build() {
		Set<S> initial = Collections.singleton(this.nonDet.getInitialState());
		MTS<Set<S>, A> result = new MTSImpl<Set<S>, A>(initial);
		result.addActions(nonDet.getActions()); // notice that this may include tau.

		Queue<Set<S>> toVisit = new LinkedList<Set<S>>();
		Set<Set<S>> visited = new HashSet<Set<S>>();
		toVisit.add(initial);

		while (!toVisit.isEmpty()) {
			Set<S> currentState = toVisit.poll();
			if (visited.contains(currentState)) {
				continue;
			}
			visited.add(currentState);
			result.addState(currentState);

			addSuccessors(currentState, result, toVisit, visited);
		}
		return result;
	}

	private void addSuccessors(Set<S> currentSet, MTS<Set<S>, A> result, Queue<Set<S>> toVisit, Set<Set<S>> visited) {
		Set<A> enabledActions = this.enabledActions(currentSet);

		// checking for dead-ends
		if (isDeadlockSet(currentSet, enabledActions)) {
			return ;
		}

		for (A action : enabledActions) {
			Set<S> succSet = computeSuccessorsSet(currentSet, action);
			result.addState(succSet);
			result.addTransition(currentSet, action, succSet, TransitionType.REQUIRED);
			if (!visited.contains(succSet)) {
				toVisit.add(succSet);
			}
		}
	}

	private Set<A> enabledActions(Set<S> currentSet) {
		Set<A> setOfActions = new HashSet<A>();
		for (S state : currentSet) {
			BinaryRelation<A, S> transitions = this.nonDet.getTransitions(state, TransitionType.REQUIRED);
			for (Pair<A, S> tr : transitions) {
				setOfActions.add(tr.getFirst());
			}
		}
		return setOfActions;
	}

	private Set<S> computeSuccessorsSet(Set<S> currentState, A action) {
		Set<S> result = new HashSet<S>();
		for (S state : currentState) {
			result.addAll(this.nonDet.getTransitions(state, TransitionType.POSSIBLE).getImage(action));
		}
		return result;
	}

	/**
	 * A set of states <i>S</i> is a deadlock state 
	 * if there exists a state <i>s</i> in <i>S</i> such that 
	 * no action in <i>enabledActions</i> is enabled.    
	 */
	private boolean isDeadlockSet(Set<S> current, Set<A> enabledActions) {
		for (S state : current) {
			if (!isImageEnabled(state, enabledActions)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isImageEnabled(S state, Set<A> enabledActions) {
		BinaryRelation<A, S> trs = this.nonDet.getTransitions(state, TransitionType.POSSIBLE);
		for (Iterator<Pair<A, S>> iterator = trs.iterator(); iterator.hasNext();) {
			Pair<A, S> pair = iterator.next();
			if (enabledActions.contains(pair.getFirst())) {
				return true;
			}
		}
		return false;
	}
}