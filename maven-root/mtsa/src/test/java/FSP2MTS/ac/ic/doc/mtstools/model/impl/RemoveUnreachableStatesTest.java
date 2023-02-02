package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestUtils;
import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;

public class RemoveUnreachableStatesTest extends MTSTestBase {

	public void testRemoveStates() throws Exception {
		String sourceString = "pessimistic A = (a->b->A | a?->A2), A2 = (c->v->g->A | m->A2).\r\n"; 
		MTS<Long, String> mts = MTSTestUtils.buildMTSFrom(sourceString, new TestLTSOuput());
		assertNotNull(mts);
		assertFalse(mts.getTransitions(mts.getInitialState(), TransitionType.POSSIBLE).isEmpty());
	}
}
