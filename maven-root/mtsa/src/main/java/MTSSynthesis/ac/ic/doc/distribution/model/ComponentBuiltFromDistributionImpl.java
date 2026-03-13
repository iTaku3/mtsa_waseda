package MTSSynthesis.ac.ic.doc.distribution.model;

import java.util.Set;

import org.apache.commons.collections.SetUtils;

import MTSTools.ac.ic.doc.mtstools.model.MTS;

/**
 * 
 * Default implementation
 * @author gsibay
 * @param <A>
 * @param <S>
 *
 */
public class ComponentBuiltFromDistributionImpl<A, S> implements ComponentBuiltFromDistribution<S, A> {

	private MTS<Set<S>, A> component;
	private Set<DeterminisationModalityMismatch<S, A>> determinisationModalityMismtches;
	
	@SuppressWarnings("unchecked")
	public ComponentBuiltFromDistributionImpl(MTS<Set<S>, A> component, 
			Set<DeterminisationModalityMismatch<S, A>> determinisationModalityMismtches) {
		this.component = component;
		this.determinisationModalityMismtches = SetUtils.unmodifiableSet(determinisationModalityMismtches);
	}
	
	@Override
	public MTS<Set<S>, A> getComponent() {
		return this.component;
	}

	@Override
	public Set<DeterminisationModalityMismatch<S, A>> getDeterminisationModalityMismatches() {
		return this.determinisationModalityMismtches;
	}
	
}
