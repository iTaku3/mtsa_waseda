package IntegrationTests;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.CompositeState;
import ltsa.lts.FileInput;
import ltsa.lts.LTSCompiler;
import ltsa.lts.LTSOutput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static MTSTools.ac.ic.doc.mtstools.model.SemanticType.STRONG;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TestNonBlockingDCS {

    private static final String[] RESOURCE_FOLDERS = {"NonBlocking/ControllableFSPs","NonBlocking/NoControllableFSPs"};
    private static final String FSP_NAME = "C";
    private File ltsFile;


    public TestNonBlockingDCS(File getFile) {
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
        if (parent.equals("ControllableFSPs")){
            assertTrue("The FSP is controllable", isControllable);
        } else{
            assertFalse("The FSP is not controllable", isControllable);
        }
    }

}


