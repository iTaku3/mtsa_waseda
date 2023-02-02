package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.SemanticType;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestUtils;


public class WeakAlphabetRefinement extends MTSTestBase {
	
	
	public void testWeakAlphabet1() throws Exception {
		String sourceString = "M = ( m -> STOP | l -> STOP | _tau? -> l -> STOP)\\{_tau}.\r\n";
		MTS<Long, String> mtsM = MTSTestUtils.buildMTSFrom(sourceString, ltsOutput);
		sourceString = "I = ( m -> STOP | a -> l -> STOP).\r\n";
		MTS<Long, String> mtsI = MTSTestUtils.buildMTSFrom(sourceString, ltsOutput);
		
		assertTrue(SemanticType.WEAK_ALPHABET.getRefinement().isARefinement(mtsM, mtsI));
		
	}

}
