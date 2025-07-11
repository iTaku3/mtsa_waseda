package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.*;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import jargs.gnu.CmdLineParser;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.*;
import ltsa.ui.StandardOutput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.*;

import static java.util.Collections.emptyList;


public class FeatureBasedExplorationHeuristic<State, Action> implements ExplorationHeuristic<State, Action> {

    /** Wrapper of DCS NonBlocking with feature calculation **/
    private final DCSWithFeatures<State, Action> dcs;

    /** List of actions available for expanding **/
    private final ArrayList<Pair<Compostate<State, Action>, HAction<State, Action>>> explorationFrontier;

    ReadyAbstraction<State, Action> ra;

    /** Environment for running the neural network of the agent **/
    OrtSession session;
    OrtEnvironment ortEnv;

    /** Path to the saved neural network **/
    public static String model_path = "mock";

    public static boolean debugging = false;

    FeatureBasedExplorationHeuristic(DirectedControllerSynthesisNonBlocking<State, Action> dcs) {

        this.dcs = new DCSWithFeatures<>(dcs);
        if(DCSWithFeatures.using_ra_feature)
            this.ra = new ReadyAbstraction<>(dcs.ltss, dcs.defaultTargets, dcs.alphabet);

        if(model_path.equals("python")){ // the buffer is created from python
            this.session = null;
            this.ortEnv = null;
        } else {
            ByteBuffer bb = ByteBuffer.allocateDirect(getNumberOfFeatures()*4*100000);
            bb.order(ByteOrder.nativeOrder());
            this.dcs.setFeaturesBuffer(bb.asFloatBuffer());

            if(!model_path.equals("mock")) {
                this.ortEnv = OrtEnvironment.getEnvironment();
                try {
                    OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
                    opts.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.BASIC_OPT);
                    this.session = ortEnv.createSession(model_path, opts);
                } catch (OrtException e) {
                    e.printStackTrace();
                }
            }
        }
        this.explorationFrontier = new ArrayList<>();
    }

    public int frontierSize(){
        return explorationFrontier.size();
    }

    void filterFrontier(){
        for(int i = 0; i < explorationFrontier.size();) {
            if (!explorationFrontier.get(i).getFirst().isStatus(Status.NONE)) {
                removeFromFrontier(i);
            } else {
                i++;
            }
        }
    }

    public void setFeaturesBuffer(FloatBuffer input_buffer){
        this.dcs.setFeaturesBuffer(input_buffer);
    }

    private void printFeatures(){
        System.out.println("--------------------------");
        System.out.println(explorationFrontier.size()+" "+getNumberOfFeatures());
        FloatBuffer availableActions = dcs.getAvailableActions(explorationFrontier.size());
        for (Pair<Compostate<State, Action>, HAction<State, Action>> stateAction : explorationFrontier) {
            System.out.print(stateAction.getSecond() + " ");
            for (int j = 0; j < getNumberOfFeatures(); j++) {
                System.out.print(availableActions.get() + " ");
            }
            System.out.println();
        }
    }

    void printFrontier(){
        System.out.println("Frontier: ");
        for(Pair<Compostate<State, Action>, HAction<State, Action>> action : explorationFrontier){
            System.out.println(action);
        }
    }

    void printValues(float[][] values){
        for(int i = 0; i < explorationFrontier.size(); i++){
            System.out.println(values[i][0]);
        }
    }

    public void computeFeatures(){
        filterFrontier();

        dcs.clearBuffer();
        for (Pair<Compostate<State, Action>, HAction<State, Action>> stateAction : explorationFrontier) {
            dcs.getActionFeatures(stateAction);
        }

        if(debugging) printFeatures();
    }

    public Pair<Compostate<State, Action>, HAction<State, Action>> getNextAction(List<String> comparison) {
        assert explorationFrontier.size() > 0;
        // printFrontier();
        computeFeatures();

        FloatBuffer availableActions = dcs.getAvailableActions(explorationFrontier.size());

        if(Objects.equals(model_path, "mock")){
            Random rand = new Random();
            int selected = rand.nextInt(explorationFrontier.size());
            // System.out.println("Action selected: "+ selected);
            return removeFromFrontier(selected);
        } else {
            float[][] values = null;
            try {
                OnnxTensor t = OnnxTensor.createTensor(this.ortEnv, availableActions, new long[]{explorationFrontier.size(), getNumberOfFeatures()});

                OrtSession.Result results = session.run(Collections.singletonMap("X", t));
                values = (float[][]) results.get(0).getValue();
            } catch (OrtException e) {
                e.printStackTrace();
            }
            if(debugging) printValues(values);

            int best = 0;
            for(int i = 0; i < explorationFrontier.size(); i++){
                assert values != null;
                if(values[i][0] > values[best][0]){
                    best = i;
                }
            }
            if(debugging) System.out.println(best);
            return removeFromFrontier(best);
        }
    }

    public void setInitialState(Compostate<State, Action> state, List<String> comparison) {
    }

    public void newState(Compostate<State, Action> state, Compostate<State, Action> parent, List<String> comparison) {
        if (parent != null)
            state.setTargets(parent.getTargets());

        if (state.marked)
            state.addTargets(state);

        if(state.isStatus(Status.NONE))
            addTransitionsToFrontier(state);

        if(DCSWithFeatures.using_ra_feature)
            ra.evalForHeuristic(state, this);
    }

    private void addTransitionsToFrontier(Compostate<State, Action> state){
        for(HAction<State, Action> action : state.getTransitions()){
            explorationFrontier.add(new Pair<>(state, action));
        }
    }

    public void notifyExpandingState(Compostate<State, Action> parent, HAction<State, Action> action, Compostate<State, Action> state) {
        if(state.wasExpanded()){ // todo: understand this, i am copying the behavior of the code pre refactor
            state.setTargets(parent.getTargets());
            if (state.marked)
                state.addTargets(state);
        }
    }

    public boolean somethingLeftToExplore() {
        return !explorationFrontier.isEmpty();
    }

    public void expansionDone(Compostate<State, Action> first, HAction<State, Action> second, Compostate<State, Action> child) {
    }

    public void updateUnexploredTransitions(Compostate<State, Action> state, HAction<State, Action> action) {
        state.unexploredTransitions--;
        if(!action.isControllable())
            state.uncontrollableUnexploredTransitions--;
    }

    public void notifyStateIsNone(Compostate<State, Action> state) {
    }


    public void notifyStateSetErrorOrGoal(Compostate<State, Action> state) {
        state.uncontrollableUnexploredTransitions = 0;
        state.unexploredTransitions = 0;
    }

    public void notifyExpansionDidntFindAnything(Compostate<State, Action> parent, HAction<State, Action> action, Compostate<State, Action> child) {
    }

    public boolean fullyExplored(Compostate<State, Action> state) {
        return state.unexploredTransitions == 0;
    }

    public boolean hasUncontrollableUnexplored(Compostate<State, Action> state) {
        return state.uncontrollableUnexploredTransitions > 0;
    }

    public void initialize(Compostate<State, Action> state) {
        state.unexploredTransitions = state.transitions.size();

        state.uncontrollableUnexploredTransitions = 0;
        for(HAction<State,Action> action : state.transitions)
            if(!action.isControllable()) state.uncontrollableUnexploredTransitions ++;

        state.estimates = new HashMap<>();
        state.uncontrollableTransitions = state.uncontrollableUnexploredTransitions;
        state.targets = emptyList();
    }

    public Pair<Compostate<State, Action>, HAction<State, Action>> removeFromFrontier(int idx) {
        Pair<Compostate<State, Action>, HAction<State, Action>> stateAction = efficientRemove(idx);

        if(stateAction.getFirst().isStatus(Status.NONE))
            this.updateUnexploredTransitions(stateAction.getFirst(), stateAction.getSecond());

        assert stateAction.getFirst().unexploredTransitions >= 0;

        return stateAction;
    }

    private Pair<Compostate<State, Action>, HAction<State, Action>> efficientRemove(int idx) {
        // removing last element is more efficient
        Pair<Compostate<State, Action>, HAction<State, Action>> stateAction = explorationFrontier.get(idx);
        explorationFrontier.set(idx, explorationFrontier.get(explorationFrontier.size()-1));
        explorationFrontier.remove(explorationFrontier.size()-1);
        return stateAction;
    }

    public void addRecommendation(Compostate<State, Action> state, HAction<State, Action> action, HEstimate<State, Action> estimate) {
        boolean controllableAction = action.isControllable();

        if(!controllableAction && estimate.isConflict()){
            state.heuristicStronglySuggestsIsError = true;
        }
        state.addEstimate(action, estimate);
    }

    public int getNumberOfFeatures(){
        return DCSWithFeatures.getNumberOfFeatures();
    }


    public static void synthesize(String filename, String model_path, String labels_path, boolean ra_feature, boolean debug){
        Pair<CompositeState, LTSOutput> c = compileFSP(filename);

        DirectedControllerSynthesisNonBlocking.mode = DirectedControllerSynthesisNonBlocking.HeuristicMode.TrainedAgent;
        FeatureBasedExplorationHeuristic.model_path = model_path;
        DCSWithFeatures.readLabels(labels_path);
        FeatureBasedExplorationHeuristic.debugging = debug;
        DCSWithFeatures.using_ra_feature = ra_feature;

        TransitionSystemDispatcher.hcs(c.getFirst(), new LTSOutput() {
            public void out(String str) {System.out.print(str);}
            public void outln(String str) { System.out.println(str);}
            public void clearOutput() {}
        });
    }

    public static Pair<CompositeState, LTSOutput> compileFSP(String filename){
        String currentDirectory = null;
        try {
            currentDirectory = (new File(".")).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LTSInput input = new LTSInputString(readFile(filename));
        LTSOutput output = new StandardOutput();

        LTSCompiler compiler = new LTSCompiler(input, output, currentDirectory);
        compiler.compile();
        CompositeState c = compiler.continueCompilation("DirectedController");

        TransitionSystemDispatcher.parallelComposition(c, output);

        return new Pair<>(c, output);
    }

    public static String readFile(String filename) {
        String result = null;
        try {
            BufferedReader file = new BufferedReader(new FileReader(filename));
            String thisLine;
            StringBuffer buff = new StringBuffer();
            while ((thisLine = file.readLine()) != null)
                buff.append(thisLine+"\n");
            file.close();
            result = buff.toString();
        } catch (Exception e) {
            System.err.print("Error reading FSP file " + filename + ": " + e);
            System.exit(1);
        }
        return result;
    }

    public static void main(String[] args) {
        CmdLineParser cmdParser= new CmdLineParser();
        CmdLineParser.Option filename = cmdParser.addStringOption('i', "file");
        CmdLineParser.Option model_path = cmdParser.addStringOption('m', "model");
        CmdLineParser.Option labels_path = cmdParser.addStringOption('l', "labels");
        CmdLineParser.Option ra_feature = cmdParser.addBooleanOption('r', "ra_feature");
        CmdLineParser.Option debug = cmdParser.addBooleanOption('d', "debug");

        try {
            cmdParser.parse(args);
        } catch (CmdLineParser.OptionException e) {
            System.out.println("Invalid option: " + e.getMessage() + "\n");
            System.exit(0);
        }

        String filenameValue = (String)cmdParser.getOptionValue(filename);
        String model_pathValue = (String)cmdParser.getOptionValue(model_path);
        String labels_pathValue = (String)cmdParser.getOptionValue(labels_path);
        Boolean ra_featureValue = (Boolean)cmdParser.getOptionValue(ra_feature);
        Boolean debugValue = (Boolean)cmdParser.getOptionValue(debug);

        assert filenameValue != null;
        assert model_pathValue != null;
        assert labels_pathValue != null;
        synthesize(filenameValue, model_pathValue, labels_pathValue, ra_featureValue != null && ra_featureValue, debugValue != null && debugValue);
    }
}

