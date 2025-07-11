package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import static java.util.Collections.emptyList;

import java.util.ArrayList;

import static java.util.Collections.emptyList;

/**
 * This class implements ExplorationHeuristic by completely relying on the underlying heuristic
 * to rank the states/transitions of the entire frontier. In contrast to OpenSetExplorationHeuristic,
 * it does not restrict the states of the frontier in any way.
 */
public class CompleteFrontierExplorationHeuristic<State, Action> implements ExplorationHeuristic<State, Action> {

    /** All the states of the frontier (only NONE, of course), ranked by the heursitic abstraction. */
    Queue<Compostate<State, Action>> frontier;

    /** Abstraction used to rank the transitions from a state. */
    Abstraction<State,Action> abstraction;

    DirectedControllerSynthesisNonBlocking<State,Action> dcs;

    private final Comparator<Compostate<State, Action>> compostateRanker;

    private List<Set<State>> knownMarked;
    private List<Set<State>> goals;

    /** If true, the estimates for a state are recomputed every time they may change: when a new marked state or goal is found. */
    // Ver Tesis de Nico Pazos para más detalles, capítulo "Mejoras propuestas"
    private final static Boolean RECOMPUTE_ESTIMATES_ON_CHANGE = true;

    /** seq (as in 'sequence') is a number that is incremented every time a marked state or a goal is reached.
     * When the estimates for a state are computed, the seq is stored in the state; if at some point a state's
     * seq is lower than the current seq, it means that the state's estimates are outdated and need to be recomputed.
     */
    public Integer seq = 0;
    

    public CompleteFrontierExplorationHeuristic(
            DirectedControllerSynthesisNonBlocking<State,Action> dcs,
            DirectedControllerSynthesisNonBlocking.HeuristicMode mode) {

        Comparator<Compostate<State, Action>> compostateRanker = new DefaultCompostateRanker<>();
        this.dcs = dcs;
        this.knownMarked = new ArrayList<>(dcs.ltssSize);
        this.goals = new ArrayList<>(dcs.ltssSize);
        for (int lts = 0; lts < dcs.ltssSize; ++lts){
            this.knownMarked.add(new HashSet<>());
            this.goals.add(new HashSet<>());
        }

        switch (mode){
            case Monotonic:
                abstraction = new MonotonicAbstraction<>(dcs);
                break;
            case Ready:
                abstraction = new ReadyAbstraction<>(dcs.ltss, dcs.defaultTargets, dcs.alphabet);
                compostateRanker = new ReadyAbstraction.CompostateRanker<>();
                break;
            case BFS:
                abstraction = new BFSAbstraction<>();
                break;
            case Debugging:
                abstraction = new DebuggingAbstraction<>();
                break;
        }
        this.compostateRanker = compostateRanker;
        frontier = new PriorityQueue<>(compostateRanker);
    }

    public Pair<Compostate<State,Action>, HAction<State,Action>> getNextAction(List<String> comparison) {
        Compostate<State,Action> state = getNextState(comparison);
        Recommendation<State, Action> recommendation = state.nextRecommendation();
        return new Pair<>(state, recommendation.getAction());
    }

    public Compostate<State,Action> getNextState(List<String> comparison) {
        recomputeEstimates(comparison);
        removeNotLive();
        Compostate<State,Action> state = frontier.remove();
        state.inOpen = false;
        return state;
    }

    private void recomputeEstimates(List<String> comparison) {
        if (!RECOMPUTE_ESTIMATES_ON_CHANGE) return;
        // TODO: creo que se puede optimizar esto, y no usar el .seq para nada.
        // Tener un field bool "dirty" que se setea en "true" al encontrar un marked/goal,
        // y se setean en "false" al final de este método.
        boolean update = false;
        for (Compostate<State,Action> state : this.frontier) {
            if (state.seq < this.seq) {
                update = true;
                break;
            }
        }

        if (update) {
            Queue<Compostate<State, Action>> newFrontier = new PriorityQueue<>(this.compostateRanker);
            for (Compostate<State,Action> state : this.frontier) {
                if (fullyExplored(state) || !state.isLive()) {
                    continue;
                }
                if (state.seq < this.seq) {
                    state.clearRecommendations();
                    state.recommendations = null;
                    abstraction.eval(state, this.knownMarked, this.goals, comparison);
                    state.seq = this.seq;
                }
                newFrontier.add(state);
            }
            this.frontier = newFrontier;
        }
    }

    private void removeNotLive() {
        while (!frontier.isEmpty() && (
            !frontier.peek().isStatus(Status.NONE) ||
            fullyExplored(frontier.peek()) ||
            !frontier.peek().isLive()
        )) {
            frontier.remove();
        }
    }
    private void maybeAddToFrontier(Compostate<State, Action> state) {
        if (state.isStatus(Status.NONE) && !fullyExplored(state) && !state.inOpen) {
            state.inOpen = true;
            state.live = true;
            this.frontier.add(state);
        }
    }

    public boolean somethingLeftToExplore() {
        removeNotLive();
        return !frontier.isEmpty();
    }

    public void setInitialState(Compostate<State, Action> state, List<String> comparison) {
        newState(state, null, comparison);
        maybeAddToFrontier(state);
    }

    public void newState(Compostate<State, Action> state, Compostate<State, Action> parent, List<String> comparison) {
        if(parent != null){
            state.setTargets(parent.getTargets());
        }
        if (state.marked) {
            this.seq++;
            state.addTargets(state);
            for (int lts = 0; lts < dcs.ltssSize; ++lts)
                this.knownMarked.get(lts).add(state.getStates().get(lts));
        }
        abstraction.eval(state, this.knownMarked, this.goals, comparison);
        state.seq = this.seq;
    }

    public void notifyExpandingState(Compostate<State, Action> parent, HAction<State, Action> action, Compostate<State, Action> state) {
        if(state.wasExpanded()){ // todo: understand this, i am copying the behavior of the code pre refactor
            state.setTargets(parent.getTargets());
            if (state.marked)
                state.addTargets(state);
        }
    }

    public void notifyStateSetErrorOrGoal(Compostate<State, Action> state) {
        state.live = false;
        state.clearRecommendations();
        if (state.isStatus(Status.GOAL)) {
            this.seq++;
            for (int lts = 0; lts < dcs.ltssSize; ++lts)
                this.goals.get(lts).add(state.getStates().get(lts));
        }
    }

    public void expansionDone(Compostate<State, Action> state, HAction<State, Action> action, Compostate<State, Action> child) {
        maybeAddToFrontier(state);
        maybeAddToFrontier(child);
    }
    public void notifyExpansionDidntFindAnything(Compostate<State, Action> parent, HAction<State, Action> action, Compostate<State, Action> child) {
    }
    public void notifyStateIsNone(Compostate<State, Action> state) {
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
