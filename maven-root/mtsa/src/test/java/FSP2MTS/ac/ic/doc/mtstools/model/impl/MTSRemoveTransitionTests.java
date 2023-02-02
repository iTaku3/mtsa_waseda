package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;

public class MTSRemoveTransitionTests extends MTSTestBase {
	public void testRemoveOneTransitionWithoutOrphans() throws Exception {
		MTS<Long, String> mts = buildBasicMTS();
		mts.removeTransition(ESTADO_CERO, A_ACTION, ESTADO_CERO, TransitionType.REQUIRED);
		assertTrue(mts.getTransitions(ESTADO_CERO, TransitionType.REQUIRED).size()==0);		
	}
	public void testRemoveOneTransitionWithTwoOrphans() throws Exception {
		MTS<Long, String> mts = buildBasicMTS();
		mts.addAction(B_ACTION);
		mts.addState(ESTADO_UNO);
		mts.addState(ESTADO_DOS);
		mts.addTransition(ESTADO_CERO, B_ACTION, ESTADO_UNO, TransitionType.REQUIRED);
		mts.addTransition(ESTADO_UNO, B_ACTION, ESTADO_DOS, TransitionType.REQUIRED);
		mts.removeTransition(ESTADO_CERO, B_ACTION, ESTADO_UNO, TransitionType.REQUIRED);
		
		assertTrue(mts.getStates().size()==3);
		assertTrue(mts.getTransitions(ESTADO_CERO, TransitionType.REQUIRED).size()==1);		
	}

	public void testRemoveOneTransitionWithOneOrphan() throws Exception {
		MTS<Long, String> mts = buildBasicMTS();
		mts.addAction(B_ACTION);
		mts.addState(ESTADO_UNO);
		mts.addState(ESTADO_DOS);
		mts.addTransition(ESTADO_CERO, B_ACTION, ESTADO_UNO, TransitionType.REQUIRED);
		mts.addTransition(ESTADO_UNO, B_ACTION, ESTADO_DOS, TransitionType.REQUIRED);
		mts.addTransition(ESTADO_CERO, B_ACTION, ESTADO_DOS, TransitionType.REQUIRED);
		mts.removeTransition(ESTADO_CERO, B_ACTION, ESTADO_UNO, TransitionType.REQUIRED);
		
		assertTrue(mts.getStates().size()==3);
		assertTrue(mts.getTransitions(ESTADO_CERO, TransitionType.REQUIRED).size()==2);
	}
}
