package MTSSynthesis.ac.ic.doc.distribution.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.UnmodifiableMTS;

/**
 * @author gsibay
 *
 */
public class DistributionResultImpl<S,A> implements DistributionResult<S,A> {

	private Map<Set<A>, MTS<Set<S>, A>> distributedComponentsByAlphabet;
	private boolean isDistributable = false;
	private MTS<S, A> monolithicModel;
	private List<DistributionFeedbackItem> feedback;

	
	private Map<Set<A>, MTS<Set<S>, A>> getDistributedComponentsByAlphabet() {
		return this.distributedComponentsByAlphabet;
	}

	public DistributionResultImpl(MTS<S, A> monolithicModel) {
		this.monolithicModel = monolithicModel;
		this.feedback = new LinkedList<DistributionFeedbackItem>();
		this.distributedComponentsByAlphabet = new HashMap<Set<A>, MTS<Set<S>,A>>();
	}
	
	
	@Override
	public MTS<S, A> getMonolithicModel() {
		return this.monolithicModel;
	}

	
	@Override
	public List<DistributionFeedbackItem> getFeedback() {
		return this.feedback;
	}

	@Override
	public void addFeedbackItem(DistributionFeedbackItem item) {
		this.feedback.add(item);
	}

	@Override
	public boolean isDistributable() {
		return this.isDistributable;
	}

	@Override
	public void setDistributable(boolean isDistributable) {
		this.isDistributable = isDistributable;
		
	}

	@Override
	public MTS<Set<S>, A> getComponent(Set<A> alphabet) {
		return this.getDistributedComponentsByAlphabet().get(alphabet);
	}

	@Override
	public void setComponent(Set<A> alphabet, MTS<Set<S>, A> distributedComponent) {
		Validate.notNull(distributedComponent, "Invalid null distribution");
		Validate.notNull(alphabet, "Invalid null alphabet");
		this.getDistributedComponentsByAlphabet().put(alphabet, UnmodifiableMTS.decorate(distributedComponent));
		
	}
}
