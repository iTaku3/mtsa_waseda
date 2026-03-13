package IntegrationTests;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import java.util.Arrays;
import java.util.Vector;
import ltsa.control.ControllerGoalDefinition;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.*;
import ltsa.lts.ltl.AssertDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TestBlockingDCS {

    private static final String[] RESOURCE_FOLDERS = {"Blocking/ControllableFSPs","Blocking/NoControllableFSPs"};
    private static final String FSP_NAME = "DirectedController";
    private File ltsFile;


    public TestBlockingDCS(File getFile) {
        ltsFile = getFile;

    }


    @Parameterized.Parameters(name = "{index}: {0}")
    public static List<File> controllerFiles() throws IOException {
        List<File> allFiles = new ArrayList();
        for (String resource_folder:RESOURCE_FOLDERS) {

            allFiles.addAll(LTSTestsUtils.getFiles(resource_folder));
        }
        return allFiles;
    }


    @Test
    public void testControllability() throws Exception {
        FileInput lts = new FileInput(ltsFile);
        LTSOutput output = new TestLTSOuput();
        LTSCompiler compiled = new LTSCompiler(lts, output, ".");
        compiled.compile();

        CompositeState compositeStateC = compiled.continueCompilation(FSP_NAME);
        TransitionSystemDispatcher.applyComposition(compositeStateC, output);

        boolean isControllable = compositeStateC.composition != null;

        String parent = ltsFile.getParentFile().getName();
        if (parent.equals("ControllableFSPs")) {
            assertTrue("The FSP is controllable", isControllable);
        } else {
            assertFalse("The FSP is not controllable", isControllable);
        }

        // Check ltl property (see TestAssertion.java)
        if (isControllable) {
            assertTrue("we are returning an empty controller",compositeStateC.composition.ntransitions()>0);

            String asserted = "Check";
            CompositeState ltlProperty = AssertDefinition.compile(output, asserted);
            CompositeState not_ltlProperty = AssertDefinition.compile(new EmptyLTSOuput(), AssertDefinition.NOT_DEF + asserted);

            CompositeState sys = compiled.continueCompilation("Sys");

            if (ltlProperty != null) {

                TransitionSystemDispatcher.checkFLTL(sys, ltlProperty,
                    not_ltlProperty, false, output);
                String result = output.toString();
                if (result.contains("No LTL Property violations detected")) {
                    assertTrue(sys.getErrorTrace() == null || sys.getErrorTrace().isEmpty());
                }

                if (result.contains("Trace to property violation in Check") || result.contains("Violation of LTL property")) {
                    fail(sys.getErrorTrace().toString());
                }

                if (result.contains("check safety")) {
                    TransitionSystemDispatcher.checkSafety(sys, output);
                    fail(result);
                }


                Symbol controllerSymbol = new Symbol();
                controllerSymbol.setString("Goal");
                ControllerGoalDefinition goalDefinition = ControllerGoalDefinition
                        .getDefinition(controllerSymbol);

                LegalityAnalyser a = new LegalityAnalyser(sys, output, "DirectedController", "Plant", goalDefinition.getControllableActionSet());
                a.composeNoHide();
                assertTrue(a.isLegal());

            } else {
                fail("Check not present in lts.");
            }
        }
    }
}


