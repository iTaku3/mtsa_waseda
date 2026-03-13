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
import java.util.List;

import static MTSTools.ac.ic.doc.mtstools.model.SemanticType.STRONG;
import static org.junit.Assert.assertTrue;

/**
 * Created by mbrassesco on 6/28/17.
 */
@RunWith(Parameterized.class)
public class TestBisimilarFSPs {

    private static final String resourceFolder = "./BisimilarFSP";

    private File ltsFile;


    public TestBisimilarFSPs(File getFile) {
        ltsFile = getFile;

    }


    @Parameterized.Parameters(name = "{index}: {0}")
    public static List<File> controllerFiles() throws IOException {
        List<File> allFiles = LTSTestsUtils.getFiles(resourceFolder);
        return allFiles;
    }


    @Test
    public void testLTSBisimilar() throws Exception {
        FileInput lts = new FileInput(ltsFile);
        LTSOutput output = new TestLTSOuput();
        LTSCompiler compiled = new LTSCompiler(lts, output, ".");
        compiled.compile();

        CompositeState compositeStateC = compiled.continueCompilation("C");
        CompositeState compositeStateExp = compiled.continueCompilation("ExpectedC");
        TransitionSystemDispatcher.applyComposition(compositeStateC, output);
        TransitionSystemDispatcher.applyComposition(compositeStateExp, output);

        boolean areRefinement = TransitionSystemDispatcher
                .isRefinement(compositeStateC.composition,
                        compositeStateExp.composition, STRONG , output);


        assertTrue(areRefinement);
        //fail("No bisimilarity Controller Specs to synthesise, file: " + ltsFile.getName());
    }

}



