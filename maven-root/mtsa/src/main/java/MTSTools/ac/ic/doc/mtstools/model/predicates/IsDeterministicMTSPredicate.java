package MTSTools.ac.ic.doc.mtstools.model.predicates;

import java.util.Iterator;

import org.apache.commons.collections15.Predicate;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class IsDeterministicMTSPredicate<S, A> implements Predicate<MTS<S, A>> {

	private TransitionType transitionType;

	@SuppressWarnings("unused")
	private IsDeterministicMTSPredicate() {
	}
	
	public IsDeterministicMTSPredicate(TransitionType transitionType) {
		this.transitionType = transitionType;
	}
	
	@Override
	public boolean evaluate(MTS<S, A> mts) {
		boolean hasNonDetTransitions = false;
		Iterator<S> statesIt = mts.getStates().iterator();
		
		HasNonDeterministicTransitionsStatePredicate<S, A> hasNonDetTransitionsFromStatePredicate = 
				new HasNonDeterministicTransitionsStatePredicate<S, A>(mts, this.transitionType);
		
		while (!hasNonDetTransitions && statesIt.hasNext()) {
			hasNonDetTransitions = hasNonDetTransitionsFromStatePredicate.evaluate(statesIt.next());
		}
		
		return !hasNonDetTransitions;
	}

}
