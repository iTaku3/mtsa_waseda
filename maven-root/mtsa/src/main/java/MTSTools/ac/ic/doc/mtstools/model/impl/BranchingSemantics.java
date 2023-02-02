package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Set;

public class BranchingSemantics extends BaseSemanticsByRelation {

	public BranchingSemantics(Set<?> silentActions) {
		super(new FixedPointRelationConstructor(
				new SimulationChain()
					.add(new BranchingForwardSimulation(silentActions))
					.add(new BranchingBackwardSimulation(silentActions))),
			silentActions);
	}
	
}
