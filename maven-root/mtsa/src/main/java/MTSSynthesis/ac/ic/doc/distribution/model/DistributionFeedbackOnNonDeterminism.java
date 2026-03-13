package MTSSynthesis.ac.ic.doc.distribution.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.set.UnmodifiableSet;

import MTSTools.ac.ic.doc.mtstools.model.MTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.MTSTransition;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSTransitionWithModalityInfo;

/**
 * Feedback when there are non deterministic transitions while trying to distribute 
 * @author gsibay
 *
 */
public class DistributionFeedbackOnNonDeterminism<S, A> implements DistributionFeedbackItem {

	private Set<MTSTrace<A, S>> tracesToNonDeterministicStates;

	public DistributionFeedbackOnNonDeterminism(Set<MTSTrace<A, S>> tracesToNonDetStates) {
		this.tracesToNonDeterministicStates = UnmodifiableSet.decorate(tracesToNonDetStates);
	}

	@Override
	public String getMessage() {
		StringBuffer message = new StringBuffer();
		message.append("The model being distributed is non deterministic. A trace for each of the non deterministic states is provided:\n");
		for (MTSTrace<A, S> trace : this.tracesToNonDeterministicStates) {
			message.append(this.getTraceAsListOfActions(trace)).append("\n");
		}
		
		return message.toString();
	}

	public Set<MTSTrace<A, S>> getTracesToNonDeterministicStates() {
		return tracesToNonDeterministicStates;
	}
	
	// XXX: This is done because MTSTransition does not have modality. 
	// There should only be one MTSTransition with the modality in its interface
	private List<String> getTraceAsListOfActions(MTSTrace<A, S> trace) {
		List<String> result = new LinkedList<String>();
		for (MTSTransition<A, S> transition : trace) {
			//XXX, TODO: unify and fix MTSTransition. Should always contain the modality info.
			MTSTransitionWithModalityInfo<A, S> transitionWithModality = (MTSTransitionWithModalityInfo<A, S>) transition; 
			result.add(transitionWithModality.toStringWithModality());
		}
		return result;
	}
}
