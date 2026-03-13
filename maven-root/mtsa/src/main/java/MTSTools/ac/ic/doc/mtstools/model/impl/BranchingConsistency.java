package MTSTools.ac.ic.doc.mtstools.model.impl;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.POSSIBLE;
import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.REQUIRED;

import java.util.Set;

import org.apache.commons.lang.Validate;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.Consistency;

public class BranchingConsistency extends AbstractConsistencyRelation implements
		Consistency {

	private Set<?> silentActions;

	
	public BranchingConsistency(Set<?> silentActions) {
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
		Validate.isTrue(mtsA.getActions().equals(mtsB.getActions()));
		
		return new FixedPointRelationConstructor(
				new SimulationChain()
					.add(new BranchingForwardSimulation(this.getSilentActions(), REQUIRED, POSSIBLE))
					.add(new BranchingBackwardSimulation(this.getSilentActions(), REQUIRED, POSSIBLE)));
	}

}
