package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import java.util.Collections;

import MTSTools.ac.ic.doc.mtstools.model.impl.MTSDeterminiser;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSimulationSemantics;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.LTSOutput;
import ltsa.ui.StandardOutput;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import FSP2MTS.ac.ic.doc.mtstools.test.util.LTSATestUtils;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.ac.ic.doc.mtstools.util.fsp.MTSToAutomataConverter;
import ltsa.dispatcher.TransitionSystemDispatcher;

public class MTSDeterminiserTest extends MTSTestBase {

	//A = (a->b->A | a->b->A | a->n->A | a->t->A ).
	public void test3States() throws Exception {
		this.testFor("A = (a->b->A | a->v?->A).\r\n", "B = (a->(b->B | v?->B)).\r\n");
	}

	/*public void test2States() throws Exception {
		this.testFor("A = (a?->A | a->A).\r\n", "B = (a->B).\r\n");
	}*/

	/*public void test1StepToErrorState() throws Exception {
		this.testFor("A = (a?->ERROR | a->ERROR).\r\n", "B = (a->ERROR).\r\n");
	}*/

	public void test2StepsToErrorState() throws Exception {
		this.testFor("A = (a->b->ERROR | a->v?->ERROR).\r\n", "B = (a->(b->ERROR | v?->ERROR)).\r\n");
	}

	/*public void testDeadlockState() throws Exception {
		this.testFor("A=(a->STOP | a->b->A).\r\n deterministic ||AA = A.\r\n", "AA", "A=(a->b->A)+{b}.\r\n", "DEFAULT", false);
	}*/

	protected void testFor(String sourceString, String sourceModel, String expectedSourceString, String expectedModel, boolean forkSemantics) throws Exception {
		LTSOutput ltsOutput = new TestLTSOuput();
		CompositeState composite = LTSATestUtils.buildAutomataFromSource(sourceString, sourceModel);
		MTS<Long, String> result;
		if (!forkSemantics) {
			TransitionSystemDispatcher.parallelComposition(composite, ltsOutput);
			MTS<Long, String> toDet = AutomataToMTSConverter.getInstance().convert(composite.getComposition());
			MTS<Long, String> deterministic = new MTSDeterminiser(toDet, false).determinize();
			CompactState detAutomata = MTSToAutomataConverter.getInstance().convert(deterministic, composite.getName());
			result = AutomataToMTSConverter.getInstance().convert(detAutomata);
			
		} else {
			TransitionSystemDispatcher.determinise(composite, ltsOutput);
			CompactState composition = composite.getComposition();
			result = AutomataToMTSConverter.getInstance().convert(composition);
		}
		
		CompositeState expectedComposite = LTSATestUtils.buildAutomataFromSource(expectedSourceString, expectedModel);
		TransitionSystemDispatcher.applyComposition(expectedComposite, ltsOutput);
		CompactState expectedComposition = expectedComposite.getComposition();
		
		MTS<Long, String> expected = AutomataToMTSConverter.getInstance().convert(expectedComposition);

		assertEquals(expected.getActions(), result.getActions());
		
		WeakSimulationSemantics weakSimulationSemantics = new WeakSimulationSemantics(Collections.emptySet());

		boolean refinement = TransitionSystemDispatcher.isRefinement(result, " original ", expected, " determinised ", weakSimulationSemantics, new StandardOutput());

		assertTrue(refinement);

		refinement = TransitionSystemDispatcher.isRefinement(expected, " determinised ", result, " original ", weakSimulationSemantics, new StandardOutput());

		assertTrue(refinement);
		
	}

	private void testFor(String sourceString, String expectedSourceString) throws Exception {
		testFor(sourceString, "DEFAULT", expectedSourceString, "DEFAULT", true);
	}
}
