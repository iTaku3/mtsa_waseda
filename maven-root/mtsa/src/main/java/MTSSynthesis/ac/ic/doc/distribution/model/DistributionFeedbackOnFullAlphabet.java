package MTSSynthesis.ac.ic.doc.distribution.model;

import java.util.Set;


/**
 * TODO add comment
 * @author gsibay
 *
 */
public class DistributionFeedbackOnFullAlphabet<A> implements DistributionFeedbackItem {

	private AlphabetDistribution<A> alphabetDistribution;
	private Set<A> systemModelActions;

	public DistributionFeedbackOnFullAlphabet(AlphabetDistribution<A> alphabetDistribution, Set<A> systemModelActions) {
		this.alphabetDistribution = alphabetDistribution;
		this.systemModelActions = systemModelActions;
	}

	@Override
	public String getMessage() {
		StringBuffer message = new StringBuffer();
		message.append("The alphabet distribution: ").append(this.alphabetDistribution).append(",\n");
		message.append("is not compatible with the system model's alphabet: ").append(this.systemModelActions).append("\n");
		return message.toString();
	}
}
