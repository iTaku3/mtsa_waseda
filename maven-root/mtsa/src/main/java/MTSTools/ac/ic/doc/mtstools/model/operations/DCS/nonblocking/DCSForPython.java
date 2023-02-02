package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.HAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.Random;

import ai.onnxruntime.OrtException;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.*;
import ltsa.ui.StandardOutput;

import static org.junit.Assert.*;

/** This class can be used from python with jpype */
public class DCSForPython {

    private final String inputStartingDirectory;
    int n;
    int k;
    String problem;
    private FeatureBasedExplorationHeuristic<Long, String> heuristic;
    private DirectedControllerSynthesisNonBlocking<Long, String> dcs;
    private FloatBuffer input_buffer;

    public DCSForPython(String inputStartingDirectory, String labels_path, boolean using_ra_feature){
        this.inputStartingDirectory = inputStartingDirectory;
        DCSWithFeatures.using_ra_feature = using_ra_feature;
        if(Objects.equals(labels_path, "mock"))
            DCSWithFeatures.readLabels(labels_path);
        else
            DCSWithFeatures.readLabels(inputStartingDirectory + labels_path);
    }

    /** restarts synthesis for a given fsp */
    public void startSynthesis(String problem, int n, int k) {
        this.n = n;
        this.k = k;
        this.problem = problem;

        String filename = inputStartingDirectory+"fsp/"+problem+"/"+problem+"-"+n+"-"+k+".fsp";

        Pair<CompositeState, LTSOutput> c = FeatureBasedExplorationHeuristic.compileFSP(filename);

        DirectedControllerSynthesisNonBlocking<Long, String> dcs = TransitionSystemDispatcher.hcsInteractive(c.getFirst(), c.getSecond());
        if(dcs == null) fail("Could not start DCS for the given fsp");

        FeatureBasedExplorationHeuristic.model_path = "python";
        this.heuristic = new FeatureBasedExplorationHeuristic<>(dcs);
        this.dcs = dcs;
        this.dcs.heuristic = this.heuristic;
        this.heuristic.setFeaturesBuffer(this.input_buffer);

        dcs.setupInitialState();
        this.heuristic.computeFeatures();
    }

    public void setFeaturesBuffer(FloatBuffer input_buffer){
        this.input_buffer = input_buffer;
    }

    public double getSynthesisTime(){
        return this.dcs.getStatistics().getElapsed();
    }

    public int getExpandedTransitions(){
        return this.dcs.getStatistics().getExpandedTransitions();
    }

    public int getExpandedStates(){
        return this.dcs.getStatistics().getExpandedStates();
    }

    public int frontierSize(){
        return this.heuristic.frontierSize();
    }

    public int getNumberOfFeatures(){
        return DCSWithFeatures.getNumberOfFeatures();
    }

    public boolean isFinished(){
        return dcs.isFinished();
    }

    public void expandAction(int idx){
        Pair<Compostate<Long, String>, HAction<Long, String>> stateAction = heuristic.removeFromFrontier(idx);
        dcs.expand(stateAction.getFirst(), stateAction.getSecond());
        if(!dcs.isFinished()){
            this.heuristic.computeFeatures();
        }
    }

    public static void main(String[] args) throws OrtException {
        // This main is for testing purposes only

        DCSForPython env = new DCSForPython("../../../learningsynthesis/", "mock", true);
        env.setFeaturesBuffer(ByteBuffer.allocateDirect(env.getNumberOfFeatures() * 4 * 100000).asFloatBuffer());
        Random rand = new Random();

        for(int i = 0; i < 10; i++){
            env.startSynthesis("AT", 2, 2);
            while (!env.isFinished()) {
                env.expandAction(rand.nextInt(env.frontierSize()));
            }
        }
    }
}


