package MTSSynthesis.ac.ic.doc.distribution;

import MTSSynthesis.ac.ic.doc.distribution.model.AlphabetDistribution;
import MTSSynthesis.ac.ic.doc.distribution.model.DistributionResult;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

/**
 * Facade for the distribution algorithm
 * @author gsibay
 *
 */
public class DistributionFacade<S,A> {

	private DistributionAlgorithm<S,A> distributionAlgorithm = new DistributionAlgorithmDefaultImpl<S, A>();
	
	public DistributionResult<S, A> tryDistribute(MTS<S, A> monolithicModel, AlphabetDistribution<A> alphabetDistribution, A tauAction) {
		//Validate.isTrue(this.alphabetDistributionCoversAllActions(monolithicModel.getActions(), alphabetDistribution.getAlphabets()));
		return this.distributionAlgorithm.tryDistribute(monolithicModel, alphabetDistribution, tauAction);
	}

	
	/**
	 * Checks if the union of the component alphabets is equals to the actions
	 * @param actions
	 * @param componentAlphabets
	 * @return
	 */
	/*
	private boolean alphabetDistributionCoversAllActions(Set<A> actions, Set<Set<A>> componentAlphabets) {
		Set<A> union = new HashSet<A>();
		
		for (Set<A> componentAlphabet : componentAlphabets) {
			union.addAll(componentAlphabet);
		}
		
		return actions.equals(union);
	}*/

}
