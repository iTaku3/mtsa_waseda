package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.abstraction;

import MTSTools.ac.ic.doc.commons.collections.BidirectionalMap;
import MTSTools.ac.ic.doc.commons.collections.InitMap;
import MTSTools.ac.ic.doc.commons.collections.QueueSet;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.*;

import java.util.*;

/** This class implements the Ready Abstraction (RA). */
public class ReadyAbstraction<State, Action> implements Abstraction<State, Action> {

    /** Set of vertices in the RA graph. */
    private Set<HAction<Action>> vertices;

    /** Edges in the RA graph. */
    private BidirectionalMap<HAction<Action>, HAction<Action>> edges;

    /** Mapping of estimates for actions (the result of evaluating the abstraction). */
    private Map<HAction<Action>, HEstimate> estimates;

    /** The minimum estimate per LTS. */
    private Map<Integer, HDist> shortest;

    /** Fresh states discovered at each iteration. */
    private QueueSet<HAction<Action>> fresh;

    private Map<HAction<Action>, Set<Integer>> readyInLTS;

    /** Mapping of actions to LTSs containing them in their alphabets. */
    private Map<HAction<Action>, Set<Integer>> actionsToLTS;

    /** Maps for each state which other states can be reached.
     *  It also stores which actions lead from source to destination and in how many local steps. */
    private Map<HState<State, Action>, Map<HState<State, Action>, Map<HAction<Action>, Integer>>> manyStepsReachableStates;

    /** Subset of the manyStepsReachableState map, containing only which
     *  marked states can be reached from a given source. This map keeps
     *  aliasing/sharing with the other and it is used only to speed up
     *  iteration while looking for marked states to reach. */
    private Map<HState<State, Action>, Map<HState<State, Action>, Map<HAction<Action>, Integer>>> markedReachableStates;

    /** Maps for each state which actions can be reached.
     *  It also stores which actions lead from source to desired action and in how many local steps. */
    private Map<HState<State, Action>, Map<HAction<Action>, Map<HAction<Action>, Integer>>> manyStepsReachableActions;

    /** Cache of target distances. */
    private Map<Integer, HDist> m0Cache;

    /** Cache of marked distances. */
    private Map<Integer, HDist> m1Cache;

    /** Cache of gaps between actions. */
    private Map<HAction<Action>, Map<HAction<Action>, Integer>> gapCache;

    /** Cache of heuristic states ordered by creation order (used for perfect hashing). */
    private final List<HState<State, Action>> stash;

    /** Cache of heuristic states used to reduce dynamic allocations. */
    private final Map<Integer, HashMap<State, Integer>> cache;
    private final List<LTS<State, Action>> ltss;
    private final Alphabet<Action> alphabet;
    private final List<Set<State>> defaultTargets;


    /** Constructor for the RA. */
    public ReadyAbstraction(List<LTS<State, Action>> ltss, List<Set<State>> defaultTargets, Alphabet<Action> alphabet) {
        this.ltss = ltss;
        this.alphabet = alphabet;
        this.defaultTargets = defaultTargets;
        stash = new ArrayList<>(ltss.size());
        cache = new HashMap<>();

        vertices = new HashSet<>();
        edges = new BidirectionalMap<>();
        estimates = new InitMap<>(() -> {
            HEstimate result = new HEstimate(ltss.size() +1, HDist.chasm);
            result.values.remove(ltss.size());
            return result;
        });
        shortest = new InitMap<>(HDist.chasmFactory);
        fresh = new QueueSet<>();
        actionsToLTS = new InitMap<>(HashSet.class);
        readyInLTS = new InitMap<>(HashSet.class);
        manyStepsReachableStates = new InitMap<>(HashMap.class);
        markedReachableStates = new InitMap<>(HashMap.class);
        manyStepsReachableActions = new InitMap<>(HashMap.class);
        m0Cache = new HashMap<>();
        m1Cache = new HashMap<>();
        gapCache = new InitMap<>(HashMap.class);
        init();
    }


