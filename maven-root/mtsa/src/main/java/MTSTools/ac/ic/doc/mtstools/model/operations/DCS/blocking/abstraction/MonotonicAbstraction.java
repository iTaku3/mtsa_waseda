package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.abstraction;

import MTSTools.ac.ic.doc.commons.collections.InitMap;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.*;

import java.util.*;

/**
 * This class implements the Monotonic Abstraction (MA).
 */
public class MonotonicAbstraction<State, Action> implements Abstraction<State, Action> {

    private final TransitionSet<State, Action> base;
    private final List<LTS<State, Action>> ltss;
    private final Alphabet<Action> alphabet;
    /**
     * Set of enabled events (ready in each LTS).
     */
    private TransitionSet<State, Action> allReady;

    /**
     * Set of fresh states discovered during an iteration.
     */
    private Set<HState<State, Action>> freshStates;

    /**
     * Set of fresh actions discovered during an iteration.
     */
    private Set<HAction<Action>> freshActions;

    /**
     * Mapping of actions to the generation index in which they were first included.
     */
    private Map<HAction<Action>, Integer> actionGenerations;

    /**
     * Mapping of states to the generation index in which they were first included.
     */
    private Map<HState<State, Action>, Integer> stateGenerations;

    /**
     * Mapping of pending transitions, that is, not yet enabled event that lead to different states.
     */
    private Map<HAction<Action>, Set<HState<State, Action>>> pending;

    /**
     * List of selected LTSs to evaluate.
     */
    private List<Integer> selected;

    /**
     * Mapping of actions to estimates (the result of evaluating the abstraction).
     */
    private Map<HAction<Action>, HEstimate> estimates;

    /**
     * Mapping of states to the reachable states and the actions leading to them plus in how many steps.
     */
    private Map<HState<State, Action>, Map<HState<State, Action>, Map<HAction<Action>, Integer>>> paths;

    /**
     * Cache of heuristic states ordered by creation order (used for perfect hashing).
     */
    private final List<HState<State, Action>> stash;

    /**
     * Cache of heuristic states used to reduce dynamic allocations.
     */
    private final Map<Integer, HashMap<State, Integer>> cache;
    private final List<Set<State>> defaultTargets;

    /**
     * Constructor for the MA.
     */
    public MonotonicAbstraction(List<LTS<State, Action>> ltss,
                                List<Set<State>> defaultTargets,
                                TransitionSet<State, Action> base,
                                Alphabet<Action> alphabet) {
        freshStates = new HashSet<>();
        freshActions = new HashSet<>();
        actionGenerations = new HashMap<>();
        stateGenerations = new HashMap<>();
        pending = new InitMap<>(HashSet.class);
        selected = new ArrayList<>(ltss.size());
        estimates = new InitMap<>(() -> new HEstimate(ltss.size(), HDist.chasm));
        paths = new InitMap<>(HashMap.class);
        stash = new ArrayList<>(ltss.size());
        cache = new HashMap<>();
        this.defaultTargets = defaultTargets;
        this.base = base;
        this.ltss = ltss;
        this.alphabet = alphabet;
    }


    /**
     * Clears the interal data kept by the MA.
     */
    public void clear() {
        allReady = base.clone();
        freshStates.clear();
        actionGenerations.clear();
        stateGenerations.clear();
        pending.clear();
        selected.clear();
        estimates.clear();
        paths.clear();
    }


    /**
     * Performs the heuristic evaluation by building the MA.
     */
    @Override
    public void eval(Compostate<State, Action> compostate) {
        if (!compostate.isEvaluated()) {
            clear();
            buildMA(compostate);
            evaluateMA(compostate);
            extractRecommendations(compostate);
        }
    }


