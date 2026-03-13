package IntegrationTests;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import MTSSynthesis.controller.model.ControlProblem;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MarkedWithIllegalLTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.monolithicDirector.DirectedControllerSynthesisMonolithicDirector;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.CompositeState;
import ltsa.lts.FileInput;
import ltsa.lts.LTSCompiler;
import ltsa.lts.LTSOutput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TestMonolithicDirector {

    private static final String[] RESOURCE_FOLDERS = {"MonolithicDirector/Controllable","MonolithicDirector/ControllableFSPs","MonolithicDirector/NoControllable","MonolithicDirector/NoControllableFSPs"};
    private File file;

    public TestMonolithicDirector(File getFile) {
        file = getFile;

    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static List<File> controllerFiles() throws IOException {
        List<File> allFiles = new ArrayList();
        for (String resource_folder:RESOURCE_FOLDERS) {

            allFiles.addAll(LTSTestsUtils.getFiles(resource_folder));
        }
        return allFiles;
    }

    private static class controlProblem{
        MarkedWithIllegalLTSImpl<Long, String> plant;
        Set<String> controllable;
        Set<String> disturbances;
    }

    /*the sintax of the test files is: (all are comma separated values)
      first line: # of states (N), # actions in alphabet (A), # of controllables (C), # disturbances (D),
                   # marked states (M), # illegal states (I), # transitions (T)
            All states named in the test case must be a long < N
      second line: A alphabet
      third line: C controllable actions
      fourth line: D disturbance actions (must have empty intersection with controllables)
      fifth line: M marked states
      sixth line: I illegal states
      next T lines: triples of the form: 2, a, 5 representing that state 2 has transition with label "a" to state 5
            where T is the ammount of transitions in the test case
    */
    private controlProblem parse(File f) throws FileNotFoundException {
        Scanner scanner = new Scanner(f);
        int N = Integer.parseInt(scanner.next());
        int A = Integer.parseInt(scanner.next());
        int C = Integer.parseInt(scanner.next());
        int D = Integer.parseInt(scanner.next());
        int M = Integer.parseInt(scanner.next());
        int I = Integer.parseInt(scanner.next());
        int T = Integer.parseInt(scanner.next());

        controlProblem probl = new controlProblem();

        scanner.nextLine();
        probl.plant = new MarkedWithIllegalLTSImpl<Long, String>(0L);
        for(Long s=0L; s<N; ++s){
            probl.plant.addState(s);
        }

        probl.plant.addActions(Arrays.asList(scanner.nextLine().split("\\s+")));
        assert(A != 0);
        assert(probl.plant.getActions().size() == A);

        probl.controllable = new HashSet<>(Arrays.asList(scanner.nextLine().split("\\s+")));
        if (C == 0) probl.controllable.clear();
        assert(probl.controllable.size() == C);

        probl.disturbances = new HashSet<>(Arrays.asList(scanner.nextLine().split("\\s+")));
        if (D == 0) probl.disturbances.clear();
        assert(probl.disturbances.size() == D);

        Set<String> marked = new HashSet<>(Arrays.asList(scanner.nextLine().split("\\s+")));
        if (M == 0) marked.clear();
        assert(marked.size() == M);
        for (String m : marked){
            assert(Long.parseLong(m) < N);
            probl.plant.mark(Long.parseLong(m));
        }

        Set<String> illegals = new HashSet<>(Arrays.asList(scanner.nextLine().split("\\s+")));
        if (I == 0) illegals.clear();
        assert(illegals.size() == I);
        for (String i : illegals){
            assert(Long.parseLong(i) < N);
            probl.plant.makeIllegal(Long.parseLong(i));
        }

        for(int i = 0; i < T; i++){
            String[] trans = scanner.nextLine().split("\\s+");
            Long s1 = Long.parseLong(trans[0]);
            Long s2 = Long.parseLong(trans[2]);
            probl.plant.addTransition(s1,trans[1],s2);
        }

        assert(!scanner.hasNext());

        scanner.close();
        return probl;
    }

    @Test
    public void testControllability() throws Exception {
        String parent = file.getParentFile().getName();
        if(parent.equals("Controllable") || parent.equals("NoControllable")) {
            controlProblem Probl = parse(file);

            DirectedControllerSynthesisMonolithicDirector<Long, String> dcs = new DirectedControllerSynthesisMonolithicDirector<Long, String>();
            LTS<Long, String> director = dcs.synthesize(Probl.plant, Probl.controllable, Probl.disturbances);

            boolean isControllable = director != null;

            if (parent.equals("Controllable")) {
                assertTrue("The FSP is controllable", isControllable);
                //here we would use LegalityAnalyser, but it uses other structures like compositeState.
                //we add the legality check to assertGoodDirector inside DCSMonolithicDirector.
            } else {
                assertFalse("The FSP is not controllable", isControllable);
            }
        }
        else {
            FileInput lts = new FileInput(file);
            LTSOutput output = new TestLTSOuput();
            LTSCompiler compiled = new LTSCompiler(lts, output, ".");
            compiled.compile();

            CompositeState compositeStateC = compiled.continueCompilation("C");
            TransitionSystemDispatcher.applyComposition(compositeStateC, output);

            boolean isControllable = compositeStateC.composition != null;

            if (parent.equals("ControllableFSPs")){
                assertTrue("The FSP is controllable", isControllable);
            } else{
                assertFalse("The FSP is not controllable", isControllable);
            }
        }
    }

}