    /** Initializes the RA precomputing tables. */
    private void init() {
        computeActionsToLTS();
        computeReachableStates();
        computeMarkedReachableStates();
        computeReachableActions();
    }


    /** Clears the RA internal state. */
    private void clear() {
        vertices.clear();
        edges.clear();
        estimates.clear();
        shortest.clear();
        fresh.clear();
        readyInLTS.clear();
        gapCache.clear();
    }


    /** Evaluates the abstraction by building and exploring the RA. */
    @Override
    public void eval(Compostate<State, Action> compostate) {
        if (!compostate.isEvaluated()) {
            clear();
            buildRA(compostate);
            evaluateRA(compostate);
            extractRecommendations(compostate);
        }
    }


    /** Builds the RA by connecting ready events through edges indicating their causal relationship. */
    private void buildRA(Compostate<State, Action> compostate) {
        for (int lts = 0; lts < ltss.size(); ++lts) {
            HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
            for (Pair<Action,State> transition : s.getTransitions()) {
                HAction<Action> action = alphabet.getHAction(transition.getFirst());
                if (!s.state.equals(transition.getSecond())) { // !s.isSelfLoop(action)
                    readyInLTS.get(action).add(lts);
                    vertices.add(action);
                }
            }
        }
        for (HAction<Action> t : vertices) {
            for (Integer lts : actionsToLTS.get(t)) {
                HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
                Map<HAction<Action>, Integer> actionsLeadingToTfromS = manyStepsReachableActions.get(s).get(t);
                if (actionsLeadingToTfromS != null) {
                    for (HAction<Action> l : actionsLeadingToTfromS.keySet()) {
                        if (!l.equals(t) && s.contains(l) && !s.isSelfLoop(l)) { // we need an efficient s.contains(l) that returns false for self-loops
                            edges.put(l, t);
                        }
                    }
                }
            }
        }
    }


    /** Evaluates the RA by exploring the graph and populating the estimates table. */
    private void evaluateRA(Compostate<State, Action> compostate) {
        for (int lts = 0; lts < ltss.size(); ++lts) {
            HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
            Set<State> markedStates = defaultTargets.get(lts);
            Set<State> targetStates = compostate.getTargets(lts);
            Map<HState<State, Action>, Map<HAction<Action>, Integer>> markedReachableStatesFromSource
                    = markedReachableStates.get(s);
            for (Pair<Action,State> transitions : s.getTransitions()) {
                HAction<Action> l = alphabet.getHAction(transitions.getFirst());
                State t = transitions.getSecond();
                if (t.equals(-1L))
                    continue;
                int mt = 2, dt = DirectedControllerSynthesisBlocking.INF;
                if (markedStates.contains(t)) {
                    mt = targetStates.contains(t) ? 0 : 1;
                    dt = 1;
                }
                if (!(mt == 0 || (mt == 1 && targetStates.isEmpty()))) { // already best, skip search
                    if (s.state.equals(t)) // a self-loop
                        continue;
                    for (HState<State, Action> g : markedReachableStatesFromSource.keySet()) { // search for best
                        int mg = targetStates.contains(g.state) ? 0 : 1;
                        Integer dg = markedReachableStatesFromSource.get(g).get(l);
                        if (dg == null)
                            continue;
                        if (mg < mt || (mg == mt && dg < dt)) {
                            mt = mg;
                            dt = dg;
                        }
                    }
                }
                HDist newlDist = getHDist(mt, dt);
                HDist currentDist = estimates.get(l).get(lts);
                if (newlDist.compareTo(currentDist) < 0) {
                    estimates.get(l).set(lts, newlDist);
                    fresh.add(l);
                    if (compostate.getTransitions().contains(l)) // register only the shortest distances for enabled events
                        registerShort(lts, newlDist);
                }
            }
        }

        while (!fresh.isEmpty()) {
            HAction<Action> t = fresh.poll();
            for (HAction<Action> l : edges.getK(t)) {
                Integer dtl = gap(compostate, l, t);
                for (int lts = 0; lts < ltss.size(); ++lts) {
                    if (readyInLTS.get(l).contains(lts))
                        continue;
                    HDist tDist = estimates.get(t).get(lts);
                    HDist lDist = estimates.get(l).get(lts);
                    Integer dl = addDist(tDist.getSecond(), dtl);
                    HDist newlDist = dl == DirectedControllerSynthesisBlocking.INF ? HDist.chasm : getHDist(tDist.getFirst(), dl);
                    if (newlDist.compareTo(lDist) < 0) {
                        estimates.get(l).set(lts, newlDist);
                        fresh.add(l);
                        if (compostate.getTransitions().contains(l))
                            registerShort(lts, newlDist);
                    }
                }
            }

        }

        reconcilateShort(compostate);
    }


