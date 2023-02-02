package MTSSynthesis.ac.ic.doc.distribution.model;

import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.MTS;

/**
 * @author gsibay
 *
 */
public interface DistributionResult<S,A> {

	public MTS<S,A> getMonolithicModel();

	public List<DistributionFeedbackItem> getFeedback();
	public void addFeedbackItem(DistributionFeedbackItem item);
	
	public boolean isDistributable();
	
	public void setDistributable(boolean isDistributable);
	
	/**
	 * Gets the calculated component for the alphabet.
	 * The component is valid only if the monolithic model is distributable
	 *   
	 * @return
	 */
	public MTS<Set<S>, A> getComponent(Set<A> alphabet);
	
	/**
	 * Sets the component for the alphabet
	 * @param alphabet
	 * @param distributedComponent
	 */
	public void setComponent(Set<A> alphabet, MTS<Set<S>, A> distributedComponent);
	
	
}