    /**
     * Build the MA from a given state.
     */
    public void buildMA(Compostate<State, Action> compostate) {
        for (int lts = 0; lts < ltss.size(); ++lts)
            freshStates.add(buildHState(lts, compostate.getStates().get(lts)));
        int iteration = 0;
        update(iteration);
        while (!freshStates.isEmpty()) {
            ++iteration;
            freshStates.clear();
            for (HAction<Action> action : freshActions) {
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


    /**
     * Updates the fresh states for the given iteration.
     */
    private void update(int iteration) {
        for (HState<State, Action> s : freshStates) {
            stateGenerations.put(s, iteration);
            LTS<State, Action> current = ltss.get(s.lts);
            for (Pair<Action, State> transition : current.getTransitions(s.state)) {
                HState<State, Action> d = buildHState(s.lts, transition.getSecond());
                HAction<Action> l = alphabet.getHAction(transition.getFirst());
                pending.get(l).add(d);
                allReady.add(s.lts, l);
                if (!actionGenerations.containsKey(l) && allReady.contains(l)) {
                    actionGenerations.put(l, iteration);
                    freshActions.add(l);
                }
            }
        }
    }


    /**
     * Evaluates the MA populating the estimates table.
     */
    public void evaluateMA(Compostate<State, Action> compostate) {
        estimates.clear();

        boolean targeted = select(compostate);
        if (selected.isEmpty())
            return;

        for (Integer lts : selected) {
            HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
            Map<HState<State, Action>, Map<HAction<Action>, Integer>> pathsFromSource = paths.get(s);
            for (HState<State, Action> t : pathsFromSource.keySet()) {
                if (!t.marked/*isMarked()*/)
                    continue;
                Map<HAction<Action>, Integer> pathsToTarget = pathsFromSource.get(t);
                for (Map.Entry<HAction<Action>, Integer> entry : pathsToTarget.entrySet()) {
                    HAction<Action> l = entry.getKey();
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


    /**
     * Selects the LTSs to consider in the MA.
     */
    private boolean select(Compostate<State, Action> compostate) {
        computePaths();
        for (int lts = 0; lts < ltss.size(); ++lts) {
            HState<State, Action> state = buildHState(lts, compostate.getStates().get(lts));
            boolean isTarget = false, isMarked = false;
            for (HState<State, Action> target : paths.get(state).keySet()) {
                isMarked |= target.marked/*isMarked()*/;
                isTarget |= compostate.getTargets(lts).contains(target.state);
            }
            if (isTarget || isMarked) selected.add(lts);
        }
        return selected.size() != 0;
    }


    /**
     * Reconcilates the estimates for the LTS which have not been analyzed.
     */
    private void reconcilateCrossLTS(Compostate<State, Action> compostate) {
        for (int lts = 0; lts < ltss.size(); ++lts) {
            HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
            for (HAction<Action> l : compostate.getTransitions()) {
                if (!selected.contains(lts))
                    estimates.get(l).set(lts, HDist.zero);
                if (!ltss.get(lts).getActions().contains(l.getAction()) || s.isSelfLoop(l)) {
                    HDist lDist = estimates.get(l).get(lts);
                    if (lDist.compareTo(HDist.chasm) == 0)
                        estimates.get(l).set(lts, HDist.zero);
                }
            }
        }
    }


    /**
     * Extracts recommendations for the given state using the computed estimates.
     */
    private void extractRecommendations(Compostate<State, Action> compostate) {
        compostate.setupRecommendations();
        for (HAction<Action> action : compostate.getTransitions()) {
            HEstimate estimate = estimates.get(action);
            estimate.reduceMax();
            if (compostate.addRecommendation(action, estimate))
                break;
        }
        compostate.rankRecommendations();
        compostate.initRecommendations();
    }


    /**
     * Returns the estimated distance that a transition adds to a path.
     */
    private Integer dist(HState<State, Action> source, HAction<Action> label) {
        if (!stateGenerations.containsKey(source) || !actionGenerations.containsKey(label))
            return DirectedControllerSynthesisBlocking.INF;
        return 1 + Math.max(0, actionGenerations.get(label) - stateGenerations.get(source));
    }


    /**
     * Returns the minimum distance from the valid transitions from a given state.
     */
    private Integer minDist(HState<State, Action> source, Set<HAction<Action>> labels) {
        int result = DirectedControllerSynthesisBlocking.INF;
        for (HAction<Action> action : labels)
            result = Math.min(result, dist(source, action));
        return result;
    }


    /**
     * Adds to distances (maxing at overflows).
     */
    private Integer addDist(Integer d1, Integer d2) {
        if (d1 == DirectedControllerSynthesisBlocking.INF || d2 == DirectedControllerSynthesisBlocking.INF)
            return DirectedControllerSynthesisBlocking.INF;
        return d1 + d2;
    }


    /**
     * Computes the paths in the abstraction (the distance between states).
     */
    private void computePaths() {
        Map<HState<State, Action>, Map<HState<State, Action>, Set<HAction<Action>>>> oneStepReachableStates
                = new InitMap<>(HashMap.class);
        Map<HState<State, Action>, Set<Pair<HAction<Action>, HState<State, Action>>>> lastStates
                = new InitMap<>(HashSet.class);
        Map<HState<State, Action>, Set<Pair<HAction<Action>, HState<State, Action>>>> nextStates
                = new InitMap<>(HashSet.class);
        boolean statesPopulated = false;

        Map<HState<State, Action>, Map<HAction<Action>, Integer>> manyStepsReachableFromSource;

        for (int lts = 0; lts < ltss.size(); ++lts) {
            for (State state : ltss.get(lts).getStates()) {
                HState<State, Action> source = buildHState(lts, state);
                manyStepsReachableFromSource = paths.get(source);
                Map<HState<State, Action>, Set<HAction<Action>>> oneReachableFromSoruce = oneStepReachableStates.get(source);
                for (Pair<Action, State> transition : ltss.get(lts).getTransitions(state)) {
                    HAction<Action> label = alphabet.getHAction(transition.getFirst());
                    HState<State, Action> destination = buildHState(lts, transition.getSecond());
                    Set<HAction<Action>> oneStepToDestination = oneReachableFromSoruce.get(destination);
                    Map<HAction<Action>, Integer> manyStepsToDestination = manyStepsReachableFromSource.get(destination);
                    if (oneStepToDestination == null) {
                        oneReachableFromSoruce.put(destination, oneStepToDestination = new HashSet<>());
                        manyStepsReachableFromSource.put(destination, manyStepsToDestination = new HashMap<>());
                    }
                    Integer newDist = dist(source, label);
                    if (newDist != DirectedControllerSynthesisBlocking.INF) { //!!!
                        DirectedControllerSynthesisBlocking.putmin(manyStepsToDestination, label, newDist);
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
                for (Pair<HAction<Action>, HState<State, Action>> pair : lastStates.get(source)) {
                    HAction<Action> label = pair.getFirst();
                    HState<State, Action> intermediate = pair.getSecond();
                    Integer distIntermediate = manyStepsReachableFromSource.get(intermediate).get(label);
                    for (HState<State, Action> target : oneStepReachableStates.get(intermediate).keySet()) {
                        Map<HAction<Action>, Integer> manyStepsReachableFromSourceToTarget
                                = manyStepsReachableFromSource.computeIfAbsent(target, k -> new HashMap<>());
                        Integer newDist = addDist(distIntermediate, minDist(intermediate,
                                oneStepReachableStates.get(intermediate).get(target)));
                        if (newDist != DirectedControllerSynthesisBlocking.INF) { //!!!
                            Integer current = manyStepsReachableFromSourceToTarget.get(label);
                            if (current == null || current > newDist) {
                                manyStepsReachableFromSourceToTarget.put(label, newDist);
                                statesPopulated |= nextStates.get(source).add(Pair.create(label, target));
                            }
                        }
                    }
                }
            }
            for (Set<Pair<HAction<Action>, HState<State, Action>>> set : lastStates.values())
                set.clear();
            Map<HState<State, Action>, Set<Pair<HAction<Action>, HState<State, Action>>>> swap = lastStates;
            lastStates = nextStates;
            nextStates = swap;
        }
    }

    /**
     * Builds (or retrieves from cache) an heuristic state.
     */
    private HState<State, Action> buildHState(int lts, State state) {
        HashMap<State, Integer> table = cache.computeIfAbsent(lts, k -> new HashMap<>());
        Integer index = table.get(state);
        if (index == null) {
            HState<State, Action> hstate = new HState<>(lts, state, this.stash.size(), this.defaultTargets.get(lts).contains(state), ltss);
            stash.add(hstate);
            index = hstate.hashCode();
            table.put(state, index);
        }
        return stash.get(index);
    }
}