    /** Returns a distance from cache. */
    private HDist getHDist(Integer m, Integer d) {
        Map<Integer, HDist> mCache = m == 0 ? m0Cache : m1Cache;
        HDist result = mCache.get(d);
        if (result == null)
            mCache.put(d, result = new HDist(m, d));
        return result;
    }


    /** Returns the maximum distance between two actions from the current state of every LTS. */
    private Integer gap(Compostate<State, Action> compostate, HAction<Action> l, HAction<Action> t) {
        Integer result = gapCache.get(l).get(t);
        if (result != null)
            return result;
        result = 0;
        int score = 0;
        for (Integer lts : actionsToLTS.get(l)) {
            if (!actionsToLTS.get(t).contains(lts))
                continue;
            HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
            if (s.contains(l)) {
                Map<HAction<Action>, Integer> actionFromSourceToTarget = manyStepsReachableActions.get(s).get(t);
                Integer dl = actionFromSourceToTarget == null ? null : actionFromSourceToTarget.get(l);
                dl = dl == null ? DirectedControllerSynthesisBlocking.INF : dl - 1;
                if (dl > result)
                    result = dl;
            } else {
                score = 1;
            }
        }
        if (result != DirectedControllerSynthesisBlocking.INF)
            result += score;
        gapCache.get(l).put(t, result);
        return result;
    }


    /** Adds to distances (maxing at overflows). */
    private Integer addDist(Integer d1, Integer d2) {
        return (d1 == DirectedControllerSynthesisBlocking.INF || d2 == DirectedControllerSynthesisBlocking.INF) ? DirectedControllerSynthesisBlocking.INF : d1 + d2;
    }


    /** Registers a distance estimated for a given LTS if minimum. */
    private void registerShort(Integer lts, HDist dist) {
        HDist shortDist = shortest.get(lts);
        if (dist.compareTo(shortDist) < 0)
            shortest.put(lts, dist);
    }


    /** Reconciliates the distances for the LTSs for which an action has not been considered. */
    private void reconcilateShort(Compostate<State, Action> compostate) {
        for (int lts = 0; lts < ltss.size(); ++lts) { // this loops sets any missing shortest information
            HDist shortLts = shortest.get(lts);
            if (shortLts == HDist.chasm) {
                HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
                Map<HState<State, Action>, Map<HAction<Action>, Integer>> markedStatesReachableFroms
                        = markedReachableStates.get(s);
                for (Map.Entry<HState<State, Action>, Map<HAction<Action>, Integer>> entry : markedStatesReachableFroms.entrySet()) {
                    HState<State, Action> t = entry.getKey();
                    Integer m = compostate.getTargets(lts).contains(t.state) ? 0 : 1;
                    if (m < shortLts.getFirst()) {
                        for (Integer d : entry.getValue().values()) {
                            if (d < shortLts.getSecond())
                                shortLts = getHDist(m, d);
                        }
                    }
                }
                shortest.put(lts, shortLts);
            }
        }
        for (HAction<Action> l : compostate.getTransitions()) { // this loops fills missing goals with shortest paths
            HEstimate el = estimates.get(l);
            for (int lts = 0; lts < ltss.size(); ++lts) {
                HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
                if (readyInLTS.get(l).contains(lts) && !s.isSelfLoop(l))
                    continue;
                HDist lDist = el.get(lts);
                if (lDist == HDist.chasm)
                    el.set(lts, shortest.get(lts).inc());
            }
        }
    }


