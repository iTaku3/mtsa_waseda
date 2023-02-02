package IntegrationTests;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.fail;

/**
 * Created by mbrassesco on 6/28/17.
 */
public class LTSTestsUtils {


    private static File ltsFile;


    public LTSTestsUtils(File input) {
        ltsFile = input;
    }

    public LTSCompiler compileWithLtsCompiler(FileInput lts, LTSOutput out) throws IOException {
        LTSCompiler compiled = new LTSCompiler(lts, out, ".");
        compiled.compile();
        return compiled;
    }

    /**
     * @param expectController false: no controller expected. true: controller expected.
     * @param compiled         Compiler with all compositions to be compile
     * @param allComposites    List of names of compositions that need to be verified for compilation
     * @return expectedController
     */
    public boolean existsExpectedController(boolean expectController, LTSCompiler compiled, List<String> allComposites) {
        LTSOutput output = new TestLTSOuput();
        for (int i = 0; i < allComposites.size(); i++) {
            try {
                CompositeState compositeState = compiled.continueCompilation(allComposites.get(i));
                TransitionSystemDispatcher.applyComposition(compositeState, output);
                expectController = expectController || !compositeState.name.contains("#");
            } catch (LTSCompositionException e) {
                System.out.println("LTSCompositionException skipped.");
            }
            
            
        }
        return expectController;
    }

    /**
     * @param compiled LTS Compiler to extract composites
     * @return List of names (strings) of all composites
     */
    public List<String> getComposites(LTSCompiler compiled) {
        List<String> allComposites = compiled.getCompositesNames();
        if (allComposites.isEmpty())
            allComposites.add("DEFAULT");
        return allComposites;
    }

    public void catchLTSException(LTSException e) {
        String message = e.getMessage() + ", file: " + ltsFile.getName();
        if (e.getLocalizedMessage().contains("process identifier expected"))
            fail("Doesn't have Expected Machine(s): " + message);
        else if (e.getMessage().contains("definition not found"))
            fail("Cannot compile: " + message);
        else
            fail("Other error: " + message);
    }

    public static boolean isLTSExtension(File fileEntry) {
        return Objects.equals(FilenameUtils.getExtension(String.valueOf(fileEntry)), "lts");
    }

    /**
     * Method to move current file under test to "failedTest" folder in test resources
     */
    public void moveFilesToFailedTestsFromFSPtoFailedTests() {
        //File failedTest = new File((TestLTSCompose.class.getClassLoader().getResource(".")).getFile().concat("/failedTest/"));
//        if (!failedTest.exists()) {
//            failedTest.mkdir();

        try {
            String failedTest = new File(".").getCanonicalPath() + "/src/test/resources/failedTest/";
            File failedFile = new File(new File(".").getCanonicalPath() + "/src/test/resources/FSP/" + ltsFile.getName());
            if (ltsFile.exists()) {
                String structure = getDirectoryStructure(ltsFile);
                FileUtils.copyFileToDirectory(ltsFile, new File(failedTest + structure), true);
                failedFile.delete();
            }
        } catch (IOException e) {
            System.out.println("Cannot move failed files." + ltsFile.getName());
        } catch (NullPointerException a) {
            System.out.println("Cannot move failed files." + ltsFile.getName());
        }

    }

    /**
     * @param theFile parent folder to analyse its internal Diretory Structure
     * @return String with all structure
     */
    String getDirectoryStructure(File theFile) {
        File parent = theFile.getParentFile();
        String beginning = parent.getAbsolutePath();

        if (!beginning.contains("FSP"))
            new RuntimeException("Not a Test Examples folder");

        while (!beginning.endsWith("FSP")) {
            beginning = new File(beginning).getParent();
        }
        return parent.getAbsolutePath().replace(beginning, "");
    }

    /**
     * Method to move current file under test to "failedTest" folder in test resources
     */
    public void moveSuccessfullTest() {
        //File failedTest = new File((TestLTSCompose.class.getClassLoader().getResource(".")).getFile().concat("/failedTest/"));
        try {
            String ltsaPath = new File(".").getCanonicalPath() + "/src/test/resources/Yes/";
            if (ltsFile.exists()) {
                String structure = getDirectoryStructure(ltsFile);
                File failedFile = new File(new File(".").getCanonicalPath() + "/src/test/resources/FSP/" + structure + ltsFile.getName());
                FileUtils.copyFileToDirectory(ltsFile, new File(ltsaPath + structure), true);
                failedFile.delete();
            }


        } catch (IOException e) {
            System.out.println("Cannot move failed files." + ltsFile.getName());
        } catch (NullPointerException a) {
            System.out.println("Cannot move failed files." + ltsFile.getName());
        }

    }

    public static void recursiveFileListing(File origin, List allFiles) {
        for (File fileEntry : origin.listFiles()
                ) {
            if (fileEntry.isFile() && isLTSExtension(fileEntry)) {
                allFiles.add(fileEntry);
            } else if (fileEntry.isDirectory()) {
                recursiveFileListing(fileEntry, allFiles);
            }
        }
    }

    public static List<File> getFiles(String resourceFolder) {
        URL resource = TestLTSCompose.class.getClassLoader().getResource(resourceFolder);
        List allFiles = new ArrayList();
        try {
            String configPath = URLDecoder.decode(resource.getFile(), "UTF-8");
            File origin = new File(configPath);
            recursiveFileListing(origin, allFiles);
        } catch (UnsupportedEncodingException e) {
            return allFiles;
        }
        Collections.sort(allFiles);

        return allFiles;
    }

    public static File getFile(String resourceFolder, String filename) {
        URL a = TestLTSCompose.class.getClassLoader().getResource(resourceFolder + "/" + filename);
        File result = new File(a.getFile());

        return result;
    }
}
