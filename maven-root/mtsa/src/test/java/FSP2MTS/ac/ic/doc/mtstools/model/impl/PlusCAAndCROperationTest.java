package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import ltsa.lts.CompositeState;
import ltsa.lts.Symbol;
import FSP2MTS.ac.ic.doc.mtstools.test.util.LTSATestUtils;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import ltsa.dispatcher.TransitionSystemDispatcher;

public class PlusCAAndCROperationTest extends MTSTestBase {
	
	public void testComponeConError() throws Exception {
		String sourceString = "A = (a?->A).\r\n" + 
				"		B = (a->B).\r\n" + 
				"		||AB = (A +cr B).\r\n";
		CompositeState composite = LTSATestUtils.buildAutomataFromSource(sourceString);
		composite.setCompositionType(Symbol.PLUS_CA);
		TransitionSystemDispatcher.applyComposition(composite, ltsOutput);
		
	}


}
