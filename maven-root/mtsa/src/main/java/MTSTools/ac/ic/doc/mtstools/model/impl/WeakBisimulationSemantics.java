package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class WeakBisimulationSemantics extends BaseSemanticsByRelation {

	public WeakBisimulationSemantics(Set<?> silentActions) {
		super(new FixedPointRelationConstructor(
				new SimulationChain()
					.add(new WeakForwardSimulation(silentActions))
					.add(new WeakBackwardSimulation(silentActions, TransitionType.REQUIRED, TransitionType.REQUIRED))),
			  silentActions);
	}

}
