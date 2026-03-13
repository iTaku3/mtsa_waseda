package MTSSynthesis.ac.ic.doc.distribution.model;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

/**
 * 
 * @author gsibay
 *
 */
public class DeterminisationModalityMismatchImpl<S, A> implements DeterminisationModalityMismatch<S, A> {


	private S stateWithReqTransition;
	private S stateWithMayTransition;
	private A action;

	public DeterminisationModalityMismatchImpl(S stateWithReqTransition, S stateWithPossTransition, A action) {
		Validate.isTrue(!stateWithReqTransition.equals(stateWithPossTransition));
		this.stateWithMayTransition = stateWithPossTransition;
		this.stateWithReqTransition = stateWithReqTransition;
		this.action = action;
	}

	@Override
	public Pair<S, S> getDeterministationJointStates() {
		return new Pair<S, S>(this.stateWithMayTransition, this.stateWithReqTransition);
	}

	@Override
	public A getAction() {
		return this.action;
	}

	@Override
	public S getStateByModality(TransitionType type) {
		Validate.isTrue(type.equals(TransitionType.MAYBE) || type.equals(TransitionType.REQUIRED));
		if(type.equals(TransitionType.MAYBE)) {
			return this.stateWithMayTransition;
		} else {
			return this.stateWithReqTransition;
		}
	}
	
	@Override
	public int hashCode(){
	    return new HashCodeBuilder()
	        .append(this.getAction())
	        .append(this.getStateByModality(TransitionType.MAYBE))
	        .append(this.getStateByModality(TransitionType.REQUIRED))
	        .toHashCode();
	}

	@Override
	public boolean equals(final Object obj){
	    if(obj instanceof DeterminisationModalityMismatchImpl<?, ?>){
	        @SuppressWarnings("rawtypes")
			final DeterminisationModalityMismatchImpl other = (DeterminisationModalityMismatchImpl) obj;
	        return new EqualsBuilder()
	            .append(this.action, other.getAction())
	            .append(this.getStateByModality(TransitionType.MAYBE), other.getStateByModality(TransitionType.MAYBE))
	            .append(this.getStateByModality(TransitionType.REQUIRED), other.getStateByModality(TransitionType.REQUIRED))
	            .isEquals();
	    } else{
	        return false;
	    }
	}
}
