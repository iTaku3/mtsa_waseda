package ltsa.lts;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import IntegrationTests.LTSTestsUtils;
import junit.framework.TestCase;
import ltsa.dispatcher.TransitionSystemDispatcher;

public class CompositionRecursionTest extends TestCase {

	@Test
	public void testRecursiveCompositionShouldRaiseException() throws Exception {
		String resourceFolder = "./Composition";
		String fileInput = "fail.lts";
		File ltsFile = LTSTestsUtils.getFile(resourceFolder, fileInput);
		LTSTestsUtils myUtils = new LTSTestsUtils(ltsFile);

		FileInput lts = new FileInput(ltsFile);
		try {
			LTSOutput output = new TestLTSOuput();
			LTSCompiler compiled = myUtils.compileWithLtsCompiler(lts, output);
			CompositeState compositeState = compiled.continueCompilation("C");
			compositeState.setErrorTrace(new ArrayList<String>());

			TransitionSystemDispatcher.applyComposition(compositeState, output);

			// if no exception raised, fail
			fail("No exception raised");

		} catch (LTSException e) {
			if (!e.getLocalizedMessage().contains("recursive expression while parallel composing"))
				fail("Other message: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testNestedRecursiveCompositionShouldRaiseException() throws Exception {
		String resourceFolder = "./Composition";
		String fileInput = "fail_nested.lts";
		File ltsFile = LTSTestsUtils.getFile(resourceFolder, fileInput);
		LTSTestsUtils myUtils = new LTSTestsUtils(ltsFile);

		FileInput lts = new FileInput(ltsFile);
		try {
			LTSOutput output = new TestLTSOuput();
			LTSCompiler compiled = myUtils.compileWithLtsCompiler(lts, output);
			CompositeState compositeState = compiled.continueCompilation("C");
			compositeState.setErrorTrace(new ArrayList<String>());

			TransitionSystemDispatcher.applyComposition(compositeState, output);

			// if no exception raised, fail
			fail("No exception raised");

		} catch (LTSException e) {
			if (!e.getLocalizedMessage().contains("recursive expression while parallel composing (2)"))
				fail("Other message: " + e.getLocalizedMessage());
		}
	}


	
}
