package FSP2MTS.ac.ic.doc.mtstools.facade;

import java.util.Collections;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSemantics;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;

public class MTSAFacadeTest {//extends MTSTestBase {
	
	private static final Long INITIAL_STATE = 0L;

	/*public void testOptimisticModel() throws Exception {

		MTS<Long, String> mts = new MTSImpl<Long, String>(INITIAL_STATE);
		mts.addAction(A_ACTION);
		mts.addAction(B_ACTION);
		mts.addState(ESTADO_CERO);
		mts.addState(ESTADO_UNO);
		mts.addTransition(ESTADO_CERO, A_ACTION, ESTADO_UNO, TransitionType.MAYBE);
		mts.addTransition(ESTADO_CERO, B_ACTION, ESTADO_CERO, TransitionType.REQUIRED);
		
		MTS<Long, String> optimistic = MTSAFacade.getOptimisticModel(mts);
		
		WeakSemantics strong = new WeakSemantics(Collections.EMPTY_SET);
		
		assertTrue(strong.isARefinement(mts, optimistic));
		assertEquals(mts.getStates(), optimistic.getStates());
		
	}*/
	
	/*public void testPesimisticModel() throws Exception {

		MTS<Long, String> mts = new MTSImpl<Long, String>(INITIAL_STATE);
		mts.addAction(A_ACTION);
		mts.addAction(B_ACTION);
		mts.addState(ESTADO_CERO);
		mts.addState(ESTADO_UNO);
		mts.addTransition(ESTADO_CERO, A_ACTION, ESTADO_CERO, TransitionType.REQUIRED);
		
		MTS<Long, String> pesimistic = MTSAFacade.getPesimisticModel(mts);
		WeakSemantics strong = new WeakSemantics(Collections.EMPTY_SET);
		assertTrue(strong.isARefinement(mts, pesimistic));
		assertEquals(0, pesimistic.getTransitions(TransitionType.MAYBE).size());
		
	}*/
}
