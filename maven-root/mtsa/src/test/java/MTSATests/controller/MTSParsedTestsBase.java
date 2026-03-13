package MTSATests.controller;


import java.util.Collections;

import junit.framework.TestCase;
import ltsa.lts.CompositeState;
import ltsa.ui.StandardOutput;
import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSimulationSemantics;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import MTSSynthesis.controller.model.ControllerGoal;
import ltsa.dispatcher.TransitionSystemDispatcher;

public abstract class MTSParsedTestsBase extends TestCase {

	protected abstract void setControllableActions(ControllerGoal<String> goal);

	protected abstract void setAssumeAndGuarantee(ControllerGoal<String> goal);

	protected abstract String getInputModel() throws Exception;

	protected MTS<Long, String> synthesiseInput(String modelName) throws Exception {
		
		CompositeState c = MTSCompiler.getInstance().compileCompositeState(modelName, getInputModel());
		MTS<Long, String> mts = MTSCompiler.getInstance().compileMTS(modelName, getInputModel());

		c.goal = this.buildGoal();
		TransitionSystemDispatcher.synthesise(c, new StandardOutput());

		MTS<Long, String> synMTS = AutomataToMTSConverter.getInstance().convert(c.getComposition());

		System.out.println(synMTS);

		for (Long state : synMTS.getStates()) {
			assertNotNull(synMTS.getTransitions(state, TransitionType.REQUIRED));
		}
		
		WeakSimulationSemantics weakSimulationSemantics = new WeakSimulationSemantics(Collections.emptySet());

		boolean refinement = TransitionSystemDispatcher.isRefinement(mts, " original ", synMTS,
				" synthesised ", weakSimulationSemantics, new StandardOutput());

		assertTrue(refinement);
		
		for (Long state : synMTS.getStates()) {
			assertTrue(synMTS.getTransitions(state, TransitionType.REQUIRED).size()>0);
		}
		
		return synMTS;
	}

	protected ControllerGoal<String> buildGoal() {
		ControllerGoal<String> goal = new ControllerGoal<String>();
		this.setControllableActions(goal);
		this.setAssumeAndGuarantee(goal);
		return goal;
	}

}
