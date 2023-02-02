package MTSTools.ac.ic.doc.mtstools.model.operations.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.facade.MTSAFacade;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class MTSPropertyToBuchiConverter {

	public static <State, Action> void convert(MTS<State, Action> property, State trapState, Action specialAction) {
		//remove every transition to error.
		MTSAFacade.deleteTransitionsToDeadlock(property);
		property.removeUnreachableStates();
		
		property.addAction(specialAction);

		//add buchi aceptance state semantic to all original states of the automata
		for (Iterator it = property.getStates().iterator(); it.hasNext();) {
			State state = (State) it.next();
			property.addTransition(state, specialAction, state, TransitionType.REQUIRED);
		}
		
		//add trap state and loop transitions over it
		property.addState(trapState);
		for (Iterator it = property.getActions().iterator(); it.hasNext();) {
			Action action = (Action) it.next();
			if (!action.equals(specialAction)) {
				property.addTransition(trapState, action, trapState, TransitionType.REQUIRED);
			}
		}
		
		//add transitions from other states to trap state
		for (Iterator it = property.getStates().iterator(); it.hasNext();) {
			State state = (State) it.next();
			Set<Action> actionsUsed = new HashSet<Action>();
			CollectionUtils.addAll(actionsUsed, property.getActions().iterator());
			actionsUsed.remove(specialAction);
			for (Iterator it2 = property.getTransitions(state, TransitionType.REQUIRED).iterator(); it2.hasNext();) {
				Pair<Action, State> pair = (Pair<Action, State>) it2.next();
				actionsUsed.remove(pair.getFirst());
			}
			for (Iterator it2 = actionsUsed.iterator(); it2.hasNext();) {
				Action action = (Action) it2.next();
				property.addTransition(state, action, trapState, TransitionType.REQUIRED);
			}
		}
	}

}
