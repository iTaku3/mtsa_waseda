package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import java.util.Collections;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import FSP2MTS.ac.ic.doc.mtstools.model.MTSExamples;
import MTSTools.ac.ic.doc.mtstools.model.Refinement;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSComposer;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSemantics;

public class MTSComposerTest extends MTSTestBase {
	
	public void testCompositionFrom2MTS() throws Exception {
		MTS<Long, String> mtsA = buildEmptyMTS();
		mtsA.addState(ESTADO_CERO);
		mtsA.addState(ESTADO_UNO);
		mtsA.addAction(B_ACTION);
		mtsA.addAction(C_ACTION);
		mtsA.addRequired(ESTADO_CERO, C_ACTION, ESTADO_UNO);
		mtsA.addRequired(ESTADO_UNO, B_ACTION, ESTADO_CERO);
		
		
		MTS<Long, String> mtsB = buildEmptyMTS();
		mtsB.addAction(A_ACTION);
		mtsB.addState(ESTADO_CERO);
		mtsB.addRequired(ESTADO_CERO, A_ACTION, ESTADO_CERO);
		
		MTS<Long, String> expectedComposition = buildEmptyMTS();
		expectedComposition.addAction(A_ACTION);
		expectedComposition.addAction(B_ACTION);
		expectedComposition.addAction(C_ACTION);
		expectedComposition.addState(ESTADO_CERO);
		expectedComposition.addState(ESTADO_UNO);
		expectedComposition.addRequired(ESTADO_CERO, C_ACTION, ESTADO_UNO);
		expectedComposition.addRequired(ESTADO_CERO, A_ACTION, ESTADO_CERO);
		expectedComposition.addRequired(ESTADO_UNO, B_ACTION, ESTADO_CERO);
		expectedComposition.addRequired(ESTADO_UNO, A_ACTION, ESTADO_UNO);

		
		Set<String> silentActions = Collections.singleton(MTSExamples.TAU);
		Refinement weak = new WeakSemantics(silentActions);
		
		MTS<Long, String> resultComposition = new MTSComposer().compose(mtsA, mtsB);
		assertTrue(weak.isARefinement(expectedComposition, resultComposition));
		assertTrue(weak.isARefinement(resultComposition, expectedComposition));
		
		
		
	}
}
