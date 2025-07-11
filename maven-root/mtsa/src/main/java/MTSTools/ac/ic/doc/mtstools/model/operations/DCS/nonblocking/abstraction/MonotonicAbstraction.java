package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import MTSTools.ac.ic.doc.commons.collections.InitMap;
import MTSTools.ac.ic.doc.commons.collections.InitMap.Factory;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.Compostate;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.DirectedControllerSynthesisNonBlocking;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.TransitionSet;

/** This class implements the Monotonic Abstraction (MA). */
public class MonotonicAbstraction<State, Action> extends Abstraction<State, Action> {

    /**
     *
     */
    private final DirectedControllerSynthesisNonBlocking<State, Action> directedControllerSynthesisNonBlocking;

    /** Set of enabled events (ready in each LTS). */
    private TransitionSet<State, Action> allReady;

    /** Set of fresh states discovered during an iteration. */
    private Set<HState<State, Action>> freshStates;

    /** Set of fresh actions discovered during an iteration. */
    private Set<HAction<State, Action>> freshActions;

    /** Mapping of actions to the generation index in which they were first included. */
    private Map<HAction<State, Action>, Integer> actionGenerations;

    /** Mapping of states to the generation index in which they were first included. */
    private Map<HState<State, Action>, Integer> stateGenerations;

    /** Mapping of pending transitions, that is, not yet enabled event that lead to different states. */
    private Map<HAction<State, Action>, Set<HState<State, Action>>> pending;

    /** List of selected LTSs to evaluate. */
    private List<Integer> selected;

    /** Mapping of actions to estimates (the result of evaluating the abstraction). */
    private Map<HAction<State, Action>, HEstimate<State, Action>> estimates;

    /** Mapping of states to the reachable states and the actions leading to them plus in how many steps. */
    private Map<HState<State, Action>, Map<HState<State, Action>, Map<HAction<State, Action>, Integer>>> paths;

    /** Cache of heuristic states ordered by creation order (used for perfect hashing). */
    public List<HState<State, Action>> stash;

    /** Cache of heuristic states used to reduce dynamic allocations. */
    private Map<Integer, HashMap<State, Integer>> cache;


    /** Constructor for the MA.
     * @param directedControllerSynthesisNonBlocking TODO*/
    public MonotonicAbstraction(DirectedControllerSynthesisNonBlocking<State, Action> directedControllerSynthesisNonBlocking) {
        this.directedControllerSynthesisNonBlocking = directedControllerSynthesisNonBlocking;
        freshStates = new HashSet<>();
        freshActions = new HashSet<>();
        actionGenerations = new HashMap<>();
        stateGenerations = new HashMap<>();
        pending = new InitMap<>(HashSet.class);
        selected = new ArrayList<>(this.directedControllerSynthesisNonBlocking.ltssSize);
        estimates = new InitMap<>(new Factory<HEstimate<State, Action>>() {
            @Override
            public HEstimate<State, Action> newInstance() {
                return new HEstimate<State, Action>(MonotonicAbstraction.this.directedControllerSynthesisNonBlocking.ltssSize, HDist.chasm);
            }
        });
        paths = new InitMap<>(HashMap.class);
        stash = new ArrayList<>(directedControllerSynthesisNonBlocking.ltss.size());
        cache = new HashMap<>();
    }


    /** Clears the interal data kept by the MA. */
    public void clear() {
        allReady = this.directedControllerSynthesisNonBlocking.base.clone();
        freshStates.clear();
        actionGenerations.clear();
        stateGenerations.clear();
        pending.clear();
        selected.clear();
        estimates.clear();
        paths.clear();
    }


    /** Performs the heuristic evaluation by building the MA. */
    @Override
    public void eval(Compostate<State, Action> compostate, List<Set<State>> knownMarked, List<Set<State>> goals, List<String> comparison) {
        if (!compostate.isEvaluated()) {
            clear();
            buildMA(compostate);
            evaluateMA(compostate);
            extractRecommendations(compostate);
        }
    }


    /** Build the MA from a given state. */
    public void buildMA(Compostate<State, Action> compostate) {
        for (int lts = 0; lts < this.directedControllerSynthesisNonBlocking.ltssSize; ++lts)
            freshStates.add(buildHState(lts, compostate.getStates().get(lts)));
        int iteration = 0;
        update(iteration);
        while (!freshStates.isEmpty()) {
            ++iteration;
            freshStates.clear();
            for (HAction<State, Action> action : freshActions) {
                for (HState<State, Action> state : pending.get(action)) {
                    if (!stateGenerations.containsKey(state))
                        freshStates.add(state);
                }
                pending.get(action).clear();
            }
            freshActions.clear();
            update(iteration);
        }
    }


