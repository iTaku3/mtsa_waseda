package MTSSynthesis.ac.ic.doc.distribution.model;

import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.MTS;

/**
 * Component bult from a distribution
 * @author gsibay
 *
 */
public interface ComponentBuiltFromDistribution <S, A> {

	public MTS<Set<S>, A> getComponent();
	public Set<DeterminisationModalityMismatch<S, A>> getDeterminisationModalityMismatches();
}
