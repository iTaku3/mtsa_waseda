package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Set;

public class WeakSimulationSemantics extends BaseSemanticsByRelation {

	public WeakSimulationSemantics(Set<?> silentActions) {
		super(new FixedPointRelationConstructor(
				new SimulationChain()
					.add(new WeakForwardSimulation(silentActions))),
			  silentActions);
	}

}
