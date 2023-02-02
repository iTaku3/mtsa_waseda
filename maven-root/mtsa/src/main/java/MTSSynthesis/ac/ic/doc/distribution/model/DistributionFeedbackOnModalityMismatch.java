package MTSSynthesis.ac.ic.doc.distribution.model;

import java.util.LinkedList;
import java.util.List;

import MTSTools.ac.ic.doc.mtstools.model.MTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.MTSTransition;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSTransitionWithModalityInfo;

/**
 * 
 * @author gsibay
 *
 */
public class DistributionFeedbackOnModalityMismatch<S, A> implements DistributionFeedbackItem {
	
	private DeterminisationModalityMismatch<S, A> mismatch;
	private MTSTrace<A, S> traceToStateWithMayTransition;
	private MTSTrace<A, S> traceToStateWithReqTransition;
	
	public DeterminisationModalityMismatch<S, A> getMismatch() {
		return this.mismatch;
	}
	
	@SuppressWarnings("unused")
	private DistributionFeedbackOnModalityMismatch() {
	}
	
	public DistributionFeedbackOnModalityMismatch(DeterminisationModalityMismatch<S,A> aMismatch) {
		this.mismatch = aMismatch;
	}
	
	public void setTraceToStateWithMayTransition(MTSTrace<A, S> traceToStateWithMayTransition) {
		this.traceToStateWithMayTransition = traceToStateWithMayTransition;
	}

	public void setTraceToStateWithReqTransition(MTSTrace<A, S> traceToStateWithReqTransition) {
		this.traceToStateWithReqTransition = traceToStateWithReqTransition;
	}

	@Override
	public String getMessage() {
		StringBuffer message = new StringBuffer();
		message.append("Modality mismatch on action ").append(mismatch.getAction());
		message.append(".\n Trace to state allowing the maybe action: ").append(this.getTraceAsListOfActions(this.traceToStateWithMayTransition));
		message.append(".\n Trace to state allowing the required action: ").append(this.getTraceAsListOfActions(this.traceToStateWithReqTransition)).append("\n");
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
