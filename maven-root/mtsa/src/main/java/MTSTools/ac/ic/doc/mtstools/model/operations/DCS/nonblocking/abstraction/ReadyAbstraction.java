package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import MTSTools.ac.ic.doc.commons.collections.BidirectionalMap;
import MTSTools.ac.ic.doc.commons.collections.InitMap;
import MTSTools.ac.ic.doc.commons.collections.QueueSet;
import MTSTools.ac.ic.doc.commons.collections.InitMap.Factory;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.*;

/** This class implements the Ready Abstraction (RA). */
public class ReadyAbstraction<State, Action> extends Abstraction<State, Action> {

    /** Set of vertices in the RA graph. */
    private Set<HAction<State, Action>> vertices;

    /** Edges in the RA graph. */
    private BidirectionalMap<HAction<State, Action>, HAction<State, Action>> edges;

    /** Mapping of estimates for actions (the result of evaluating the abstraction). */
    private Map<HAction<State, Action>, HEstimate<State, Action>> estimates;

    /** The minimum estimate per LTS. */
    private Map<Integer, HDist> shortest;

    /** Fresh states discovered at each iteration. */
    private QueueSet<HAction<State, Action>> fresh;

    private Map<HAction<State, Action>, Set<Integer>> readyInLTS;

    /** Mapping of actions to LTSs containing them in their alphabets. */
    private Map<HAction<State, Action>, Set<Integer>> actionsToLTS;

    /** Maps for each state which other states can be reached.
     *  It also stores which actions lead from source to destination and in how many local steps. */
    private Map<HState<State, Action>, Map<HState<State, Action>, Map<HAction<State, Action>, Integer>>> manyStepsReachableStates;

    /** Subset of the manyStepsReachableState map, containing only which
     *  marked or "goal" states can be reached from a given source. This map keeps
     *  aliasing/sharing with the other and it is used only to speed up
     *  iteration while looking for marked states to reach. */
    private Map<HState<State, Action>, Map<HState<State, Action>, Map<HAction<State, Action>, Integer>>> markedOrGoalReachableStates;

    /** Maps for each state which actions can be reached.
     *  It also stores which actions lead from source to desired action and in how many local steps. */
    private Map<HState<State, Action>, Map<HAction<State, Action>, Map<HAction<State, Action>, Integer>>> manyStepsReachableActions;

    /** Cache of target distances. */
    private Map<Integer, HDist> m0Cache;

    /** Cache of marked distances. */
    private Map<Integer, HDist> m1Cache;

    /** Cache of heuristic states ordered by creation order (used for perfect hashing). */
    public List<HState<State, Action>> stash;

    /** Cache of heuristic states used to reduce dynamic allocations. */
    private Map<Integer, HashMap<State, Integer>> cache;

    /** Cache of gaps between actions. */
    private Map<HAction<State, Action>, Map<HAction<State, Action>, Integer>> gapCache;

    private final List<LTS<State, Action>> ltss;
    private final Alphabet<State, Action> alphabet;
    private final List<Set<State>> defaultTargets;
    private List<Set<State>> knownMarked;
    private List<Set<State>> goals;

    /** Whether the debug log is activated or not. */
    private final static Boolean DEBUG_LOG = false;

    /** Whether to consider "goal" states apart from marked states when estimating shortest paths. */
    private final static Boolean CONSIDER_GOALS = true;
    /** Whether to attempt to consider the plant's "structure" when comparing Compostates. */
    private final static Boolean CONSIDER_STRUCTURE = true;
    // Ver Tesis de Nico Pazos para más detalles de esto de arriba, capítulo "Mejoras propuestas"

    /** Used to log internal RA calculations for debugging purposes. */

    private List<HAction<State, Action>> actionnonamae;
    public Action actionnonamae2;
    public Action actionnonamae3;
    private List<String> complist;
    private int listcount=0;
    private static void debugLog(Object log, Object... strFmtArgs) {
        if (DEBUG_LOG) {
            if (String.class.isInstance(log)) {
                System.err.println(String.format((String)log, strFmtArgs));
            } else {
                System.err.println(log);
            }
        }
    }

    static public class CompostateRanker<State, Action> implements Comparator<Compostate<State, Action>> {