    /** Extracts recommendations for a state from the estimates table. */
    @SuppressWarnings("unchecked")
    private void extractRecommendations(Compostate<State, Action> compostate) {
        compostate.setupRecommendations();
        for (HAction<Action> action : compostate.getTransitions()) {
            HEstimate estimate = estimates.get(action);
            estimate.sortDescending();
            if (compostate.addRecommendation(action, estimate))
                break;
        }
        Recommendation<Action> first = compostate.rankRecommendations();
        if (first != null && !first.getEstimate().isConflict()) {
            HEstimate ef = (HEstimate)first.getEstimate().clone();
            HAction<Action> af = first.getAction();
            for (int i = 0; i < compostate.recommendations.size(); ++i) {
                Recommendation<Action> ri = compostate.recommendations.get(i);
                HEstimate ei = ri.getEstimate();
                if (!af.isControllable() && ri.getAction().isControllable()) {
                    first = ri;
                    ef = (HEstimate)first.getEstimate().clone();
                    af = first.getAction();
                }
                if (!ri.getAction().isControllable())
                    ei.values.add(0, getHDist(0,0));
                else if (ei.equals(ef))
                    ei.values.add(0, getHDist(0,1));
                else if (!ei.isConflict())
                    ei.values.add(0, getHDist(0,2));
                else
                    ei.values.add(0, HDist.chasm);
            }
        }
        compostate.initRecommendations();
    }


    /** Populates the actionsToLTS map. */
    private void computeActionsToLTS() {
        for (int i = 0; i < ltss.size(); ++i) {
            for (Action l : ltss.get(i).getActions()) {
                Set<Integer> set = actionsToLTS.computeIfAbsent(alphabet.getHAction(l), k -> new HashSet<>());
                set.add(i);
            }
        }
    }


    /** Computes for each state in each LTS which other states can reach and in how many steps. */
    private void computeReachableStates() {
        Map<HState<State, Action>, Map<HState<State, Action>, Set<HAction<Action>>>> oneStepReachableStates
                = new InitMap<>(HashMap.class);
        Map<HState<State, Action>, Set<Pair<HAction<Action>,HState<State, Action>>>> lastStates = new InitMap<>(HashSet.class);
        Map<HState<State, Action>, Set<Pair<HAction<Action>,HState<State, Action>>>> nextStates = new InitMap<>(HashSet.class);
        boolean statesPopulated = false;

        Map<HState<State, Action>, Map<HAction<Action>, Integer>> manyStepsReachableFromSource;

        for (int lts = 0; lts < ltss.size(); ++lts) { // this loop populates one step reachable states
            for (State state : ltss.get(lts).getStates()) {
                HState<State, Action> source = buildHState(lts, state);
                manyStepsReachableFromSource = manyStepsReachableStates.get(source);
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
                    manyStepsToDestination.put(label, 1);
                    oneStepToDestination.add(label);
                    statesPopulated |= lastStates.get(source).add(Pair.create(label, destination));
                }
            }
        }

