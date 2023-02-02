package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.*;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import static java.util.Collections.emptyList;

public class OpenSetExplorationHeuristic<State, Action> implements ExplorationHeuristic<State, Action> {

    /** Queue of open states, the most promising state should be expanded first. */
    Queue<Compostate<State, Action>> open;

    /** Abstraction used to rank the transitions from a state. */
    Abstraction<State,Action> abstraction;

    DirectedControllerSynthesisNonBlocking<State,Action> dcs;

    public OpenSetExplorationHeuristic(
            DirectedControllerSynthesisNonBlocking<State,Action> dcs,
            DirectedControllerSynthesisNonBlocking.HeuristicMode mode) {

        Comparator<Compostate<State, Action>> compostateRanker = new DefaultCompostateRanker<>();
        this.dcs = dcs;

        //FIXME, this is only done here until it can be chosen from the FSP instead of hardcoded
        switch (mode){
            case Monotonic:
                abstraction = new MonotonicAbstraction<>(dcs);
                // System.err.println("Heuristic mode: MA");
                break;
            case Ready:
                abstraction = new ReadyAbstraction<>(dcs.ltss, dcs.defaultTargets, dcs.alphabet);
                compostateRanker = new ReadyAbstraction.CompostateRanker<>();
                // System.err.println("Heuristic mode: RA");
                break;
            case BFS:
                abstraction = new BFSAbstraction<>();
                // System.err.println("Heuristic mode: BFS");
                break;
            case Debugging:
                abstraction = new DebuggingAbstraction<>();
                // System.err.println("Heuristic mode: Debugging");
                break;
        }

        open = new PriorityQueue<>(compostateRanker);
    }

    public Pair<Compostate<State,Action>, HAction<State,Action>> getNextAction() {
        Compostate<State,Action> state = getNextState();
        while(fullyExplored(state) || !state.isLive()/* || state.getStates() == null*/){
            state = getNextState();
        }
        Recommendation<State, Action> recommendation = state.nextRecommendation();
        // System.out.println(recommendation.getEstimate());
        return new Pair<>(state, recommendation.getAction());
    }

    public Compostate<State,Action> getNextState() {
        Compostate<State,Action> state = open.remove();
        state.inOpen = false;
        return state;
    }

    public boolean somethingLeftToExplore() {
        return !open.isEmpty();
    }

    /** Adds this state to the open queue (reopening it if was previously closed). */
    public boolean open(Compostate<State,Action> state) {
        // System.err.println("opening" + state);
        boolean result = false;
        state.live = true;
        if (!state.inOpen) {
            if (!state.hasStatusChild(Status.NONE)) {
                result = addToOpen(state);
            } else { // we are reopening a state, thus we reestablish it's exploredChildren instead
                for (Pair<HAction<State, Action>,Compostate<State, Action>> transition : state.getExploredChildren()) {
                    Compostate<State, Action> child = transition.getSecond();
                    if (!child.isLive() && child.isStatus(Status.NONE) && !fullyExplored(child)) // !isGoal(child)
                        result |= open(child);
                }
                if (!result || state.isControlled()){
                    result = addToOpen(state);
                }
            }
        }
        return result;
    }

    public boolean addToOpen(Compostate<State, Action> state) {
        state.inOpen = true;
        return open.add(state);
    }

    public void setInitialState(Compostate<State, Action> state) {
        open(state);
        newState(state, null);
    }

    public void newState(Compostate<State, Action> state, Compostate<State, Action> parent) {
        if(parent != null){
            state.setTargets(parent.getTargets());
        }
        if (state.marked)
            state.addTargets(state);
        abstraction.eval(state);
    }

    public void notifyExpandingState(Compostate<State, Action> parent, HAction<State, Action> action, Compostate<State, Action> state) {
        if(state.wasExpanded()){ // todo: understand this, i am copying the behavior of the code pre refactor
            state.setTargets(parent.getTargets());
            if (state.marked)
                state.addTargets(state);
        }
    }

    public void notifyStateIsNone(Compostate<State, Action> state) {
        if(!fullyExplored(state))
            open(state);
    }

    public void notifyStateSetErrorOrGoal(Compostate<State, Action> state) {
        state.live = false;
        state.clearRecommendations();
    }

    public void expansionDone(Compostate<State, Action> state, HAction<State, Action> action, Compostate<State, Action> child) {
        if (state.isControlled() && state.isStatus(Status.NONE) && !fullyExplored(state)) {
            open(state);
        }
    }

    public void notifyExpansionDidntFindAnything(Compostate<State, Action> parent, HAction<State, Action> action, Compostate<State, Action> child) {
        if (!child.isLive() && !fullyExplored(child)) {
            open(child);
        }
    }

    public boolean fullyExplored(Compostate<State, Action> state) {
        return state.recommendations == null || state.recommendation == null;
    }

    public boolean hasUncontrollableUnexplored(Compostate<State, Action> state) {
        return state.recommendation != null && !state.recommendation.getAction().isControllable();
    }

    public void initialize(Compostate<State, Action> state) {
        state.live = false;
        state.inOpen = false;
        state.controlled = true; // we assume the state is controlled until an uncontrollable recommendation is obtained
        state.targets = emptyList();
    }
}
