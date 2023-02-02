package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.HAction;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.HDist;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.*;

public class DCSWithFeatures<State, Action> {

    public static boolean using_ra_feature = true;
    public static HashMap<String, Integer> labels_idx = new HashMap<>();

    FloatBuffer input_buffer;
    DirectedControllerSynthesisNonBlocking<State, Action> dcs;

    DCSWithFeatures(DirectedControllerSynthesisNonBlocking<State, Action> dcs){
        this.dcs = dcs;
    }

    public void setFeaturesBuffer(FloatBuffer featuresBuffer){
        this.input_buffer = featuresBuffer;
    }

    public void setHeuristic(ExplorationHeuristic<State, Action> heuristic){
        this.dcs.heuristic = heuristic;
    }

    public void clearBuffer(){
        this.input_buffer.clear();
    }

    private static float toFloat(boolean b) {
        return b ? 1.0f : 0.0f;
    }

    public boolean isFinished(){
        return this.dcs.isFinished();
    }

    public static int getNumberOfFeatures(){
        return 11+labels_idx.size() + (using_ra_feature ? 2 : 0);
    }

    public void getActionFeatures(Pair<Compostate<State, Action>, HAction<State, Action>> stateAction){
        Compostate<State, Action> state = stateAction.getFirst();
        HAction<State, Action> action = stateAction.getSecond();

        List<State> childStates = this.dcs.getChildStates(state, action);
        Compostate<State, Action> child = this.dcs.compostates.get(childStates);

        boolean childMarked = true;
        for (int lts = 0; childMarked && lts < this.dcs.ltssSize; ++lts)
            childMarked = this.dcs.defaultTargets.get(lts).contains(childStates.get(lts));

        if(using_ra_feature) addRAFeature(state, action);
        addActionLabelFeature(action);
        this.input_buffer.put(toFloat(action.isControllable()));
        this.input_buffer.put(1.0f / ((float) state.getDepth() + 1));
        this.input_buffer.put(((float) state.unexploredTransitions) / state.getTransitions().size());
        this.input_buffer.put(toFloat(state.marked));
        this.input_buffer.put(toFloat(childMarked));

        if (child != null) {
            this.input_buffer.put(toFloat(child.isStatus(Status.GOAL)));
            this.input_buffer.put(toFloat(child.isStatus(Status.ERROR)));
            this.input_buffer.put(toFloat(child.isStatus(Status.NONE)));
            this.input_buffer.put(toFloat(child.getTransitions().size() == 0));
            this.input_buffer.put(((float) child.uncontrollableTransitions) / (child.getTransitions().size()+1));
            this.input_buffer.put(((float) child.unexploredTransitions) / (child.getTransitions().size()+1));
        } else {
            this.input_buffer.put(0.0f);
            this.input_buffer.put(0.0f);
            this.input_buffer.put(0.0f);
            this.input_buffer.put(0.0f); // we assume that it is not deadlock
            this.input_buffer.put(0.5f); // we assume that half of the actions are controllable
            this.input_buffer.put(1.0f); // all actions are unexplored
        }
    }

    private void addActionLabelFeature(HAction<State, Action> action){
        if(!labels_idx.isEmpty()){
            System.out.println(labels_idx);
            System.out.println(action);
            String label = action.toString().split("\\.")[0];
            int idx = labels_idx.get(label);
            for(int i = 0; i < labels_idx.size(); i++){
                this.input_buffer.put(toFloat(i == idx));
            }
        }
    }

    private void addRAFeature(Compostate<State, Action> state, HAction<State, Action> action) {
        HDist estimate = state.getEstimate(action).get(0);
        this.input_buffer.put(1.0f / ((float) estimate.getSecond()));
    }

    public FloatBuffer getAvailableActions(int n){
        this.input_buffer.position(0);
        this.input_buffer.limit(n*getNumberOfFeatures());
        return this.input_buffer.slice();
    }

    public static void readLabels(String labels_path){
        LinkedList<String> action_labels = new LinkedList<>();
        if(!Objects.equals(labels_path, "mock")){
            try {
                Scanner reader = new Scanner(new File(labels_path));
                while (reader.hasNextLine()) {
                    String data = reader.nextLine();
                    action_labels.add(data);
                }
                reader.close();
            } catch (FileNotFoundException e) {
                System.out.println("Could not open labels file.");
                e.printStackTrace();
            }
        }

        labels_idx = new HashMap<>();

        int idx = 0;
        for(String action : action_labels){
            labels_idx.put(action, idx);
            idx += 1;
        }
    }
}
