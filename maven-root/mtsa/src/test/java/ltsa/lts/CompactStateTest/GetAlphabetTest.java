package ltsa.lts.CompactStateTest;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import IntegrationTests.LTSTestsUtils;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.CompositeState;
import ltsa.lts.FileInput;
import ltsa.lts.LTSCompiler;
import ltsa.lts.LTSOutput;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class GetAlphabetTest {

    private String resourceFolder = "./Alphabet";
    private static LTSTestsUtils myUtils;

    private void assertFileWithAlphabetSolution(String fileInput, String[] solution) throws IOException {
        File ltsFile = LTSTestsUtils.getFile(resourceFolder, fileInput);
        myUtils = new LTSTestsUtils(ltsFile);

        FileInput lts = new FileInput(ltsFile);
        System.out.println("Start Testing file:" + ltsFile.getName());

        LTSOutput output = new TestLTSOuput();
        LTSCompiler compiled = myUtils.compileWithLtsCompiler(lts, output);
        CompositeState compositeState = compiled.continueCompilation("C"); //if no C exists then "Default" will be use
        TransitionSystemDispatcher.applyComposition(compositeState, output);

        String[] alphabet = compositeState.getComposition().getAlphabet();

        Assert.assertTrue("Solution is wrong: " + printAllAlphabet(alphabet), Arrays.equals(alphabet, solution));
    }


    String printAllAlphabet(String[] alphabet) {
        String res = new String();
        for (String element : alphabet) {
            res = res + element + " ";
        }
        return res;
    }

    @Test
    public void getLTSAlphabet() throws Exception {
        String[] solution = new String[]{"a", "b", "c", "d"};
        assertFileWithAlphabetSolution("lts_alphabet.lts", solution);
    }

    @Test
    public void getLTSAlphabetExpanded() throws Exception {
        String[] solution = new String[]{"a", "b", "c", "d", "added"};
        assertFileWithAlphabetSolution("lts_alphabet_expand.lts", solution);
    }

    @Test
    public void getLTSAlphabetHide() throws Exception {
        String[] solution = new String[]{"a", "b", "d"};
        assertFileWithAlphabetSolution("lts_alphabet_hiding.lts", solution);
    }

    @Test
    public void getMTSAlphabet() throws Exception {
        String[] solution = new String[]{"a", "b", "c", "d"};
        assertFileWithAlphabetSolution("mts_alphabet.lts", solution);
    }

    @Test
    public void getMTSAlphabetHide() throws Exception {
        String[] solution = new String[]{"a", "b", "d"};
        assertFileWithAlphabetSolution("mts_alphabet_hiding.lts", solution);
    }


    @Test
    public void getLTSControlledDet() throws Exception {
        String[] solution = new String[]{"s", "-1", "pressed", "e", "w", "n"};
        assertFileWithAlphabetSolution("2013-controlleddet2.lts", solution);
    }


}