        @Override
        public int compare(Compostate<State, Action> c1, Compostate<State, Action> c2) {
            Recommendation<State, Action> r1 = c1.peekRecommendation();
            Recommendation<State, Action> r2 = c2.peekRecommendation();

            // FIXME: parece que pueden quedar compostates con recom en null. Hay que
            // investigar.
            if (r1 == null)
                return 1;
            if (r2 == null)
                return -1;

            // Uncontrollables have higher priority
            int controllable1 = r1.getAction().isControllable() ? 1 : 0;
            int controllable2 = r2.getAction().isControllable() ? 1 : 0;
            int result = controllable1 - controllable2;


            if (result == 0 && controllable1 == 0) { // If both uncontrollable
                result = r2.compareTo(r1);
            } else if (result == 0 && controllable1 == 1) { //  If both controllable
                if (CONSIDER_STRUCTURE) {
                    if (c1.uncontrollablesCount == 0 && c2.uncontrollablesCount > 0) {
                        result = -1;
                    } else if (c2.uncontrollablesCount == 0 && c1.uncontrollablesCount > 0) {
                        result = 1;
                    } else if (c1.getControllablesExpandedCount() == 0 && c2.getControllablesExpandedCount() > 0) {
                            result = -1;
                    } else if (c2.getControllablesExpandedCount() == 0 && c1.getControllablesExpandedCount() > 0) {
                            result = 1;
                    } else {
                        result = r1.compareTo(r2);
                    }
                } else {
                    result = r1.compareTo(r2);
                }
            }

            // Depth is the tiebreaker
            if (result == 0)
                result = c1.getDepth() - c2.getDepth();

            return result;
        }
    }

    /** Constructor for the RA.
     *
     * @param ltss
     * @param defaultTargets
     * @param alphabet
     */
    public ReadyAbstraction(List<LTS<State, Action>> ltss, List<Set<State>> defaultTargets, Alphabet<State, Action> alphabet) {
        this.ltss = ltss;
        this.alphabet = alphabet;
        this.defaultTargets = defaultTargets;
        this.goals = new ArrayList<>(this.ltss.size());
        for (int i = 0; i < this.ltss.size(); ++i) {
            this.goals.add(new HashSet<>());
        }
        stash = new ArrayList<>(this.ltss.size());
        cache = new HashMap<>();

        vertices = new HashSet<>();
        readyInLTS = new InitMap<>(HashSet.class);
        edges = new BidirectionalMap<>();
        estimates = new InitMap<>(new Factory<HEstimate<State, Action>>() {
            @Override
            public HEstimate<State, Action> newInstance() {
                HEstimate<State, Action> result = new HEstimate<State, Action>(ltss.size()+1, HDist.chasm);
                result.values.remove(ltss.size());
                return result;
            }
        });
        shortest = new InitMap<>(HDist.chasmFactory);
        fresh = new QueueSet<>();
        actionsToLTS = new InitMap<>(HashSet.class);
        manyStepsReachableStates = new InitMap<>(HashMap.class);
        markedOrGoalReachableStates = new InitMap<>(HashMap.class);
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
        computeMarkedOrGoalReachableStates();
        computeReachableActions();
    }


    /** Clears the RA internal state. */
    private void clear() {
        vertices = new HashSet<>();
        readyInLTS = new InitMap<>(HashSet.class);
        edges = new BidirectionalMap<>();
        estimates.clear();
        shortest.clear();
        fresh.clear();
        gapCache.clear();
    }


    /** Evaluates the abstraction by building and exploring the RA. */
    @Override
    public void eval(Compostate<State, Action> compostate, List<Set<State>> knownMarked, List<Set<State>> goals, List<String> comparison) {
        if (!compostate.isEvaluated()) {
            this.knownMarked = knownMarked;
            if (CONSIDER_GOALS) {
                this.goals = goals;
                computeMarkedOrGoalReachableStates();
            }
            clear();
            buildRA(compostate);
            evaluateRA(compostate);
            if(listcount==0){

                for (String action : comparison) {
                    complist.add(action);
                }
                listcount=listcount+1;
            }
            extractRecommendations(compostate);
        }
    }

    public void evalForHeuristic(Compostate<State, Action> compostate, FeatureBasedExplorationHeuristic<State, Action> heuristic) {
        clear();
        buildRA(compostate);
        evaluateRA(compostate);
        extractRecommendationsForHeuristic(compostate, heuristic);
    }


