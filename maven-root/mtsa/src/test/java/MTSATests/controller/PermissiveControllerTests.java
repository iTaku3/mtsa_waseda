package MTSATests.controller;

import static MTSAClient.ac.ic.doc.mtsa.MTSCompiler.getInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import ltsa.lts.CompositeState;

import org.junit.Test;

import MTSATests.util.TestConstants;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.control.util.ControlConstants;

public class PermissiveControllerTests {

	@Test
	public void testPermissive1() throws Exception {
		TestLTSOuput testLTSOuput = new TestLTSOuput();
		CompositeState model = getInstance().compileCompositeState("PERMISSIVE", TestConstants.fileFrom("permissive-controller.lts"), testLTSOuput);
		String name = model.getComposition().name;
		assertTrue("There is no controller for C", !name.contains(ControlConstants.NO_CONTROLLER));
		MTS<Long, String> permissive = AutomataToMTSConverter.getInstance().convert(model.composition);
		String permissiveAction = "#w#_c";
		assertTrue(permissive.getActions().contains(permissiveAction));
		assertFalse(permissive.getTransitions(permissive.getInitialState(), TransitionType.REQUIRED).getImage(permissiveAction).isEmpty());
	}

	@Test
	public void testNotPermissive1() throws Exception {
		TestLTSOuput testLTSOuput = new TestLTSOuput();
		CompositeState model = getInstance().compileCompositeState("C", TestConstants.fileFrom("permissive-controller.lts"), testLTSOuput);
		String name = model.getComposition().name;
		assertTrue("There is no controller for C", !name.contains(ControlConstants.NO_CONTROLLER));
		MTS<Long, String> permissive = AutomataToMTSConverter.getInstance().convert(model.composition);
		String permissiveAction = "#w#_c";
		assertFalse(permissive.getActions().contains(permissiveAction));
		assertTrue(permissive.getTransitions(permissive.getInitialState(), TransitionType.REQUIRED).getImage(permissiveAction).isEmpty());
		assertTrue(permissive.getActions().contains("c"));
		assertTrue(permissive.getTransitions(permissive.getInitialState(), TransitionType.REQUIRED).getImage("c").isEmpty());
	}

	@Test
	public void testPermissive2() throws Exception {
		TestLTSOuput testLTSOuput = new TestLTSOuput();
		CompositeState model = getInstance().compileCompositeState("PERMISSIVE", TestConstants.fileFrom("permissive-controller2.lts"), testLTSOuput);
		String name = model.getComposition().name;
		assertTrue("There is no controller for C", !name.contains(ControlConstants.NO_CONTROLLER));
		MTS<Long, String> permissive = AutomataToMTSConverter.getInstance().convert(model.composition);
		String permissiveAction = "#w#_c";
		assertTrue(permissive.getActions().contains(permissiveAction));
		assertFalse(permissive.getTransitions(permissive.getInitialState(), TransitionType.REQUIRED).getImage(permissiveAction).isEmpty());
	}
}
