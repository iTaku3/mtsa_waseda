package FSP2MTS.ac.ic.doc.mtstools.model.operations;

import java.util.Collections;

import MTSTools.ac.ic.doc.mtstools.model.operations.MergeBuilder;
import org.junit.Test;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.Refinement;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSemantics;
import MTSTools.ac.ic.doc.mtstools.model.operations.impl.WeakAlphabetMergeBuilder;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestUtils;

public class MergeBuilderTest extends MTSTestBase {

	protected Refinement refinement = new WeakSemantics(Collections.EMPTY_SET);
	protected MergeBuilder mergeBuilder = new WeakAlphabetMergeBuilder(Collections.EMPTY_SET);
	

	@Test
	public void testMerge0() throws Exception {
		String fsmA = "A_0 = ( x -> A_0_1 | x -> A_0_2), A_0_1 = ( a? -> A_0_1 | b? -> A_0_1), A_0_2 = ( c? -> A_0_2 | d? -> A_0_2).\r\n";	
		String fsmB = "B_0 = ( x -> B_0_1 | x -> B_0_2), B_0_1 = ( a? -> B_0_1 | d? -> B_0_1), B_0_2 = ( c? -> B_0_2 | b? -> B_0_2).\r\n";
		this.doMergeTest(fsmA, fsmB, null);
	}

	public void testMergeC2() throws Exception {
		String fsmA = "CA_2 = ( x? -> A_2_1 | x -> A_2_2),A_2_1 = ( a? -> A_2_1),A_2_2 = ( b? -> A_2_2 | c? -> A_2_2).\r\n";
		String fsmB = "CB_2 = ( x? -> B_2_1 | x -> B_2_2),B_2_1 = ( b? -> B_2_1 ),B_2_2 = ( a? -> B_2_2 | b? -> B_2_2 | c? -> B_2_2).\r\n";
		String fsmLCR = "CLCR_2 = ( x? -> STOP | x? -> LCR_2_1_2 | x? -> LCR_2_2_1 | x -> LCR_2_2_2 )," + 
						"LCR_2_1_2 = ( a? -> LCR_2_1_2 ), " +
						"LCR_2_2_1 = ( b? -> LCR_2_2_1 ), " + 
						"LCR_2_2_2 = ( b? -> LCR_2_2_2 | c? -> LCR_2_2_2 ).\r\n";
		
		this.doMergeTest(fsmA, fsmB, fsmLCR);
	}
	
	public void testMergeC3() throws Exception {
		String fsmA = "CA_3 = ( x? -> A_1 | x -> A_2),A_1 = (a -> A_1),A_2 = (a? -> A_2).\r\n";		
		
		this.doMergeTest(fsmA, fsmA, fsmA);
	}

	public void testMergeC4() throws Exception {
		String fsmA = "CA_4 = ( x? -> A_1 | x -> A_2),A_1 = (a? -> A_1 | b? -> A_1 | c? -> A_1),A_2 = (a? -> A_2 | b? -> A_2).\r\n";		
		String fsmB = "CB_4 = ( x? -> B_1 | x -> B_2),B_1 = (a? -> B_1 | b? -> B_1 | c? -> B_1),B_2 = (b? -> B_2 | c? -> B_2).	\r\n";
		String fsmLCR = "CLCR_4 = ( x? -> LCR_2_1_1 | x? -> LCR_2_1_2 | x? -> LCR_2_2_1 | x -> LCR_2_2_2 )," + 
						"LCR_2_1_1 = ( a? -> LCR_2_1_1 | b? -> LCR_2_1_1 | c? -> LCR_2_1_1  ), " +
						"LCR_2_1_2 = ( b? -> LCR_2_1_2 | c? -> LCR_2_1_2 ), " +
						"LCR_2_2_1 = ( a? -> LCR_2_2_1 | b? -> LCR_2_2_1 ), " + 
						"LCR_2_2_2 = ( b? -> LCR_2_2_2 ).\r\n";
		
		this.doMergeTest(fsmA, fsmB, fsmLCR);
	}
	
	public void testMergeC5() throws Exception {
		String fsmA = "CA_5 = ( x? -> A_1 | x -> A_2),A_1 = (a? -> A_1 | b -> A_1 | c? -> A_1),A_2 = (a? -> A_2 | b? -> A_2).\r\n";		
		String fsmB = "CB_5 = ( x? -> B_1 | x -> B_2),B_1 = (a? -> B_1 | b? -> B_1 | c? -> B_1),B_2 = (b? -> B_2 | c? -> B_2).\r\n";
		
		this.doMergeTest(fsmA, fsmB, null);
	}
	
	@Test
	public void testMerge8() throws Exception {
		String fsmA = "A_8 = ( x? -> ( y? -> A_8_2 | y -> A_8_3)),A_8_2 = ( a? -> STOP | b? -> STOP),A_8_3 = ( c? -> STOP | d? -> STOP).\r\n";
		String fsmB = "B_8 = ( x? -> ( y? -> B_8_2 | y -> B_8_3)),B_8_2 = ( a? -> STOP | d? -> STOP),B_8_3 = ( b? -> STOP | c? -> STOP).\r\n";
		String fsmLCR = "LCR_8 = ( x? -> ( y? -> LCR_8_2_2 | y -> LCR_8_2_3 | y -> LCR_8_3_2 | y? -> LCR_8_3_3 ) " + 
						"|  x? -> ( y? -> LCR_8_2_2 | y? -> LCR_8_2_3 | y? -> LCR_8_3_2 | y -> LCR_8_3_3 )), " +
						"LCR_8_2_2 = ( a? -> STOP ), " +
						"LCR_8_2_3 = ( b? -> STOP ), " +
						"LCR_8_3_2 = ( d? -> STOP ), " + 
						"LCR_8_3_3 = ( c? -> STOP ).\r\n";
		
		this.doMergeTest(fsmA, fsmB, fsmLCR);
		
	}
	
	private void doMergeTest(String fsmA, String fsmB, String fsmMergeLowerBound) throws Exception {		
		MTS<Long, String> mtsA = MTSTestUtils.buildMTSFrom(fsmA, ltsOutput);
		
		MTS<Long, String> mtsB = MTSTestUtils.buildMTSFrom(fsmB, ltsOutput);
			
		MTS<?,String> merge = this.mergeBuilder.merge(mtsA, mtsB);
		
		assertTrue(refinement.isARefinement(mtsA, merge));
		assertTrue(refinement.isARefinement(mtsB, merge));
		
		if (fsmMergeLowerBound != null) {
			MTS<Long, String> mtsLB = MTSTestUtils.buildMTSFrom(fsmMergeLowerBound, ltsOutput);
					
			assertTrue(refinement.isARefinement(merge, mtsLB));
		}
				
	}
	
	
		
}