    /** Builds the RA by connecting ready events through edges indicating their causal relationship. */
    private void buildRA(Compostate<State, Action> compostate) {
        if (compostate.vertices != null) {
            // TODO: consider freeing this once the state is goal/error
            this.vertices = compostate.vertices;
            this.readyInLTS = compostate.readyInLTS;
            this.edges = compostate.edges;
            return;
        }
        for (int lts = 0; lts < this.ltss.size(); ++lts) {
            HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
            for (Pair<Action,State> transition : s.getTransitions()) {
                HAction<State, Action> action = this.alphabet.getHAction(transition.getFirst());
                if (!s.state.equals(transition.getSecond())) { // !s.isSelfLoop(action)
                    readyInLTS.get(action).add(lts);
                    vertices.add(action);
                }
            }
        }
        for (HAction<State, Action> t : vertices) {
            for (Integer lts : actionsToLTS.get(t)) {
                HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
                Map<HAction<State, Action>, Integer> actionsLeadingToTfromS = manyStepsReachableActions.get(s).get(t);
                if (actionsLeadingToTfromS != null) {
                    for (HAction<State, Action> l : actionsLeadingToTfromS.keySet()) {
                        if (!l.equals(t) && s.contains(l) && !s.isSelfLoop(l)) { // we need an efficient s.contains(l) that returns false for self-loops
                            edges.put(l, t);
                        }
                    }
                }
            }
        }
        compostate.vertices = this.vertices;
        compostate.readyInLTS = this.readyInLTS;
        compostate.edges = this.edges;
        // debugLog("Built RA: %s", edges);
    }