    /** Updates the fresh states for the given iteration. */
    private void update(int iteration) {
        for (HState<State, Action> s : freshStates) {
            stateGenerations.put(s, iteration);
            LTS<State, Action> current = this.directedControllerSynthesisNonBlocking.ltss.get(s.lts);
            for (Pair<Action,State> transition : current.getTransitions(s.state)) {
                HState<State, Action> d = buildHState(s.lts, transition.getSecond());
                HAction<State, Action> l = this.directedControllerSynthesisNonBlocking.alphabet.getHAction(transition.getFirst());
                pending.get(l).add(d);
                allReady.add(s.lts, l);
                if (!actionGenerations.containsKey(l) && allReady.contains(l)) {
                    actionGenerations.put(l, iteration);
                    freshActions.add(l);
                }
            }
        }
    }


    /** Evaluates the MA populating the estimates table. */
    public void evaluateMA(Compostate<State, Action> compostate) {
        estimates.clear();

        boolean targeted = select(compostate);
        if (selected.isEmpty())
            return;

        for (Integer lts : selected) {
            HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
            Map<HState<State, Action>, Map<HAction<State, Action>, Integer>> pathsFromSource = paths.get(s);
            for (HState<State, Action> t : pathsFromSource.keySet()) {
                if (!t.marked/*isMarked()*/)
                    continue;
                Map<HAction<State, Action>, Integer> pathsToTarget = pathsFromSource.get(t);
                for (Entry<HAction<State, Action>, Integer> entry : pathsToTarget.entrySet()) {
                    HAction<State, Action> l = entry.getKey();
                    Integer d = entry.getValue();
                    Integer m = targeted && compostate.getTargets(lts).contains(t.state) ? 0 : 1;
                    HDist newDist = new HDist(m, d);
                    HDist lDist = estimates.get(l).get(lts);
                    if (newDist.compareTo(lDist) < 0)
                        estimates.get(l).set(lts, newDist);
                }
            }
        }

        reconcilateCrossLTS(compostate);
    }


    /** Selects the LTSs to consider in the MA. */
    private boolean select(Compostate<State, Action> compostate) {
        boolean includeTargets = true;
        computePaths();
        for (int lts = 0; lts < this.directedControllerSynthesisNonBlocking.ltssSize; ++lts) {
            HState<State, Action> state = buildHState(lts, compostate.getStates().get(lts));
            boolean isTarget = false, isMarked = false;
            for (HState<State, Action> target : paths.get(state).keySet()) {
                isMarked |= target.marked/*isMarked()*/;
                isTarget |= compostate.getTargets(lts).contains(target.state);
            }
            if (isTarget) {
                if (includeTargets)
                    selected.add(lts);
            } else if (isMarked) {
                if (includeTargets) {
                    selected.clear();
                    includeTargets = false;
                }
                selected.add(lts);
            } else {
                selected.clear();
                break;
            }
        }
        return includeTargets;
    }


    /** Reconcilates the estimates for the LTS which have not been analyzed. */
    private void reconcilateCrossLTS(Compostate<State, Action> compostate) {
        for (int lts = 0; lts < this.directedControllerSynthesisNonBlocking.ltssSize; ++lts) {
            HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
            for (HAction<State, Action> l : compostate.getTransitions()) {
                if (!selected.contains(lts))
                    estimates.get(l).set(lts, HDist.zero);
                if (!this.directedControllerSynthesisNonBlocking.ltss.get(lts).getActions().contains(l.getAction()) || s.isSelfLoop(l)) {
                    HDist lDist = estimates.get(l).get(lts);
                    if (lDist.compareTo(HDist.chasm) == 0)
                        estimates.get(l).set(lts, HDist.zero);
                }
            }
        }
    }


    /** Extracts recommendations for the given state using the computed estimates. */
    private void extractRecommendations(Compostate<State, Action> compostate) {
        //compostate.setupRecommendations(comparison);
        for (HAction<State, Action> action : compostate.getTransitions()) {
            HEstimate<State, Action> estimate = estimates.get(action);
            estimate.reduceMax();
            if (compostate.addRecommendation(action, estimate))
                break;
        }
        compostate.rankRecommendations();
        compostate.initRecommendations();
    }


    /** Returns the estimated distance that a transition adds to a path. */
    private Integer dist(HState<State, Action> source, HAction<State, Action> label) {
        if (!stateGenerations.containsKey(source) || !actionGenerations.containsKey(label))
            return DirectedControllerSynthesisNonBlocking.INF;
        return 1 + Math.max(0, actionGenerations.get(label) - stateGenerations.get(source));
    }


    /** Returns the minimum distance from the valid transitions from a given state. */
    private Integer minDist(HState<State, Action> source, Set<HAction<State, Action>> labels) {
        int result = DirectedControllerSynthesisNonBlocking.INF;
        for (HAction<State, Action> action : labels)
            result = Math.min(result, dist(source, action));
        return result;
    }


    /** Adds to distances (maxing at overflows). */
    private Integer addDist(Integer d1, Integer d2) {
        if (d1 == DirectedControllerSynthesisNonBlocking.INF || d2 == DirectedControllerSynthesisNonBlocking.INF)
            return DirectedControllerSynthesisNonBlocking.INF;
        return d1 + d2;
    }


