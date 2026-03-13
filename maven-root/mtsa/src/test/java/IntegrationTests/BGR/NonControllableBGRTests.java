package IntegrationTests.BGR;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import IntegrationTests.LTSTestsUtils;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@RunWith(Parameterized.class)
public class NonControllableBGRTests {

    private static final String RESOURCE_FOLDER = "BGR/Tests/NonControllable";
    private static final String FSP_NAME = "C";

    @Parameterized.Parameters
    public static List<File> controllerFiles() {
        return LTSTestsUtils.getFiles(RESOURCE_FOLDER);
    }

    private final File ltsFile;
    public NonControllableBGRTests(File ltsFile) {
        this.ltsFile = ltsFile;
    }

    @Test(expected = LTSCompositionException.class)
    public void Test() throws Exception {
        FileInput lts = new FileInput(ltsFile);
        LTSOutput output = new TestLTSOuput();

        // Compilation and composition
        LTSCompiler compiled = new LTSCompiler(lts, output, ".");
        compiled.compile();
        CompositeState compositeStateC = compiled.continueCompilation(FSP_NAME);
        TransitionSystemDispatcher.applyComposition(compositeStateC, output);

    }
}