        int i = 2;
        while (statesPopulated) { // this loop extends the reachable states in the transitive closure (each iteration adds the states reachable in i steps).
            statesPopulated = false;
            for (HState<State, Action> source : lastStates.keySet()) {
                manyStepsReachableFromSource = manyStepsReachableStates.get(source);
                for (Pair<HAction<Action>, HState<State, Action>> pair : lastStates.get(source)) {
                    HAction<Action> label = pair.getFirst();
                    HState<State, Action> intermediate = pair.getSecond();
                    for (HState<State, Action> target : oneStepReachableStates.get(intermediate).keySet()) {
                        Map<HAction<Action>, Integer> manyStepsReachableFromSourceToTarget
                                = manyStepsReachableFromSource.computeIfAbsent(target, k -> new HashMap<>());
                        Integer current = manyStepsReachableFromSourceToTarget.get(label);
                        if (current == null) {
                            manyStepsReachableFromSourceToTarget.put(label, i);
                            statesPopulated |= nextStates.get(source).add(Pair.create(label, target));
                        }
                    }
                }
            }
            for (Set<Pair<HAction<Action>,HState<State, Action>>> set : lastStates.values())
                set.clear();
            Map<HState<State, Action>, Set<Pair<HAction<Action>,HState<State, Action>>>> swap = lastStates;
            lastStates = nextStates;
            nextStates = swap;
            ++i;
        }

    }


    /** Computes for each state in each LTS which other *marked* states can reach and in how many steps. */
    private void computeMarkedReachableStates() {
        for (HState<State, Action> source : manyStepsReachableStates.keySet()) {
            Map<HState<State, Action>, Map<HAction<Action>, Integer>> markedStatesFromSource
                    = markedReachableStates.get(source);
            Map<HState<State, Action>, Map<HAction<Action>, Integer>> reachableStatesFromSource
                    = manyStepsReachableStates.get(source);
            for (HState<State, Action> destination : reachableStatesFromSource.keySet()) {
                if (destination.marked/*isMarked()*/)
                    markedStatesFromSource.put(destination, reachableStatesFromSource.get(destination));
            }
        }
    }


    /** Computes for each state in each LTS which actions can be reached and in how many steps. */
    private void computeReachableActions() {
        for (int lts = 0; lts < ltss.size(); ++lts) {
            for (State state : ltss.get(lts).getStates()) { // this loop populates the reachable action with the LTSs' transition relations (one step)
                HState<State, Action> source = buildHState(lts, state);
                Map<HAction<Action>, Map<HAction<Action>, Integer>> reachableActionsFromSource = manyStepsReachableActions.get(source);
                for (Pair<Action,State> transition : source.getTransitions()) {
                    HAction<Action> label = alphabet.getHAction(transition.getFirst());
                    Map<HAction<Action>, Integer> reachableActionsThroughLabel = reachableActionsFromSource.computeIfAbsent(label, k -> new HashMap<>());
                    putmin(reachableActionsThroughLabel, label, 1);
                }
            }
            for (State state : ltss.get(lts).getStates()) { // this loop extends the reachable actions with the outgoing events from reachable states (many steps)
                HState<State, Action> source = buildHState(lts, state);
                Map<HState<State, Action>, Map<HAction<Action>, Integer>> statesReachableFromSource = manyStepsReachableStates.get(source);
                Map<HAction<Action>, Map<HAction<Action>, Integer>> actionsReachableFromSource = manyStepsReachableActions.get(source);
                for (HState<State, Action> destination : statesReachableFromSource.keySet()) {
                    for (Map.Entry<HAction<Action>, Integer> entry : statesReachableFromSource.get(destination).entrySet()) {
                        HAction<Action> actionLeadingToDestination = entry.getKey();
                        Integer distanceFromSourceToDestination = entry.getValue();
                        for (Pair<Action,State> transition : destination.getTransitions()) {
                            HAction<Action> target = alphabet.getHAction(transition.getFirst());
                            Map<HAction<Action>, Integer> actionsLeadingToTarget = actionsReachableFromSource.computeIfAbsent(target, k -> new HashMap<>());
                            putmin(actionsLeadingToTarget, actionLeadingToDestination, distanceFromSourceToDestination + 1);
                        }
                    }
                }
            }
        }
    }

    /** Builds (or retrieves from cache) an heuristic state. */
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

    /** Auxiliary function to put an element into a map, but keeping the minimum if already set. */
    private static <K, V extends Comparable<V>> boolean putmin(Map<K, V> map, K key, V value) {
        boolean result;
        V old = map.get(key);
        if (result = (old == null || old.compareTo(value) > 0)) {
            map.put(key, value);
        }
        return result;
    }
}
