package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import java.util.Collections;

import MTSTools.ac.ic.doc.mtstools.model.impl.BaseSemanticsByRelation;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSemantics;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.LTSOutput;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import FSP2MTS.ac.ic.doc.mtstools.test.util.LTSATestUtils;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.dispatcher.TransitionSystemDispatcher;


public class RefinementBetweenStatesTest extends MTSTestBase {

	public void testBasic2StatesRefinement() throws Exception {
		String sourceString = "A = (a->C|b->RC), RC = (c->RC), C = (c?->C).\r\n";
		LTSOutput ltsOutput = new TestLTSOuput();
		CompositeState composite = LTSATestUtils.buildAutomataFromSource(sourceString);
		TransitionSystemDispatcher.parallelComposition(composite, ltsOutput);
		MTS<Long, String> mts = AutomataToMTSConverter.getInstance().convert((CompactState) composite.getComposition());
		
		BaseSemanticsByRelation weak = new WeakSemantics(Collections.EMPTY_SET);
		/*
		assertTrue(weak.isARefinement(mts, mts));

		assertFalse(weak.isARefinement(mts, mts, Long.valueOf(1), Long.valueOf(2))); 
		assertFalse(weak.isARefinement(mts, mts, Long.valueOf(0), Long.valueOf(1)));
		assertFalse(weak.isARefinement(mts, mts, Long.valueOf(0), Long.valueOf(2)));
		assertFalse(weak.isARefinement(mts, mts, Long.valueOf(1), Long.valueOf(0)));
		assertFalse(weak.isARefinement(mts, mts, Long.valueOf(2), Long.valueOf(0)));
*/
	}
	
}
