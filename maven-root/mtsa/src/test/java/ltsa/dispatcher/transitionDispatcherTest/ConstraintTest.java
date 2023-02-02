package ltsa.dispatcher.transitionDispatcherTest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.SemanticType;
import java.io.IOException;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.ui.StandardOutput;
import org.junit.Test;

public class ConstraintTest {
  AutomataToMTSConverter converter = AutomataToMTSConverter.getInstance();
  MTSCompiler compiler = MTSCompiler.getInstance();

  private MTS<Long, String> compile(String FSP, String modelName) {

    try {
      return converter.convert(compiler.compileCompositeState(modelName, FSP).getComposition());
    } catch (IOException e) {
      e.printStackTrace();
      fail("Exception while compiling FSP in test" + FSP);
    }

    return null;
  }


  @Test
  public void nextTest() {
    propertyTest("constraint P = [] (p -> X q)", "P", "A = (p -> q -> A | q -> A).", "A");
  }

  @Test
  public void noTemporalOperatorsTest() {
      propertyTest("constraint P = (p && !q)", "P", "A = (p -> B), B= ({p,q} -> B).", "A");
  }

  @Test
  public void fluentsTest() {
    String propertyFSP =
            "fluent A = <a, c>\n" +
            "fluent B = <b, c>\n" +
            "\n" +
            "constraint Q= [](A -> X B)";

    String expectedFSP = "Q = Q0,\n" +
            "\tQ0\t= (c -> Q0\n" +
            "\t\t  |a -> Q1\n" +
            "\t\t  |b -> Q2),\n" +
            "\tQ1\t= (b -> Q3),\n" +
            "\tQ2\t= (c -> Q0\n" +
            "\t\t  |b -> Q2\n" +
            "\t\t  |a -> Q3),\n" +
            "\tQ3\t= ({a, b} -> Q3).";

    propertyTest(propertyFSP, "Q", expectedFSP, "Q");
  }


    public void propertyTest(
      String propertyFSP, String abstractName, String expected, String expectedName) {

    assertTrue(
        TransitionSystemDispatcher.isStronglyBisimilar(
            compile(propertyFSP, abstractName),
            " original ",
            compile(expected, expectedName),
            " synthesised ",
            new StandardOutput()));
  }
}
