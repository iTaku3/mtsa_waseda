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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by mbrassesco on 6/28/17.
 */
@RunWith(Parameterized.class)
public class TestAssertion {

    private static final String ext = "lts";
    private static final String resourceFolder = "AssertionTests";
    private static LTSTestsUtils myUtils;
    private File ltsFile;


    public TestAssertion(File getFile) {
        myUtils = new LTSTestsUtils(ltsFile);
        ltsFile = getFile;
    }


    @Parameterized.Parameters(name = "{index}: {0}")
    public static List<File> controllerFiles() throws IOException {
        return LTSTestsUtils.getFiles(resourceFolder);
    }


    @Test
    public void testLTLProperty() throws Exception {
        FileInput lts = new FileInput(ltsFile);
        try {
            LTSOutput output = new TestLTSOuput();
            LTSCompiler compiled = myUtils.compileWithLtsCompiler(lts, output);
            CompositeState compositeState = compiled.continueCompilation("C");
            compositeState.setErrorTrace(new ArrayList<String>());

            //TransitionSystemDispatcher.parallelComposition(compositeState, output);
            TransitionSystemDispatcher.applyComposition(compositeState, output);
            if (!compositeState.name.contains("#")) {
                String asserted = "TESTGOAL";
                CompositeState ltlProperty = AssertDefinition.compile(output, asserted);
                CompositeState not_ltl_property = AssertDefinition.compile(new EmptyLTSOuput(), AssertDefinition.NOT_DEF + asserted);

                if (compositeState != null && ltlProperty != null) {

                    TransitionSystemDispatcher.checkFLTL(compositeState, ltlProperty,
                            not_ltl_property, false, output);
                    String result = output.toString();
                    if (result.contains("No LTL Property violations detected")) {
                        assertTrue(compositeState.getErrorTrace() == null || compositeState.getErrorTrace().isEmpty());
                    }

                    if (result.contains("Trace to property violation in TESTGOAL") || result.contains("Violation of LTL property")) {
                        fail(compositeState.getErrorTrace().toString());
                    }

                    if (result.contains("check safety")) {
                        TransitionSystemDispatcher.checkSafety(compositeState, output);
                        fail(result);
                    }

                } else {
                    fail("TESTGOAL not present in lts.");
                }

            } else {
                fail("Cannot Compile ||C");
            }
        } catch (LTSException e) {
            myUtils.catchLTSException(e);
//            myUtils.moveFilesToFailedTestsFromFSPtoFailedTests();
        }
    }

}
