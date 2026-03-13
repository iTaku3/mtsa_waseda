package IntegrationTests.BGR;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import IntegrationTests.LTSTestsUtils;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.*;
import ltsa.lts.ltl.AssertDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.List;
import java.util.Vector;

import static org.testng.Assert.*;

@RunWith(Parameterized.class)
public class ControllableBGRTests {

    private static final String RESOURCE_FOLDER = "BGR/Tests/Controllable";
    private static final String FSP_NAME = "C";
    private static final String LTL_PROPERTY = "Test";


    @Parameterized.Parameters
    public static List<File> controllerFiles() {
        return LTSTestsUtils.getFiles(RESOURCE_FOLDER);
    }

    private final File ltsFile;
    public ControllableBGRTests(File ltsFile) {
        this.ltsFile = ltsFile;
    }

    @Test
    public void Test() throws Exception {
        FileInput lts = new FileInput(ltsFile);
        LTSOutput output = new TestLTSOuput();

        // Compilation and composition
        LTSCompiler compiled = new LTSCompiler(lts, output, ".");
        compiled.compile();
        CompositeState compositeStateC = compiled.continueCompilation(FSP_NAME);
        TransitionSystemDispatcher.applyComposition(compositeStateC, output);

        //assertEquals(compositeStateC.composition.getMtsControlProblemAnswer(), "ALL");

        // We create a new composite state tester
        Vector<CompactState> machines = new Vector<>();
        machines.add(compositeStateC.getComposition());
        CompositeState tester = new CompositeState(compositeStateC.name, machines);

        // The LTL property to test
        CompositeState ltlProperty = AssertDefinition.compile(output, LTL_PROPERTY);
        tester.checkLTL(output, ltlProperty);

        assertTrue(tester.getErrorTrace() == null || tester.getErrorTrace().isEmpty());
    }
}

