package MTSSynthesis.ac.ic.doc.distribution.model;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

/**
 * TODO add comment
 * @author gsibay
 *
 */
public interface DeterminisationModalityMismatch<S,A> {

	/**
	 * States from where the action can be taken with maybe and required modality
	 * @return
	 */
	public Pair<S, S> getDeterministationJointStates();
	
	public S getStateByModality(TransitionType type);
	
	/**
	 * The action causing the modality missmatch
	 * @return
	 */
	public A getAction();
}
