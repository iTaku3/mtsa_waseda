package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.ImplementationNotion;
import MTSTools.ac.ic.doc.mtstools.model.Refinement;

public class WeakSemantics extends BaseSemanticsByRelation implements Refinement, ImplementationNotion {

	public WeakSemantics(Set<?> silentActions) {
		super(new FixedPointRelationConstructor(
				new SimulationChain()
					.add(new WeakForwardSimulation(silentActions))
					.add(new WeakBackwardSimulation(silentActions))),
			  silentActions);
	}
	
}
