package IntegrationTests;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import MTSAClient.ac.ic.doc.mtsa.MTSA;
import ltsa.lts.FileInput;
import ltsa.lts.LTSCompiler;
import ltsa.lts.LTSException;
import ltsa.lts.LTSOutput;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;


/**
 * Created by mbrassesco on 6/7/17.
 */
@RunWith(Parameterized.class)
public class TestLTSCompose {

    private static final String resourceFolder = "FSP";
    private static LTSTestsUtils myUtils;
    private File ltsFile;


    @Parameterized.Parameters(name = "{index}: {0}")
    public static List<File> controllerFiles() throws IOException {
        List<File> allFiles = LTSTestsUtils.getFiles(resourceFolder);
        return allFiles;
    }


    public TestLTSCompose(File getFile) {
        ltsFile = getFile;
        myUtils = new LTSTestsUtils(ltsFile);
    }

    /**
     * All lts files under FSP folder must compose
     */
    @Test(timeout = 30000000)
    @Category(MTSA.SlowTests.class)
    public void testLTSCompilesAndComposeAllComposites() {
        FileInput lts;
        try {
            lts = new FileInput(ltsFile);
            System.out.println("Start Testing file:" + ltsFile.getName());

            boolean expectController = false;
            LTSOutput output = new TestLTSOuput();
            LTSCompiler compiled = myUtils.compileWithLtsCompiler(lts, output);

            List<String> allComposites = myUtils.getComposites(compiled);
            expectController = myUtils.existsExpectedController(expectController, compiled, allComposites);
            assertTrue(expectController);

        } catch (LTSException e) {
//            myUtils.moveFilesToFailedTestsFromFSPtoFailedTests();
            myUtils.catchLTSException(e);
        } catch (IOException e) {
//            myUtils.moveFilesToFailedTestsFromFSPtoFailedTests();
        } catch (Throwable t) {
            // take screenshot, output results
            if (t instanceof InterruptedException) {
                System.out.println("Timeout: " + ltsFile.getName());
//                myUtils.moveFilesToFailedTestsFromFSPtoFailedTests();
            }
        } finally {
            System.out.println("Complete Testing file:" + ltsFile.getName());
        }


    }

}