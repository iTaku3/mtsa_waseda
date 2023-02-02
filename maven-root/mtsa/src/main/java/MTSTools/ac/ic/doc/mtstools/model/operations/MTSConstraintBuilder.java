package MTSTools.ac.ic.doc.mtstools.model.operations;

import java.util.HashSet;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;
import MTSTools.ac.ic.doc.mtstools.model.MTSTransition;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSTransitionImpl;

public class MTSConstraintBuilder {

	public <State, Action> void makeConstrainedModel(MTS<State, Action> mts) {
		
		Set<MTSTransition<Action, State>> toMakePossible = new HashSet<MTSTransition<Action,State>>(); 
		for (State state : mts.getStates()) {
			BinaryRelation<Action, State> transitions = mts.getTransitions(state, TransitionType.REQUIRED);
			if (transitions.size()>1) {
				for (Pair<Action, State> transition : transitions) {
					Action action = transition.getFirst();
					assert (!MTSConstants.ASTERIX.equals(action));
					toMakePossible.add(MTSTransitionImpl.createMTSEventState(state, action, transition.getSecond()));
				}
			}
		}
		for (MTSTransition<Action, State> tr : toMakePossible) {
			mts.removeRequired(tr.getStateFrom(), tr.getEvent(), tr.getStateTo());
			mts.addPossible(tr.getStateFrom(), tr.getEvent(), tr.getStateTo());
		}
	}

}
