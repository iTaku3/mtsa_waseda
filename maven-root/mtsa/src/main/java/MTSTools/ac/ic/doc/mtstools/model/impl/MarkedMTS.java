package MTSTools.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import java.util.Iterator;
import org.apache.commons.collections15.CollectionUtils;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Victor Wjugow on 19/06/15.
 */
public class MarkedMTS<S, A> extends MTSImpl<S, A> {

	private Set<Set<S>> markedStatesSet;

	public MarkedMTS(MTS<S, A> mts, S initialState, Set<Set<S>> terminalSet) {
		super(initialState);
		super.addActions(mts.getActions());
		super.addStates(new HashSet<S>(mts.getStates()));
		EnumMap<TransitionType, Map<S, BinaryRelation<A, S>>> trasitionsByType;
		trasitionsByType = new EnumMap<TransitionType, Map<S, BinaryRelation<A, S>>>(TransitionType.class);
		for (TransitionType type : TransitionType.values()) {
			Map<S, BinaryRelation<A, S>> transitions = new HashMap<S, BinaryRelation<A, S>>(mts.getTransitions(type));
			trasitionsByType.put(type, transitions);
		}
		super.setTransitionsByType(trasitionsByType);
		markedStatesSet = new HashSet<Set<S>>(terminalSet);
	}

	/**
	 * Removes the states that are not 'marked' and all the transitions to and from the ones removed.
	 *
	 * @param initialState this will be the new initial state after removing the non-marked states.
	 * @param currentState this state is used to determine with which of the terminal sets to work with (if there is
	 *                     more than one)
	 * @return
	 */
	public MTSImpl<S, A> chopNotMarkedStates(S initialState, S currentState) {
		MarkedMTS<S, A> chopped = new MarkedMTS<S, A>(this, initialState, this.markedStatesSet);
		Set<S> markedStates = this.selectMarkedStates(currentState);
		Collection<S> unreachable = CollectionUtils.subtract(getInternalState(), markedStates);
		chopped.removeStates(unreachable);

		Set<A> newActions = new HashSet<A>();
		for (S state : chopped.getStates()) {
			for (Pair<A, S> pair : chopped.getTransitions(state, TransitionType.REQUIRED)) {
				newActions.add(pair.getFirst());
			}
		}
		for (A action : this.getActions()) {
			if (!newActions.contains(action)) {
				chopped.removeAction(action);
			}
		}
		return chopped;
	}

	private Set<S> selectMarkedStates(S currentState) {
		Set<S> currentTerminalSet = selectMarkedStatesIfStatePresent(currentState);
		if (currentTerminalSet == null) {
			BinaryRelation<A, S> transitions = this.getTransitions(currentState, TransitionType.REQUIRED);
			for (Iterator<Pair<A, S>> it = transitions.iterator(); it.hasNext() && currentTerminalSet == null; ) {
				Pair<A, S> transition = it.next();
				//TODO: Problematic situation_
				//currentState has 2 transitions. With one it goes to one terminal set, with the other, to another one.
				currentTerminalSet = selectMarkedStatesIfStatePresent(transition.getSecond());
				currentTerminalSet.add(currentState);
			}
		}
		return currentTerminalSet;
	}

	private Set<S> selectMarkedStatesIfStatePresent(S currentState) {
		Set<S> currentTerminalSet = null;
		for (Set<S> terminalSet : this.markedStatesSet) {
			if (terminalSet.contains(currentState)) {
				currentTerminalSet = terminalSet;
			}
		}
		return currentTerminalSet;
	}

	public Set<S> getMarkedStates(S currentState) {
		return selectMarkedStates(currentState);
	}
}