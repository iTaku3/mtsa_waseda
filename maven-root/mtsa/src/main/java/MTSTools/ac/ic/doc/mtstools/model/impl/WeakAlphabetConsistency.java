package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.Consistency;

public class WeakAlphabetConsistency extends AbstractConsistencyRelation
		implements Consistency {

	private Set<?> silentActions;

	
	public WeakAlphabetConsistency(Set<?> silentActions) {
		this.setSilentActions(silentActions);
	}
	
	public Set<?> getSilentActions() {
		return silentActions;
	}

	public void setSilentActions(Set<?> silentActions) {
		this.silentActions = silentActions;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <S1, S2, A> FixedPointRelationConstructor getRelationContructor(
			MTS<S1, A> mtsA, MTS<S2, A> mtsB) {
		return new FixedPointRelationConstructor(				
				new SimulationChain()
					.add(new WeakAlphabetConsistencyForwardSimulation(mtsA, mtsB, this.getSilentActions()))
					.add(new WeakAlphabetConsistencyBackwardSimulation(mtsA, mtsB, this.getSilentActions())));
	}

}
