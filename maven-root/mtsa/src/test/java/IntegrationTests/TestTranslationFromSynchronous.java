package IntegrationTests;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
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

import static MTSTools.ac.ic.doc.mtstools.model.SemanticType.STRONG;
import static org.junit.Assert.*;
import static org.junit.Assert.fail;

/**
 * Created by mbrassesco on 6/28/17.
 */
@RunWith(Parameterized.class)
public class TestTranslationFromSynchronous {

    private static final String[] RESOURCE_FOLDERS = {"GR1FromReactive/Controllable","GR1FromReactive/Uncontrollable"};

    private File ltsFile;


    public TestTranslationFromSynchronous(File getFile) {
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
    public void TestBisimilarAfterTranslation() throws Exception {
        FileInput lts = new FileInput(ltsFile);
        LTSOutput output = new TestLTSOuput();
        LTSCompiler compiled = new LTSCompiler(lts, output, ".");
        compiled.compile();

        CompositeState noAsterisk = compiled.continueCompilation("WithAlphabetSet");
        CompositeState usingAsterisk = compiled.continueCompilation("WithAsterisk");
        TransitionSystemDispatcher.applyComposition(noAsterisk, output);
        TransitionSystemDispatcher.applyComposition(usingAsterisk, output);

        String parent = ltsFile.getParentFile().getName();
//        if (parent.equals("Controllable")) {
//            boolean areRefinement = TransitionSystemDispatcher
//                    .isRefinement(noAsterisk.composition,
//                            usingAsterisk.composition, STRONG, output);
//
//            assertTrue(areRefinement);
//        }
        //End of TestBisimilarity. Begin TestBlocking for both properties

        boolean isControllable = noAsterisk.composition != null;
        assertTrue("One FSP is controllable and the other is not",
                (isControllable == (usingAsterisk.composition != null)));

        if (parent.equals("Controllable")) {
            assertTrue("The FSP is controllable", isControllable);
        } else {
            assertFalse("The FSP is not controllable", isControllable);
        }

        // Check ltl property (see TestAssertion.java)
        if (isControllable) {
            assertTrue("we are returning an empty controller",noAsterisk.composition.ntransitions()>0);

            String assertAsterisk = "CheckWith";
            String assertAlphabet = "CheckWithout";
            CompositeState ltlProperty1 = AssertDefinition.compile(output, assertAsterisk);
            CompositeState not_ltlProperty1 = AssertDefinition.compile(new EmptyLTSOuput(), AssertDefinition.NOT_DEF + assertAsterisk);
            CompositeState ltlProperty2 = AssertDefinition.compile(output, assertAlphabet);
            CompositeState not_ltlProperty2 = AssertDefinition.compile(new EmptyLTSOuput(), AssertDefinition.NOT_DEF + assertAlphabet);

            CompositeState sys1 = compiled.continueCompilation("SysWith");
            CompositeState sys2 = compiled.continueCompilation("SysWithout");

            if (ltlProperty1 != null && ltlProperty2 != null) {
                System.out.println("\n ==================Does Sys with * fulfil LTL with *? ==================\n");
                TransitionSystemDispatcher.checkFLTL(sys1, ltlProperty1,
                        not_ltlProperty1, false, output);
                System.out.println("\n ==================Does Sys with * fulfil LTL without *?==================\n");
                TransitionSystemDispatcher.checkFLTL(sys1, ltlProperty2,
                        not_ltlProperty2, false, output);
                System.out.println("\n ==================Does Sys without * fulfil LTL with *?==================\n");
                TransitionSystemDispatcher.checkFLTL(sys2, ltlProperty1,
                        not_ltlProperty1, false, output);
                System.out.println("\n ==================Does Sys without * fulfil LTL without *?==================\n");
                TransitionSystemDispatcher.checkFLTL(sys2, ltlProperty2,
                        not_ltlProperty2, false, output);
                String result = output.toString();
                if (result.contains("No LTL Property violations detected")) {
                    assertTrue((sys1.getErrorTrace() == null || sys1.getErrorTrace().isEmpty())
                              && (sys2.getErrorTrace() == null || sys2.getErrorTrace().isEmpty()));
                }

                if (result.contains("Trace to property violation in CheckWith") ||
                    result.contains("Trace to property violation in CheckWithout") ||
                    result.contains("Violation of LTL property")) {
                    fail(sys1.getErrorTrace().toString() +
                            "\n End SysWith, Begin SysWithout \n" +
                            sys1.getErrorTrace().toString());
                }

                if (result.contains("check safety")) {
                    TransitionSystemDispatcher.checkSafety(sys1, output);
                    TransitionSystemDispatcher.checkSafety(sys2, output);
                    fail(result);
                }

            } else {
                fail("CheckWith or CheckWithout not present in lts.");
            }
        }
    }

}



