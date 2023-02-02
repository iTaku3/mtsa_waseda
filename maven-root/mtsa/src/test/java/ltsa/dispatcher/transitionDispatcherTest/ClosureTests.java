package ltsa.dispatcher.transitionDispatcherTest;

import static ltsa.dispatcher.TransitionSystemDispatcher.makeClosureModel;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import java.io.IOException;
import javax.annotation.Nullable;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.CompositeState;
import ltsa.lts.EmptyLTSOuput;
import ltsa.ui.StandardOutput;
import org.junit.Test;

public class ClosureTests {
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

  private MTS<Long, String> compileClosure(String FSP, String modelName) {
    CompositeState cs = compileCS(FSP, modelName);
    makeClosureModel(cs, new EmptyLTSOuput());
    return converter.convert(cs.getComposition());
  }

  @Test
  public void noTausTest() {
    String closure = "A = (a -> b -> A).";
    String closureName = "A";
    String expected = "A = (a -> b -> A).";
    String expectedName = "A";

    assertTrue(

        TransitionSystemDispatcher.isStronglyBisimilar(
            compileClosure(closure, closureName),
            " original ",
            compile(expected, expectedName),
            " synthesised ",
            new StandardOutput()));
  }

  @Test
  public void tauRemovalTest() {
    String closure = " A = (a -> b -> A)\\{b}.";
    String closureName = "A";
    String expected = "A = (a -> A).";
    String expectedName = "A";

    assertTrue(
        TransitionSystemDispatcher.isWeaklyBisimilar(
            compileClosure(closure, closureName),
            " original ",
            compile(expected, expectedName),
            " synthesised ",
            new StandardOutput()));
  }

  @Test
  public void nonDeterminisimTest() {
    String closure = " A = (d -> a -> b -> A | d->a->c->A)\\{a}.";
    String closureName = "A";
    String expected = "A = (d -> b -> A| d -> c -> A).";
    String expectedName = "A";

    assertTrue(
        TransitionSystemDispatcher.isWeaklyBisimilar(
            compileClosure(closure, closureName),
            " original ",
            compile(expected, expectedName),
            " synthesised ",
            new StandardOutput()));
  }

  @Test
  public void compositeClosure() {
    String closure = " A = (d -> a -> b -> A | d->a->c->A). ||COMP = (A)\\{a}.";
    String closureName = "COMP";
    String expected = "A = (d -> b -> A| d -> c -> A).";
    String expectedName = "A";

    assertTrue(
            TransitionSystemDispatcher.isWeaklyBisimilar(
                    compileClosure(closure, closureName),
                    " original ",
                    compile(expected, expectedName),
                    " synthesised ",
                    new StandardOutput()));
  }
}
