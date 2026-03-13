package MTSSynthesis.ac.ic.doc.distribution.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.MTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.MTSTransition;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSTransitionWithModalityInfo;

/**
 * 
 * @author gsibay
 *
 */
public class DistributionFeedbackOnTracesAddedByComposition<S, A> implements DistributionFeedbackItem {

	private Set<MTSTrace<A, S>> tracesAddedByComposition;

	public DistributionFeedbackOnTracesAddedByComposition(Set<MTSTrace<A, S>> traces) {
		this.tracesAddedByComposition = traces;
	}

	@Override
	public String getMessage() {
		StringBuffer message = new StringBuffer();
		message.append("The LTS view of the model being distributed is not distributable. The composition of the determinised and minimised" +
				" projection of the LTS onto the distributed alphabet produced new traces:\n");
		for (MTSTrace<A, S> trace : this.tracesAddedByComposition) {
			message.append(this.getTraceAsListOfActions(trace)).append("\n");
		}
		return message.toString();
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
