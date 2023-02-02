package ltsa.lts;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import IntegrationTests.LTSTestsUtils;
import junit.framework.TestCase;
import ltsa.lts.CompositionExpression;
import ltsa.lts.FileInput;
import ltsa.lts.LTSCompiler;
import ltsa.lts.LTSOutput;
import ltsa.lts.LabelSet;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PriorityActionSetTest extends TestCase{
    /*
     * From issue: https://bitbucket.org/lnahabedian/mtsa/issues/15/prioritization-does-not-work-with-set
     */

    private String resourceFolder = "./Alphabet";
    private static LTSTestsUtils myUtils;

    private void assertFileWithAlphabetSolution(String fileInput, String[] solution, String compositionName) throws IOException {
        File ltsFile = LTSTestsUtils.getFile(resourceFolder, fileInput);
        myUtils = new LTSTestsUtils(ltsFile);

        FileInput lts = new FileInput(ltsFile);
        System.out.println("Start Testing file:" + ltsFile.getName());

        LTSOutput output = new TestLTSOuput();
        LTSCompiler compiled = myUtils.compileWithLtsCompiler(lts, output);
        CompositionExpression c = compiled.getComposite(compositionName);
        String[] alphabet = actionsToString(c.priorityActions);
        
        Arrays.sort(alphabet);
        Arrays.sort(solution);
        
        Assert.assertTrue(
                "Solution is wrong: " + printAllAlphabet(alphabet), 
                Arrays.equals(alphabet, solution)
        );
    }

    private String[] actionsToString(LabelSet priorityActions) {
        Set<String> result = new HashSet<String>();
        for (Object label : priorityActions.labels) {
            ActionName actName = (ActionName) label;
            result.add(actName.name.toString());
        }        
        return result.toArray(new String[result.size()]);
    }

    String printAllAlphabet(String[] alphabet) {
        String res = new String();
        for (String element : alphabet) {
            res = res + element + " ";
        }
        return res;
    }

    @Test
    public void testActionSet() throws Exception {
        String[] solution = new String[]{"a", "b", "c", "a?", "b?", "c?" };
        assertFileWithAlphabetSolution("action_set.lts", solution, "Q");
    }

    @Test
    public void testActionLiterals() throws Exception {
        String[] solution = new String[]{"a", "b", "c", "a?", "b?", "c?" };
        assertFileWithAlphabetSolution("action_set.lts", solution, "S");
    }

}