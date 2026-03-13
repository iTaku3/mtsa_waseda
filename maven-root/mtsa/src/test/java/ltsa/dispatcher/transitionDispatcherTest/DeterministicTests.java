package ltsa.dispatcher.transitionDispatcherTest;

import static ltsa.dispatcher.TransitionSystemDispatcher.determinise;
import static ltsa.dispatcher.TransitionSystemDispatcher.makeAbstractModel;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

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

public class DeterministicTests {

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

    private MTS<Long, String> compileDeterministic(String FSP, String modelName) {
        CompositeState cs = compileCS(FSP, modelName);
        determinise(cs, new EmptyLTSOuput());
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
    abstractTest("A = (a -> b -> A | a -> c -> A).", "A", "A = (a -> (b -> A | c -> A)).", "A");
    }

    @Test
    public void compositeProcessTest() {
        abstractTest("A = (a -> b -> A | a -> c -> A). ||COMP = (A).", "COMP", "A = (a -> (b -> A | c -> A)).", "A");
    }


    @Test
    public void terminalStatesTest() {
        abstractTest("A = (a -> b -> A | a->END | b->STOP).", "A", "A = (a -> b -> A | b -> STOP).", "A");
    }


  public void abstractTest(
      String abstractFSP, String abstractName, String expected, String expectedName) {

    assertTrue(
        TransitionSystemDispatcher.isStronglyBisimilar(
            compileDeterministic(abstractFSP, abstractName),
            " original ",
            compile(expected, expectedName),
            " synthesised ",
            new StandardOutput()));
        }
}
