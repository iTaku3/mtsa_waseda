package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.util.MTSUtils;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

import org.junit.Ignore;

import FSP2MTS.ac.ic.doc.mtstools.test.util.LTSATestUtils;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;

public class MTSPropertyTest extends MTSTestBase { 
	
	@Ignore
	public void testPropertyA() throws Exception {
//		String sourceString = "property A = (a->A)+{b,c}.\r\n";
//		CompositeState buildCompositeState = LTSATestUtils.buildCompositeState(sourceString, new TestLTSOuput());
//		MTS<?, ?> mts = AutomataToMTSConverter.getInstance().convert((CompactState) buildCompositeState.getComposition());
//		System.out.println(mts);
//		boolean isMTS = MTSUtils.isMTSRepresentation(buildCompositeState);
//		assertFalse(isMTS);
	}
	
}
