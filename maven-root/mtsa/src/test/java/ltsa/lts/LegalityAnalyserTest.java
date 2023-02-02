package ltsa.lts;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import IntegrationTests.LTSTestsUtils;
import junit.framework.TestCase;
import ltsa.dispatcher.TransitionSystemDispatcher;

public class LegalityAnalyserTest extends TestCase{

	@Test
	public void testAnalyser() throws IOException {
		String resourceFolder = "./Legality";
		String fileInput = "Factory.lts";
        File ltsFile = LTSTestsUtils.getFile(resourceFolder, fileInput);
        LTSTestsUtils myUtils = new LTSTestsUtils(ltsFile);
        FileInput lts = new FileInput(ltsFile);

        LTSOutput output = new TestLTSOuput();
        LTSCompiler compiled = myUtils.compileWithLtsCompiler(lts, output);
        CompositeState compositeState = compiled.continueCompilation("C"); //if no C exists then "Default" will be use
        TransitionSystemDispatcher.applyComposition(compositeState, output);
        		
        Vector<String> controlledActions = new Vector<String>(Arrays.asList("makeA", "ready"));
		LegalityAnalyser a = new LegalityAnalyser(compositeState, output, "MAKE_A", "MAKE_B", controlledActions);

		a.composeNoHide();
		
		assertTrue(a.isLegal());
	}


	@Test
	public void testAnalyserNotLegal() throws IOException {
		String resourceFolder = "./Legality";
		String fileInput = "NotLegal.lts";
        File ltsFile = LTSTestsUtils.getFile(resourceFolder, fileInput);
        LTSTestsUtils myUtils = new LTSTestsUtils(ltsFile);
        FileInput lts = new FileInput(ltsFile);
        System.out.println("Start Testing file:" + ltsFile.getName());

        LTSOutput output = new TestLTSOuput();
        LTSCompiler compiled = myUtils.compileWithLtsCompiler(lts, output);
        CompositeState compositeState = compiled.continueCompilation("C"); //if no C exists then "Default" will be use
        TransitionSystemDispatcher.applyComposition(compositeState, output);
        		
        Vector<String> controlledActions = new Vector<String>(Arrays.asList("makeA", "ready"));
        LegalityAnalyser a = new LegalityAnalyser(compositeState, output, 0, 1, controlledActions);
		
        a.composeNoHide();
		
		assertFalse(a.isLegal());
	}	

	@Test
	public void testAnotherAnalyserNotLegal() throws IOException {
		String resourceFolder = "./Legality";
		String fileInput = "NotLegal2.lts";
        File ltsFile = LTSTestsUtils.getFile(resourceFolder, fileInput);
        LTSTestsUtils myUtils = new LTSTestsUtils(ltsFile);
        FileInput lts = new FileInput(ltsFile);
        System.out.println("Start Testing file:" + ltsFile.getName());

        LTSOutput output = new TestLTSOuput();
        LTSCompiler compiled = myUtils.compileWithLtsCompiler(lts, output);
        CompositeState compositeState = compiled.continueCompilation("C"); //if no C exists then "Default" will be use
        TransitionSystemDispatcher.applyComposition(compositeState, output);
        		
        Vector<String> controlledActions = new Vector<String>(Arrays.asList("a"));
        LegalityAnalyser a = new LegalityAnalyser(compositeState, output, "A", "B", controlledActions);
		
        a.composeNoHide();
		
		assertFalse(a.isLegal());
	}	
	


}
