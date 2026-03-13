package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import static FSP2MTS.ac.ic.doc.mtstools.model.LTSExamples.LTS_01;
import static FSP2MTS.ac.ic.doc.mtstools.model.LTSExamples.LTS_04;
import static FSP2MTS.ac.ic.doc.mtstools.model.LTSExamples.LTS_04_B;
import static FSP2MTS.ac.ic.doc.mtstools.model.LTSExamples.LTS_WEIRD;
import static FSP2MTS.ac.ic.doc.mtstools.model.MTSExamples.MTS_01;
import static FSP2MTS.ac.ic.doc.mtstools.model.MTSExamples.MTS_04;
import static FSP2MTS.ac.ic.doc.mtstools.model.MTSExamples.MTS_07;
import static FSP2MTS.ac.ic.doc.mtstools.model.MTSExamples.MTS_08;
import static FSP2MTS.ac.ic.doc.mtstools.model.MTSExamples.MTS_WEIRD;

import java.util.Collections;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.impl.BranchingImplementationNotion;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSemantics;
import junit.framework.TestCase;
import MTSTools.ac.ic.doc.mtstools.model.ImplementationNotion;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import FSP2MTS.ac.ic.doc.mtstools.model.MTSExamples;


public class BranchingImplementationNotionTest extends TestCase {

	private static Set<String> silentActions = Collections.singleton(MTSExamples.TAU);
	private static ImplementationNotion branching = new BranchingImplementationNotion(silentActions);
	private static ImplementationNotion branching2 = new BranchingImplementationNotion(Collections.EMPTY_SET);
	private static ImplementationNotion weak = new WeakSemantics(silentActions);
	
	public void test1(){this.doTestImplementation(branching ,MTS_01, LTS_01, true );};
	public void test2(){this.doTestImplementation(branching2,MTS_01, LTS_01, false);};
	public void test3(){this.doTestImplementation(branching ,MTS_07, LTS_01, false);};		
	public void test4(){this.doTestImplementation(branching ,MTS_08, LTS_01, true);};
	public void test5(){this.doTestImplementation(branching ,MTS_04, LTS_04, true);};
	public void test6(){this.doTestImplementation(branching ,MTS_04, LTS_04_B, false);};
	public void test7(){this.doTestImplementation(weak      ,MTS_04, LTS_04_B, true);};
	public void test8(){this.doTestImplementation(branching ,MTS_WEIRD, LTS_WEIRD, false);};
	public void test9(){this.doTestImplementation(weak      ,MTS_WEIRD, LTS_WEIRD, true);};		



	
	public <S,A> void doTestImplementation(ImplementationNotion notion, MTS<S,A> mts, LTS<S,A> lts, boolean expectedResult) {		
		assertEquals(expectedResult, notion.isAnImplementation(mts,lts));
	}

	
}
