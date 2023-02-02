package MTSTools.ac.ic.doc.mtstools.model.predicates;

import org.apache.commons.collections15.Predicate;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

/**
 * Built from an MTS mts. The predicate returns true when evaluated with a state
 * that is a deadlock state in the MTS mts.
 * @author gsibay
 *
 */
public class IsDeadlockStatePredicate<S, A> implements Predicate<S> {

	private MTS<S, A> mts;

	@SuppressWarnings("unused")
	private IsDeadlockStatePredicate() {
	}
	
	public IsDeadlockStatePredicate(MTS<S, A> mts) {
		this.mts = mts;
	}
	
	@Override
	public boolean evaluate(S state) {
		return this.mts.getTransitions(state, TransitionType.POSSIBLE).size() == 0;
	}

}
