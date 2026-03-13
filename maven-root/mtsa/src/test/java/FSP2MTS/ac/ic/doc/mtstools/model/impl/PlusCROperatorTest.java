package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import java.util.Collections;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.Refinement;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestUtils;
import MTSTools.ac.ic.doc.mtstools.model.impl.StrongPlusCROperator;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakAlphabetPlusCROperator;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSemantics;

public class PlusCROperatorTest extends MTSTestBase{
	

	protected Refinement refinement;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.refinement = new WeakSemantics(Collections.EMPTY_SET);
	}
	
	public void testCR() throws Exception {
		String sourceString = "A_0 = ( x -> A_0_1 | x -> A_0_2), A_0_1 = ( a? -> A_0_1 | b? -> A_0_1), A_0_2 = ( c? -> A_0_2 | d? -> A_0_2).\r\n";
		MTS<Long, String> mtsA = MTSTestUtils.buildMTSFrom(sourceString, ltsOutput);
		
		sourceString = "B_0 = ( x -> B_0_1 | x -> B_0_2), B_0_1 = ( a? -> B_0_1 | d? -> B_0_1), B_0_2 = ( c? -> B_0_2 | b? -> B_0_2).\r\n";
		MTS<Long, String> mtsB = MTSTestUtils.buildMTSFrom(sourceString, ltsOutput);
		
		StrongPlusCROperator CRoperator = new StrongPlusCROperator();
		
		MTS<Pair<Long,Long>, String> cr = CRoperator.compose(mtsA, mtsB);
		
		assertTrue(refinement.isARefinement(mtsA, cr));
		assertTrue(refinement.isARefinement(mtsB, cr));
		
		WeakAlphabetPlusCROperator weakCRoperator = new WeakAlphabetPlusCROperator();
		
		cr = weakCRoperator.compose(mtsA, mtsB);
		
		assertTrue(refinement.isARefinement(mtsA, cr));
		assertTrue(refinement.isARefinement(mtsB, cr));
		
		
	}

}
