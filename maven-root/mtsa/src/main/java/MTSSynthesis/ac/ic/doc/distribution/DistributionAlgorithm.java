package MTSSynthesis.ac.ic.doc.distribution;

import MTSSynthesis.ac.ic.doc.distribution.model.AlphabetDistribution;
import MTSSynthesis.ac.ic.doc.distribution.model.DistributionResult;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

/**
 * TODO add comment
 * @author gsibay
 *
 */
public interface DistributionAlgorithm<S, A> {

	public abstract DistributionResult<S, A> tryDistribute(MTS<S, A> monolithicModel, AlphabetDistribution<A> alphabetDistribution, A tauAction);

}
