package MTSTools.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import java.util.Set;

public class WeakBisimulationSemantics extends BaseSemanticsByRelation {

  public WeakBisimulationSemantics(Set<?> silentActions) {
    super(
        new FixedPointRelationConstructor(
            new SimulationChain()
                .add(new WeakForwardSimulation(silentActions))
                .add(
                    new WeakBackwardSimulation(
                        silentActions, TransitionType.REQUIRED, TransitionType.REQUIRED))),
        silentActions);
  }
}
