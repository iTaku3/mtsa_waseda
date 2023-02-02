package MTSTools.ac.ic.doc.mtstools.model.impl;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.POSSIBLE;
import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.REQUIRED;

import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.Consistency;


/**
 * This class implements a weak consistency relation, which characterises consistency
 * between models under weak semantics. If the silent actions sets is empty then it
 * characterises strong consistency.
 * 
 */
public class WeakConsistency extends AbstractConsistencyRelation implements Consistency {

	private Set<?> silentActions;

		
	public WeakConsistency(Set<?> silentActions) {
		this.setSilentActions(silentActions);
	}
	
	public Set<?> getSilentActions() {
		return silentActions;
	}

	public void setSilentActions(Set<?> silentActions) {
		this.silentActions = silentActions;
	}

	@SuppressWarnings("unchecked")
	protected <S1, S2, A> FixedPointRelationConstructor getRelationContructor(MTS<S1, A> mtsA, MTS<S2, A> mtsB) {		
		return new FixedPointRelationConstructor(				
				new SimulationChain()
					.add(new WeakForwardSimulation(this.getSilentActions(), REQUIRED, POSSIBLE))
					.add(new WeakBackwardSimulation(this.getSilentActions(), REQUIRED, POSSIBLE)));
	}

}
