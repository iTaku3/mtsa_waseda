package MTSTools.ac.ic.doc.mtstools.model.predicates;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.Predicate;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

/**
 * Built from an MTS mts. The predicate returns true when evaluated with a state
 * that has non deterministic transitions
 * @author gsibay
 *
 */
public class HasNonDeterministicTransitionsStatePredicate<S, A> implements 	Predicate<S> {

	private MTS<S, A> mts;
	private TransitionType transitionType;
	
	@SuppressWarnings("unused")
	private HasNonDeterministicTransitionsStatePredicate() {
	}
	
	public HasNonDeterministicTransitionsStatePredicate(MTS<S, A> mts, TransitionType transitionType) {
		this.mts = mts;
		this.transitionType = transitionType;
	}
	
	@Override
	public boolean evaluate(S state) {
		boolean hasNonDetTransitions = false;
		Set<A> actions = mts.getActions();
		
		BinaryRelation<A, S> transitionsFromState = this.mts.getTransitions(state, this.transitionType);
		Iterator<A> actionsIt = actions.iterator();
		while (!hasNonDetTransitions && actionsIt.hasNext()) {
			hasNonDetTransitions = transitionsFromState.getImage(actionsIt.next()).size() > 1;
		}
		return hasNonDetTransitions;
	}

}
