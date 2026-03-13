package MTSSynthesis.controller.model;

/**
 * Created by Virginia Brassesco on 5/6/16.
 */

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSSimulationSemantics;
import ltsa.MultiCore.ComputerOptions;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.FileInput;
import ltsa.lts.LTSOutput;
import ltsa.lts.ltl.AssertDefinition;
import ltsa.ui.StandardOutput;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static IntegrationTests.LTSTestsUtils.getFiles;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;


@RunWith(Parameterized.class)
public class RankBasedGameSolverTests {
    private File inputFile;
    private FileInput inputLTS;
    private ComputerOptions computerOptions = ComputerOptions.getInstance();

    @Parameterized.Parameters(name = "{index}: Testing MultiCore ({0})")
    public static List<File> controllerFiles() throws IOException {

        List<File> origin = getFiles("MultiCore/");

        return origin;
    }

    public RankBasedGameSolverTests(File fileName) {
        try {
            this.inputFile = fileName;
            this.inputLTS = new FileInput(this.inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(timeout = 300000)
    public void tenThreadsControllerRealisable() throws Exception {
        this.computerOptions.setAllowedThreads(10);
        CompositeState parallelC = MTSCompiler.getInstance().compileComposite("C", inputLTS, new StandardOutput());

        assertTrue(!parallelC.composition.name.contains("#"));
    }

    @Test(timeout = 300000)
    public void sequentialControllerRealisable() throws Exception {
        this.computerOptions.setAllowedThreads(1);
        CompositeState parallelC = MTSCompiler.getInstance().compileComposite("C", inputLTS, new StandardOutput());

        assertTrue(!parallelC.composition.name.contains("#"));
    }

    @Test(timeout = 1200000)
    public void bisimilarSeq4Threads() throws Exception {
        this.computerOptions.setAllowedThreads(1);
        MTS seqC = MTSCompiler.getInstance().compileMTS("C", inputFile);
        if (seqC.getStates().size() < 2000) {
            this.computerOptions.setAllowedThreads(4);
            MTS parallelC = MTSCompiler.getInstance().compileMTS("C", inputFile);
            boolean seqseq = TransitionSystemDispatcher.isRefinement(seqC, "SeqC", seqC, "SeqC", new LTSSimulationSemantics(), new StandardOutput());
            boolean parpar = TransitionSystemDispatcher.isRefinement(parallelC, "ParallelC", parallelC, "ParallelC", new LTSSimulationSemantics(), new StandardOutput());
            boolean seqpar = TransitionSystemDispatcher.isRefinement(seqC, "SeqC", parallelC, "ParallelC", new LTSSimulationSemantics(), new StandardOutput());
            boolean parseq = TransitionSystemDispatcher.isRefinement(parallelC, "ParallelC", seqC, "SeqC", new LTSSimulationSemantics(), new StandardOutput());

            assertTrue(seqpar && seqseq && parpar && parseq);
        } else {
            System.out.println("Too big to check bisimilarity: " + seqC.getStates().size());
        }
    }

    @Test(timeout = 1200000)
    public void bisimilar2vs4Threads() throws Exception {
        this.computerOptions.setAllowedThreads(2);
        MTS seqC = MTSCompiler.getInstance().compileMTS("C", inputFile);
        if (seqC.getStates().size() < 2000) {
            this.computerOptions.setAllowedThreads(4);
            MTS parallelC = MTSCompiler.getInstance().compileMTS("C", inputFile);
            boolean twotwo = TransitionSystemDispatcher.isRefinement(seqC, "SeqC", seqC, "SeqC", new LTSSimulationSemantics(), new StandardOutput());
            boolean fourfour = TransitionSystemDispatcher.isRefinement(parallelC, "ParallelC", parallelC, "ParallelC", new LTSSimulationSemantics(), new StandardOutput());
            boolean twofour = TransitionSystemDispatcher.isRefinement(seqC, "SeqC", parallelC, "ParallelC", new LTSSimulationSemantics(), new StandardOutput());
            boolean fourtwo = TransitionSystemDispatcher.isRefinement(parallelC, "ParallelC", seqC, "SeqC", new LTSSimulationSemantics(), new StandardOutput());

            assertTrue(twotwo && fourfour && twofour && fourtwo);
        } else {
            System.out.println("Too big to check bisimilarity: " + seqC.getStates().size());
        }
    }
    //TODO: ExpectedC... que me dejen este Goal y que sea bisimilar con lo que ya obtengo


    @Test(timeout = 600000)
    public void parallelAssertTestGoals() throws Exception {
        this.computerOptions.setAllowedThreads(4);
        CompositeState compiled = MTSCompiler.getInstance().compileCompositeState("C", inputFile);
        if (compiled.name.contains("#")) {
            fail("There is no controller");
        } else {

            LTSOutput output = new TestLTSOuput();

            CompositeState ltlProperty = AssertDefinition.compile(output, "TESTGOAL");
            if (ltlProperty != null) {
                compiled.setErrorTrace(new ArrayList<String>());

                Vector<CompactState> machines = new Vector<CompactState>();
                machines.add(compiled.getComposition());
                CompositeState cs = new CompositeState(compiled.name, machines);
                cs.checkLTL(output, ltlProperty);

                if (output.toString().contains("check safety")) {
                    TransitionSystemDispatcher.checkSafety(cs, output);
                    fail(output.toString());
                } else {
                    assertTrue(cs.getErrorTrace() == null || cs.getErrorTrace().isEmpty());
                }

            } else {
                Assert.fail("TESTGOAL not present in lts.");
            }
        }
    }

}