    /** Evaluates the RA by exploring the graph and populating the estimates table. */
    private void evaluateRA(Compostate<State, Action> compostate) {
        for (int lts = 0; lts < this.ltss.size(); ++lts) {
            if (complist == null)
                complist = new ArrayList<>();
            HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
            Set<State> markedStates = this.defaultTargets.get(lts);
            Set<State> knownMarked  = this.knownMarked.get(lts);
            Set<State> goals  = this.goals.get(lts);
            Map<HState<State, Action>, Map<HAction<State, Action>, Integer>> markedOrGoalReachableStatesFromSource = markedOrGoalReachableStates.get(s);
            for (Pair<Action,State> transitions : s.getTransitions()) {
                HAction<State, Action> l = this.alphabet.getHAction(transitions.getFirst());
                State t = transitions.getSecond();
                if (t.equals(-1L)) // CASE 1: action leads to illegal state
                    continue;
                Integer mt = 2, dt = DirectedControllerSynthesisNonBlocking.INF;
                if (markedStates.contains(t) || goals.contains(t)) {
                    if (goals.contains(t)) {
                        mt = -1;
                    } else if (knownMarked.contains(t)) {
                        mt = 0;
                    } else {
                        mt = 1;
                    }
                    dt = 1;
                }
                if (!(mt == -1 || (mt == 1 && goals.isEmpty() && knownMarked.isEmpty()))) { // already best, skip search
                    if (s.state.equals(t)) // a self-loop
                        continue;
                    for (HState<State, Action> g : markedOrGoalReachableStatesFromSource.keySet()) { // search for best
                        Integer mg;
                        if (goals.contains(g.state)) {
                            mg = -1;
                        } else if (knownMarked.contains(g.state)) {
                            mg = 0;
                        } else {
                            mg = 1;
                        }
                        Integer dg = markedOrGoalReachableStatesFromSource.get(g).get(l);
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
                    // CASES 2/3: there's a path in the RA, update with the shortest one
                    estimates.get(l).set(lts, newlDist);
                    // debugLog("Upated estimate for l=%s lts=%d d=%s", l, lts, dt);
                    fresh.add(l);
                    if (compostate.getTransitions().contains(l)) { // register only the shortest distances for enabled events
                        // debugLog("New shortest for lts=%d at comp=%s is d=%d using l=%s", lts, compostate, dt, l);
                        registerShort(lts, newlDist);
                    }
                }
            }
        }

        while (!fresh.isEmpty()) {
            HAction<State, Action> t = fresh.poll();
            for (HAction<State, Action> l : edges.getK(t)) {
                Integer dtl = gap(compostate, l, t);
                // debugLog("At comp=%s gap(%s, %s)=%d", compostate, l, t, dtl);
                for (int lts = 0; lts < this.ltss.size(); ++lts) {
                    if (readyInLTS.get(l).contains(lts))
                        continue;
                    HDist tDist = estimates.get(t).get(lts);
                    HDist lDist = estimates.get(l).get(lts);
                    Integer dl = addDist(tDist.getSecond(), dtl);
                    HDist newlDist = dl == DirectedControllerSynthesisNonBlocking.INF ? HDist.chasm : getHDist(tDist.getFirst(), dl);
                    if (newlDist.compareTo(lDist) < 0) {
                        // CASES 2/3 (again): there's a path in the RA, update with the shortest one
                        estimates.get(l).set(lts, newlDist);
                        // debugLog("CLOSURE: Updated estimate for lts=%d at comp=%s,using l1=%s--l2=%s %d = (%d)+%d", lts, compostate, l, t, dl, dtl, tDist.getSecond());
                        fresh.add(l);
                        if (compostate.getTransitions().contains(l)) {
                            // debugLog("CLOSURE: New shortest for lts=%d at comp=%s,using l1=%s--l2=%s %d = (%d)+%d", lts, compostate, l, t, dl, dtl, tDist.getSecond());
                            registerShort(lts, newlDist);
                        }
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
    private Integer gap(Compostate<State, Action> compostate, HAction<State, Action> l, HAction<State, Action> t) {
        Integer result = gapCache.get(l).get(t);
        if (result != null)
            return result;
        result = 0;
        for (Integer lts : actionsToLTS.get(l)) {
            if (!actionsToLTS.get(t).contains(lts))
                continue;
            HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
            if (s.contains(l)) {
                Map<HAction<State, Action>, Integer> actionFromSourceToTarget = manyStepsReachableActions.get(s).get(t);
                Integer dl = actionFromSourceToTarget == null ? null : actionFromSourceToTarget.get(l);
                dl = dl == null ? DirectedControllerSynthesisNonBlocking.INF : dl - 1;
                if (dl > result)
                    result = dl;
            }
        }
        gapCache.get(l).put(t, result);
        return result;
    }


    /** Adds to distances (maxing at overflows). */
    private Integer addDist(Integer d1, Integer d2) {
        return (d1 == DirectedControllerSynthesisNonBlocking.INF || d2 == DirectedControllerSynthesisNonBlocking.INF) ? DirectedControllerSynthesisNonBlocking.INF : d1 + d2;
    }


    /** Registers a distance estimated for a given LTS if minimum. */
    private void registerShort(Integer lts, HDist dist) {
        HDist shortDist = shortest.get(lts);
        if (dist.compareTo(shortDist) < 0)
            shortest.put(lts, dist);
    }


    /** Reconciliates the distances for the LTSs for which an action has not been considered. */
    private void reconcilateShort(Compostate<State, Action> compostate) {
        for (int lts = 0; lts < this.ltss.size(); ++lts) { // this loops sets any missing shortest information
            HDist shortLts = shortest.get(lts);
            if (shortLts == HDist.chasm) {
                HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
                Map<HState<State, Action>, Map<HAction<State, Action>, Integer>> markedStatesReachableFroms = markedOrGoalReachableStates.get(s);
                for (Entry<HState<State, Action>, Map<HAction<State, Action>, Integer>> entry : markedStatesReachableFroms.entrySet()) {
                    HState<State, Action> t = entry.getKey();
                    Integer m;
                    if (this.goals.get(lts).contains(t.state)) {
                        m = -1;
                    } else if (this.knownMarked.get(lts).contains(t.state)) {
                        m = 0;
                    } else {
                        m = 1;
                    }
                    if (m < shortLts.getFirst()) {
                        for (HAction<State, Action> a : entry.getValue().keySet()) {
                            Integer d = entry.getValue().get(a);
                            if (d < shortLts.getSecond()) {
                                shortLts = getHDist(m, d);
                                // debugLog("New shortest for lts=%d at comp=%s is d=%d using l=%s", lts, compostate, d, a.getAction());
                            }
                        }
                    }
                }
                shortest.put(lts, shortLts);
            }
        }
        for (HAction<State, Action> l : compostate.getTransitions()) { // this loops fills missing goals with shortest paths
            HEstimate<State, Action> el = estimates.get(l);
            for (int lts = 0; lts < this.ltss.size(); ++lts) {
                HState<State, Action> s = buildHState(lts, compostate.getStates().get(lts));
                if (readyInLTS.get(l).contains(lts) && !s.isSelfLoop(l))
                    continue;
                HDist lDist = el.get(lts);
                if (lDist == HDist.chasm) {
                    if (s.marked && (!actionsToLTS.get(l).contains(lts) || s.isSelfLoop(l))) {
                        // CASES 4/5
                        // Current state is marked and 'l' is either not in the component's alphabet or a self-loop,
                        // so taking 'l' means staying at a marked state.
                        Integer m;
                        if (goals.contains(s.state)) {
                            m = -1;
                        } else if (knownMarked.contains(s.state)) {
                            m = 0;
                        } else {
                            m = 1;
                        }
                        el.set(lts, getHDist(m, 1));
                        // debugLog("Current is marked for lts=%s and l=%s is either not in alphabet or a self-loop: (%d, 1)", lts, l, m);
                    } else {
                        // CASES 6/7 if shortest.get(lts) is not 'chasm'. If it is 'chasm', we are covering CASE 8
                        // debugLog("Updating dist for lts=%s l=%s using 1 + shortest=%s", lts, l, shortest.get(lts));
                        el.set(lts, shortest.get(lts).inc());
                    }
                }
            }
        }
    }


    /** Extracts recommendations for a state from the estimates table. */
    @SuppressWarnings("unchecked")
    private void extractRecommendations(Compostate<State, Action> compostate) {
        compostate.setupRecommendations(complist);
        // TODO: maybe move this 'explored' to a method in compostate
        // TODO: optimizable, tener las exploradas precomputadas en el compostate
        HashSet<HAction<State, Action>> explored = new HashSet<>();
        for (Pair<HAction<State, Action>,Compostate<State, Action>> transition : compostate.getExploredChildren()) {
            HAction<State, Action> action = transition.getFirst();
            explored.add(action);
        }
        for (HAction<State, Action> action : compostate.getTransitions()) {
            if (explored.contains(action)) {
                continue;
            }
            HEstimate<State, Action> estimate = estimates.get(action);
            estimate.sortDescending();
            if (compostate.addRecommendation(action, estimate))
                break;
        }
        compostate.rankRecommendations();
        compostate.initRecommendations();
    }


    /** Extracts estimates for a state from the estimates table (to use as RL feature). */
    private void extractRecommendationsForHeuristic(Compostate<State, Action> compostate, FeatureBasedExplorationHeuristic<State, Action> heuristic) {
        for (HAction<State, Action> action : compostate.getTransitions()) {
            HEstimate<State, Action> estimate = estimates.get(action);
            estimate.sortDescending();
            heuristic.addRecommendation(compostate, action, estimate);
        }
    }

    /** Populates the actionsToLTS map. */
    private void computeActionsToLTS() {
        for (int i = 0; i < this.ltss.size(); ++i) {
            for (Action l : this.ltss.get(i).getActions()) {
                Set<Integer> set = actionsToLTS.computeIfAbsent(this.alphabet.getHAction(l), k -> new HashSet<>());
                set.add(i);
            }
        }
    }


    /** Computes for each state in each LTS which other states can reach and in how many steps. */
    private void computeReachableStates() {
        Map<HState<State, Action>, Map<HState<State, Action>, Set<HAction<State, Action>>>> oneStepReachableStates = new InitMap<>(HashMap.class);
        Map<HState<State, Action>, Set<Pair<HAction<State, Action>,HState<State, Action>>>> lastStates = new InitMap<>(HashSet.class);
        Map<HState<State, Action>, Set<Pair<HAction<State, Action>,HState<State, Action>>>> nextStates = new InitMap<>(HashSet.class);
        boolean statesPopulated = false;

        Map<HState<State, Action>, Map<HAction<State, Action>, Integer>> manyStepsReachableFromSource;

        for (int lts = 0; lts < this.ltss.size(); ++lts) { // this loop populates one step reachable states
            for (State state : this.ltss.get(lts).getStates()) {
                HState<State, Action> source = buildHState(lts, state);
                manyStepsReachableFromSource = manyStepsReachableStates.get(source);
                Map<HState<State, Action>, Set<HAction<State, Action>>> oneReachableFromSoruce = oneStepReachableStates.get(source);
                for (Pair<Action, State> transition : this.ltss.get(lts).getTransitions(state)) {
                    HAction<State, Action> label = this.alphabet.getHAction(transition.getFirst());
                    HState<State, Action> destination = buildHState(lts, transition.getSecond());
                    Set<HAction<State, Action>> oneStepToDestination = oneReachableFromSoruce.get(destination);
                    Map<HAction<State, Action>, Integer> manyStepsToDestination = manyStepsReachableFromSource.get(destination);
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
                for (Pair<HAction<State, Action>, HState<State, Action>> pair : lastStates.get(source)) {
                    HAction<State, Action> label = pair.getFirst();
                    HState<State, Action> intermediate = pair.getSecond();
                    for (HState<State, Action> target : oneStepReachableStates.get(intermediate).keySet()) {
                        Map<HAction<State, Action>, Integer> manyStepsReachableFromSourceToTarget = manyStepsReachableFromSource.get(target);
                        if (manyStepsReachableFromSourceToTarget == null)
                            manyStepsReachableFromSource.put(target, manyStepsReachableFromSourceToTarget = new HashMap<>());
                        Integer current = manyStepsReachableFromSourceToTarget.get(label);
                        if (current == null) {
                            manyStepsReachableFromSourceToTarget.put(label, i);
                            statesPopulated |= nextStates.get(source).add(Pair.create(label, target));
                        }
                    }
                }
            }
            for (Set<Pair<HAction<State, Action>,HState<State, Action>>> set : lastStates.values())
                set.clear();
            Map<HState<State, Action>, Set<Pair<HAction<State, Action>,HState<State, Action>>>> swap = lastStates;
            lastStates = nextStates;
            nextStates = swap;
            ++i;
        }

    }


    /** Computes for each state in each LTS which other *marked* or *goal* states can reach and in how many steps. */
    private void computeMarkedOrGoalReachableStates() {
        markedOrGoalReachableStates = new InitMap<>(HashMap.class);
        for (HState<State, Action> source : manyStepsReachableStates.keySet()) {
            Map<HState<State, Action>, Map<HAction<State, Action>, Integer>> markedOrGoalStatesFromSource = markedOrGoalReachableStates.get(source);
            Map<HState<State, Action>, Map<HAction<State, Action>, Integer>> reachableStatesFromSource = manyStepsReachableStates.get(source);
            for (HState<State, Action> destination : reachableStatesFromSource.keySet()) {
                if (
                    destination.marked ||
                    (!destination.state.equals(-1L) && this.goals.get(destination.lts).contains(destination.state))
                )
                    markedOrGoalStatesFromSource.put(destination, reachableStatesFromSource.get(destination));
            }
        }
    }


    /** Computes for each state in each LTS which actions can be reached and in how many steps. */
    private void computeReachableActions() {
        for (int lts = 0; lts < this.ltss.size(); ++lts) {
            for (State state : this.ltss.get(lts).getStates()) { // this loop populates the reachable action with the LTSs' transition relations (one step)
                HState<State, Action> source = buildHState(lts, state);
                Map<HAction<State, Action>, Map<HAction<State, Action>, Integer>> reachableActionsFromSource = manyStepsReachableActions.get(source);
                for (Pair<Action,State> transition : source.getTransitions()) {
                    HAction<State, Action> label = this.alphabet.getHAction(transition.getFirst());
                    Map<HAction<State, Action>, Integer> reachableActionsThroughLabel = reachableActionsFromSource.get(label);
                    if (reachableActionsThroughLabel == null)
                        reachableActionsFromSource.put(label, reachableActionsThroughLabel = new HashMap<>());
                    DirectedControllerSynthesisNonBlocking.putmin(reachableActionsThroughLabel, label, 1);
                }
            }
            for (State state : this.ltss.get(lts).getStates()) { // this loop extends the reachable actions with the outgoing events from reachable states (many steps)
                HState<State, Action> source = buildHState(lts, state);
                Map<HState<State, Action>, Map<HAction<State, Action>, Integer>> statesReachableFromSource = manyStepsReachableStates.get(source);
                Map<HAction<State, Action>, Map<HAction<State, Action>, Integer>> actionsReachableFromSource = manyStepsReachableActions.get(source);
                for (HState<State, Action> destination : statesReachableFromSource.keySet()) {
                    for (Entry<HAction<State, Action>, Integer> entry : statesReachableFromSource.get(destination).entrySet()) {
                        HAction<State, Action> actionLeadingToDestination = entry.getKey();
                        Integer distanceFromSourceToDestination = entry.getValue();
                        for (Pair<Action,State> transition : destination.getTransitions()) {
                            HAction<State, Action> target = this.alphabet.getHAction(transition.getFirst());
                            Map<HAction<State, Action>, Integer> actionsLeadingToTarget = actionsReachableFromSource.get(target);
                            if (actionsLeadingToTarget == null)
                                actionsReachableFromSource.put(target, actionsLeadingToTarget = new HashMap<>());
                            DirectedControllerSynthesisNonBlocking.putmin(actionsLeadingToTarget, actionLeadingToDestination, distanceFromSourceToDestination + 1);
                        }
                    }
                }
            }
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
                this.defaultTargets.get(lts).contains(state),
                this.ltss);

            stash.add(hstate);
            index = hstate.hashCode();
            table.put(state, index);
        }
        return stash.get(index);
    }
}