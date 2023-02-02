package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import java.util.Collections;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSimulationSemantics;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.ui.StandardOutput;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.TraceInclusionClosure;
import FSP2MTS.ac.ic.doc.mtstools.test.util.LTSATestUtils;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.dispatcher.TransitionSystemDispatcher;

public class ClosureTraceInclusionTest extends MTSTestBase {
	
	/*public void test5states() throws Exception {
		
		String testModel = "M = (a?->B1 | a->B2), B1=(b->STOP | b->B3), B3 = (b->B3), B2 = (b->B3).";
		String expectedFSP = "M = (b?->STOP | b->B3), B3 = (b->B3)+{a}.";

		testTraceClousure(testModel, expectedFSP, Collections.singleton("a"));
	}*/

	public void test2StatesBisimulationWouldntMinimize() throws Exception {
		String testModel = "M = (a->B1 | c->M), B1=(b->STOP).";
		String expectedFSP = "M = (b->STOP | c->M)+{a}.";

		testTraceClousure(testModel, expectedFSP, Collections.singleton("a"));
		
	}
	
	private void testTraceClousure(String testModel, String expectedFSP, Set<String> silent)
			throws Exception {
		CompositeState aut = LTSATestUtils.buildAutomataFromSource(testModel);
		aut.compose(new StandardOutput());
		MTS<Long, String> result = AutomataToMTSConverter.getInstance().convert(aut.getComposition());
		TraceInclusionClosure.getInstance().applyMTSClosure(result, silent);
		System.out.println(result);
		
		
		CompositeState expectedComposite = LTSATestUtils.buildAutomataFromSource(expectedFSP);
		TransitionSystemDispatcher.applyComposition(expectedComposite, ltsOutput);
		CompactState expectedComposition = expectedComposite.getComposition();
		MTS<Long, String> expected = AutomataToMTSConverter.getInstance().convert(expectedComposition);

		assertEquals(expected.getActions(), result.getActions());
		
		WeakSimulationSemantics weakSimulationSemantics = new WeakSimulationSemantics(Collections.emptySet());

		boolean refinement = TransitionSystemDispatcher.isRefinement(result, " original ", expected, " closured ", weakSimulationSemantics, new StandardOutput());

		assertTrue(refinement);

		refinement = TransitionSystemDispatcher.isRefinement(expected, " closured ", result, " original ", weakSimulationSemantics, new StandardOutput());

		assertTrue(refinement);
	}
	
	
	
}
