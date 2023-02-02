package MTSATests.controller;

import static MTSAClient.ac.ic.doc.mtsa.MTSCompiler.getInstance;
import static org.junit.Assert.assertTrue;
import ltsa.lts.CompositeState;
import ltsa.lts.LTSCompositionException;

import org.junit.Test;

import ltsa.control.util.ControlConstants;

import MTSATests.util.TestConstants;
import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;

public class MTSControllerSynthesisTests {
	
	@Test
	public void testAll() throws Exception {
		TestLTSOuput testLTSOuput = new TestLTSOuput();
		CompositeState model = getInstance().compileCompositeState("C_All", TestConstants.fileFrom("mts-control.lts"), testLTSOuput);
		String name = model.getComposition().name;
		assertTrue("There is no controller for C", !name.contains(ControlConstants.NO_CONTROLLER));
		assertTrue("The answer is not All", testLTSOuput.toString().contains("All implementations of C_All can be controlled"));
	}

	@Test
	public void testSome() throws Exception {
		TestLTSOuput testLTSOuput = new TestLTSOuput();
		CompositeState model = getInstance().compileCompositeState("C_Some", TestConstants.fileFrom("mts-control.lts"), testLTSOuput);
		String name = model.getComposition().name;
		assertTrue("There is no controller for C", !name.contains(ControlConstants.NO_CONTROLLER));
		assertTrue("The answer is not Some", testLTSOuput.toString().contains("Some implementations of C_Some can be controlled and some cannot."));
	}
	
	@Test(expected = LTSCompositionException.class)
	public void testNone() throws Exception {
		TestLTSOuput testLTSOuput = new TestLTSOuput();
		CompositeState model = getInstance().compileCompositeState("C_None", TestConstants.fileFrom("mts-control.lts"), testLTSOuput);		
	}

}