    /** Computes the paths in the abstraction (the distance between states). */
    private void computePaths() {
        Map<HState<State, Action>, Map<HState<State, Action>, Set<HAction<State, Action>>>> oneStepReachableStates = new InitMap<>(HashMap.class);
        Map<HState<State, Action>, Set<Pair<HAction<State, Action>,HState<State, Action>>>> lastStates = new InitMap<>(HashSet.class);
        Map<HState<State, Action>, Set<Pair<HAction<State, Action>,HState<State, Action>>>> nextStates = new InitMap<>(HashSet.class);
        boolean statesPopulated = false;

        Map<HState<State, Action>, Map<HAction<State, Action>, Integer>> manyStepsReachableFromSource;

        for (int lts = 0; lts < this.directedControllerSynthesisNonBlocking.ltssSize; ++lts) {
            for (State state : this.directedControllerSynthesisNonBlocking.ltss.get(lts).getStates()) {
                HState<State, Action> source = buildHState(lts, state);
                manyStepsReachableFromSource = paths.get(source);
                Map<HState<State, Action>, Set<HAction<State, Action>>> oneReachableFromSoruce = oneStepReachableStates.get(source);
                for (Pair<Action, State> transition : this.directedControllerSynthesisNonBlocking.ltss.get(lts).getTransitions(state)) {
                    HAction<State, Action> label = this.directedControllerSynthesisNonBlocking.alphabet.getHAction(transition.getFirst());
                    HState<State, Action> destination = buildHState(lts, transition.getSecond());
                    Set<HAction<State, Action>> oneStepToDestination = oneReachableFromSoruce.get(destination);
                    Map<HAction<State, Action>, Integer> manyStepsToDestination = manyStepsReachableFromSource.get(destination);
                    if (oneStepToDestination == null) {
                        oneReachableFromSoruce.put(destination, oneStepToDestination = new HashSet<>());
                        manyStepsReachableFromSource.put(destination, manyStepsToDestination = new HashMap<>());
                    }
                    Integer newDist = dist(source, label);
                    if (newDist != DirectedControllerSynthesisNonBlocking.INF) { //!!!
                        DirectedControllerSynthesisNonBlocking.putmin(manyStepsToDestination, label, newDist);
                        oneStepToDestination.add(label);
                        statesPopulated |= lastStates.get(source).add(Pair.create(label, destination));
                    }
                }
            }
        }

        while (statesPopulated) {
            statesPopulated = false;
            for (HState<State, Action> source : lastStates.keySet()) {
                manyStepsReachableFromSource = paths.get(source);
                for (Pair<HAction<State, Action>, HState<State, Action>> pair : lastStates.get(source)) {
                    HAction<State, Action> label = pair.getFirst();
                    HState<State, Action> intermediate = pair.getSecond();
                    Integer distIntermediate = manyStepsReachableFromSource.get(intermediate).get(label);
                    for (HState<State, Action> target : oneStepReachableStates.get(intermediate).keySet()) {
                        Map<HAction<State, Action>, Integer> manyStepsReachableFromSourceToTarget = manyStepsReachableFromSource.get(target);
                        if (manyStepsReachableFromSourceToTarget == null)
                            manyStepsReachableFromSource.put(target, manyStepsReachableFromSourceToTarget = new HashMap<>());
                        Integer newDist = addDist(distIntermediate, minDist(intermediate, oneStepReachableStates.get(intermediate).get(target)));
                        if (newDist != DirectedControllerSynthesisNonBlocking.INF) { //!!!
                            Integer current = manyStepsReachableFromSourceToTarget.get(label);
                            if (current == null || current > newDist) {
                                manyStepsReachableFromSourceToTarget.put(label, newDist);
                                statesPopulated |= nextStates.get(source).add(Pair.create(label, target));
                            }
                        }
                    }
                }
            }
            for (Set<Pair<HAction<State, Action>,HState<State, Action>>> set : lastStates.values())
                set.clear();
            Map<HState<State, Action>, Set<Pair<HAction<State, Action>,HState<State, Action>>>> swap = lastStates;
            lastStates = nextStates;
            nextStates = swap;
        }
    }

    /** Builds (or retrieves from cache) an heuristic state. */
    public HState<State, Action> buildHState(int lts, State state) {
        HashMap<State, Integer> table = cache.computeIfAbsent(lts, k -> new HashMap<>());
        Integer index = table.get(state);
        if (index == null) {
            HState<State, Action> hstate = new HState<State, Action>(
                    lts,
                    state,
                    this.stash.size(),
                    this.directedControllerSynthesisNonBlocking.defaultTargets.get(lts).contains(state),
                    this.directedControllerSynthesisNonBlocking.ltss);

            stash.add(hstate);
            index = hstate.hashCode();
            table.put(state, index);
        }
        return stash.get(index);
    }
}