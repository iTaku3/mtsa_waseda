package ltsa.dispatcher.transitionDispatcherTest;

import static ltsa.dispatcher.TransitionSystemDispatcher.makeAbstractModel;
import static org.junit.Assert.*;

import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.SemanticType;
import java.io.IOException;
import javax.annotation.Nullable;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.CompositeState;
import ltsa.lts.EmptyLTSOuput;
import ltsa.ui.StandardOutput;
import org.junit.Test;

public class AbstractTests {
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

  private MTS<Long, String> compileAbstract(String FSP, String modelName) {
    CompositeState cs = compileCS(FSP, modelName);
    makeAbstractModel(cs, new EmptyLTSOuput());
    return converter.convert(cs.getComposition());
  }

  @Nullable
  private CompositeState compileCS(String FSP, String modelName) {
    try {
      return compiler.compileCompositeState(modelName, FSP);
    } catch (IOException e) {
      e.printStackTrace();
      fail("Exception while compiling FSP in test" + FSP);
    }
    return null;
  }

  @Test
  public void sequentialProcessTest() {
    abstractTest("A = (a -> b -> A | a->STOP | b->END).", "A", "A = (a? -> A | b? -> A).", "A");
  }

  @Test
  public void compositeProcessTest() {
    abstractTest("A = (a -> b -> A | a->STOP | b->END). ||COMP = (A).", "COMP", "A = (a? -> A | b? -> A).", "A");
  }

  public void abstractTest(
      String abstractFSP, String abstractName, String expected, String expectedName) {

    assertTrue(
        TransitionSystemDispatcher.isRefinement(
            compileAbstract(abstractFSP, abstractName),
            " original ",
            compile(expected, expectedName),
            " synthesised ",
            SemanticType.STRONG.getRefinement(),
            new StandardOutput()));

    assertFalse(
        TransitionSystemDispatcher.isRefinement(
            compile(expected, expectedName),
            " synthesised ",
            compileAbstract(abstractFSP, abstractName),
            " original ",
            SemanticType.STRONG.getRefinement(),
            new StandardOutput()));

    assertTrue(
        TransitionSystemDispatcher.isRefinement(
            compile(abstractFSP, abstractName),
            " synthesised ",
            compileAbstract(abstractFSP, abstractName),
            " original ",
            SemanticType.STRONG.getRefinement(),
            new StandardOutput()));
  }
}
