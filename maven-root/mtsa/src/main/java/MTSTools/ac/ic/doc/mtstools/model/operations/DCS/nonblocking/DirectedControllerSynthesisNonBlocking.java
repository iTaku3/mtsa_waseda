package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking;

import MTSTools.ac.ic.doc.commons.collections.BidirectionalMap;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.MarkedLTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.DirectedControllerSynthesis;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.Statistics;

import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.HAction;

import java.util.*;

import static java.util.Collections.*;
import static org.junit.Assert.*;


/** This class contains the logic to synthesize a controller for
 *  a deterministic environment using an informed search procedure. */
public class DirectedControllerSynthesisNonBlocking<State, Action> extends DirectedControllerSynthesis<State, Action> {

    /** Constant used to represent an infinite distance to the goal. */
    public static final int INF = Integer.MAX_VALUE;

    /** Constant used to represent an undefined distance to the goal. */
    static final int UNDEF = -1;

    /** Indicates the heuristic to use. */
    public static HeuristicMode mode = HeuristicMode.Ready;

    /** List of LTS that compose the environment. */
    public List<LTS<State,Action>> ltss;

    /** The number of intervening LTSs. */
    public int ltssSize;

    /** Set of controllable actions. */
    public Set<Action> controllable;

    public List<String> comparison;

    /** Indicates whether the procedure tries to reach a goal state (by default we look for live controllers). */
    private boolean reachability = false;

    /** Indicates whether the procedure consider a non-blocking requirement (by default we consider a stronger goal). */
    boolean nonblocking = false;

    /** Environment alphabet (implements perfect hashing for actions). */
    public Alphabet<State, Action> alphabet;

    /** Set of transitions enabled by default by each LTS. */
    public TransitionSet<State, Action> base;

    /** Auxiliary transitions allowed by a given compostate. */
    TransitionSet<State, Action> allowed;

    /** Last states used to update the allowed transitions. */
    List<State> facilitators;

    public ExplorationHeuristic<State, Action> heuristic;

    /** Cache of states mapped from their basic components. */
    Map<List<State>,Compostate<State, Action>> compostates;

    /** List of reachable states after a transition (used during expansion). */
    private Deque<Set<State>> transitions;

    /** Set of visited during iteration. */
    private Set<Compostate<State, Action>> visited;

    private Set<Compostate<State, Action>> loop;
    private Set<Compostate<State, Action>> probablyWinningStates;

    /** Directed acyclic graph from a successor state to a precursor state. */
    private BidirectionalMap<Compostate<State, Action>, Compostate<State, Action>> dag;

    /** List of ancestor states considered during the construction of the dag. */
    private List<Compostate<State, Action>> auxiliarListStates;

    /** List of descendants of an state (used to close unnecessary descendants). */
    private Deque<Compostate<State, Action>> descendants;

    /** Contains the marked states per LTS. */
    public List<Set<State>> defaultTargets;

    /** Initial state. */
    private Compostate<State, Action> initial;

    /** Statistic information about the procedure. */
    final public Statistics statistics = new Statistics();


    /** This method starts the directed synthesis of a controller.
     *  @param ltss, a list of MarkedLTSs that compose the environment.
     *  @param controllable, the set of controllable actions.
     *  @param comparison,
     *  @param reachability, a boolean indicating whether to pursue reachability or liveness.
     *  @param guarantees not used in nonblocking
     *  @param assumptions not used in nonblocking
     *  @return the controller for the environment in the form of an LTS that
     *      when composed with the environment reaches the goal, only by
     *      enabling or disabling controllable actions and monitoring
     *      uncontrollable actions. Or null if no such controller exists. */
    @SuppressWarnings("unchecked")
    public LTS<Long,Action> synthesize(
        List<LTS<State, Action>> ltss,
        Set<Action> controllable,
        boolean reachability,
        HashMap<Integer, Integer> guarantees,
        HashMap<Integer, Integer> assumptions,
        List<String> comparison)
    {
        if (mode == HeuristicMode.Dummy)
            return (LTS<Long,Action>)ltss.get(0);

        setupSynthesis(ltss, controllable, reachability, guarantees, assumptions, comparison);
        //System.out.println(comparison);
        setupHeuristic();
        setupInitialState();

        while (heuristic.somethingLeftToExplore() && !isFinished()) {
            statistics.startHeuristicTime();
            Pair<Compostate<State, Action>, HAction<State, Action>> action = heuristic.getNextAction(comparison);
            statistics.endHeuristicTime();

            expand(action.getFirst(), action.getSecond());
        }

        assertTrue("Finished because there was nothing left to explore, shouldn't be the case", isFinished());

        statistics.end();

        LTS<Long,Action> result;

        if(isGoal(initial)){
            result = buildDirector();
        } else {
            result = null;
        }

        return result;
    }

    public void setupSynthesis(
            List<LTS<State, Action>> ltss,
            Set<Action> controllable,
            boolean reachability,
            HashMap<Integer, Integer> guarantees,
            HashMap<Integer, Integer> assumptions,
            List<String> comparison)
    {

        this.ltss = ltss;
        this.ltssSize = ltss.size();
        this.controllable = controllable;
        this.reachability = reachability;
        this.comparison = comparison;

        statistics.clear();
        statistics.start();

        compostates = new HashMap<>();
        transitions = new ArrayDeque<>(ltss.size());
        visited = new HashSet<>();
        loop = new HashSet<>();
        probablyWinningStates = new HashSet<>();
        dag = new BidirectionalMap<>();
        auxiliarListStates = new ArrayList<>();
        descendants = new ArrayDeque<>();

        alphabet = new Alphabet<>(this.ltss, this.controllable);
        base = new TransitionSet<>(this.ltss, alphabet);
        allowed = base.clone();
        defaultTargets = buildDefaultTargets();
    }

    void setupHeuristic(){
        if(mode == HeuristicMode.TrainedAgent){
            heuristic = new FeatureBasedExplorationHeuristic<>(this);
        } else {
            // heuristic = new OpenSetExplorationHeuristic<>(this, mode);
            // 'CompleteFrontierExplorationHeuristic' no usa la Open Queue, le deja a la heuristica manejar la cola
            heuristic = new CompleteFrontierExplorationHeuristic<>(this, mode);
        }
    }

    void setupInitialState(){
        initial = buildInitialState();
        heuristic.setInitialState(initial, comparison);
        initial.setExpanded();
    }

    public boolean isFinished(){
        return !isNone(initial);
    }

    /** Returns a list of marked states per LTS. */
    private List<Set<State>> buildDefaultTargets() {
        List<Set<State>> result = new ArrayList<>();
        for (int i = 0; i < ltssSize; ++i) {
            LTS<State,Action> lts = ltss.get(i);
            Set<State> markedStates = new HashSet<>();
            if (lts instanceof MarkedLTSImpl) {
                markedStates.addAll(((MarkedLTSImpl<State,Action>)lts).getMarkedStates());
            } else {
                markedStates.addAll(lts.getStates());
                markedStates.remove(-1L); // -1 is never marked since it is used to represent errors
            }
            result.add(markedStates);
        }
        return result;
    }


    /** Returns the statistic information about the procedure. */
    public Statistics getStatistics() {
        return statistics;
    }


    /** Creates (or retrieves from cache) a state in the composition given
     *  a list with its base components.
     *  @param states, a list of states with one state per LTS in the
     *         environment, the position of each state in the list (its index)
     *         reflects to which LTS that state belongs. */
    private Compostate<State, Action> buildCompostate(List<State> states, Compostate<State, Action> parent) {
        Compostate<State, Action> result = compostates.get(states);
        if (result == null) {
            statistics.incExpandedStates();
            result = new Compostate<>(this, states);
            compostates.put(states, result);
            heuristic.newState(result, parent, comparison);
            if (result.getStates().contains(-1L) || heuristic.fullyExplored(result)) {
                setError(result);
            }
        }
        return result;
    }


    /** Creates the controller's initial state. */
    private Compostate<State, Action> buildInitialState() {
        List<State> states = new ArrayList<>(ltss.size());
        for (LTS<State,Action> lts : ltss)
            states.add(lts.getInitialState());
        Compostate<State, Action> result = buildCompostate(states, null); // for non-deterministic LTS I need the tau-closure as a set of initial states
        result.setDepth(0);
        return result;
    }


    /** Expands a state following a given recommendation from a parent compostate.
     *  Internally this populates the transitions and expanded lists.
     * @return*/
    void expand(Compostate<State, Action> state, HAction<State, Action> action) {
        statistics.incExpandedTransitions();

        Compostate<State, Action> child = buildCompostate(getChildStates(state, action), state);
        // System.err.println("Expanding "+state+" -> "+action+" -> "+child);

        state.addChild(action, child);
        child.addParent(action, state);

        heuristic.notifyExpandingState(state, action, child);

        open(state, action, child);

        child.setExpanded();

        heuristic.expansionDone(state, action, child);
    }

    List<State> getChildStates(Compostate<State, Action> state, HAction<State, Action> action) {
        List<State> parentStates = state.getStates();
        int size = parentStates.size();
        for (int i = 0; i < size; ++i) {
            Set<State> image = ltss.get(i).getTransitions(parentStates.get(i)).getImage(action.getAction());
            if (image == null || image.isEmpty()) {
                if (ltss.get(i).getActions().contains(action.getAction())) { // tau does not belong to the lts alphabet, yet it may have a valid image, but I do not want taus at this stage
                    transitions.clear();
                    // System.err.println("Invalid action: " + action);
                    return null; // do not expand a state through an invalid action
                }
                image = singleton(parentStates.get(i));
            }
            transitions.add(image);
        }
        List<State> childStates = new ArrayList<>();
        for (Set<State> singleton : transitions) // Note: Cartesian product for non-deterministic systems
            childStates.add(singleton.iterator().next());
        transitions.clear();
        return childStates;
    }

    private static <T> Set<T> newHashSet(T element) {
        Set<T> set = new HashSet<>();
        set.add(element);
        return set;
    }

    /** Opens a given child state following an action selected by the heuristic.
     *  This method distinguishes between reachability and liveness.
     *  If the child to "open" is a goal (or closes a loop over a marked state)
     *  it propagates the goal through the ancestors. If the child is an error
     *  (or closes a loop over non-marked states) it propagates an error. */
    private void open(Compostate<State, Action> parent, HAction<State, Action> action, Compostate<State, Action> child) {
        assert isNone(parent);  //the parent should be NONE, otherwise exploring is useless
        //System.err.printLn("opening from " + parent.toString() + " using " + action.toString() + " to " + child.toString());

        if (isError(child) || child.heuristicStronglySuggestsIsError) {
            if(!isError(child)){
                setError(child);
            }
            propagateError(newHashSet(child), newHashSet(parent));

        } else if (isGoal(child)) {
            parent.setHasGoalChild(action);
            propagateGoal(newHashSet(child), newHashSet(parent));

        } else if (closingALoop(parent, child)) {

            gatherLoopStates(child);

            //only if we discovered a new Compostate for the loop or if the action was uncontrollable we can find new goals.
            //if (probablyWinningStates.size()>0 && (!recommendation.action.isControllable() || !newLoopIsTheOldOne )){
            if (probablyWinningStates.size() > 0) {
                findNewGoals();
            } else {
                findNewErrors();
            }
        } else {
            heuristic.notifyExpansionDidntFindAnything(parent, action, child);
        }
        clearLoopDetection();
    }

    private void findNewGoals() {
        statistics.incFindNewGoalsCalls();

        Set<Compostate<State, Action>> c = new HashSet<>(loop);
        //System.err.printLn("we are gatheringGoals with c: " + c);
        int previous_size = 0;

        while(previous_size != c.size() && probablyWinningStates.size() > 0){
            previous_size = c.size();
            boolean reached_fixpoint = false;

            while(!reached_fixpoint){
                Iterator<Compostate<State, Action>> it = c.iterator();
                boolean removed = false;
                while (it.hasNext()) {

                    Compostate<State, Action> state = it.next();
                    //sacar los que son forzados a irse
                    if(forcedByEnvironmentToLeave(state, c)){
                        probablyWinningStates.remove(state);
                        it.remove();
                        removed=true;
                    } else {
                        //sacar a los que no llega nadie
                        boolean hasParentInC = false;
                        for (Pair<HAction<State, Action>,Compostate<State, Action>> ancestorActionAndState : state.getParents()) {
                            Compostate<State, Action> ancestor = ancestorActionAndState.getSecond();
                            if (c.contains(ancestor)) {
                                hasParentInC = true;
                                break;
                            }
                        }
                        if(!hasParentInC){
                            probablyWinningStates.remove(state);
                            it.remove();
                            removed=true;
                        }
                    }
                }
                if(!removed){
                    reached_fixpoint = true;
                }
            }
            //sacar los que no llegan a un estado marcado
            Set<Compostate<State, Action>> ancestors = gatherTargetAncestors(c, probablyWinningStates);
            probablyWinningStates.retainAll(ancestors);
            c.retainAll(ancestors);
        }

        //System.err.printLn("probablyWinningStates has " + probablyWinningStates.toString());

        //we check that the final c is valid
        if(c.isEmpty() || probablyWinningStates.size()<=0){
            return;
        }

        auxiliarListStates.clear();
        auxiliarListStates.addAll(probablyWinningStates);
        visited.addAll(probablyWinningStates);

        Set<Compostate<State, Action>> mccc = new HashSet<>(probablyWinningStates);

        for (int i = 0; i < auxiliarListStates.size(); ++i) {
            Compostate<State, Action> state = auxiliarListStates.get(i);
            for (Pair<HAction<State, Action>,Compostate<State, Action>> predecesor : state.getParents()) {
                Compostate<State, Action> predState = predecesor.getSecond();

                if(c.contains(predState) && visited.add(predState)){
                    mccc.add(predState);
                    auxiliarListStates.add(predState);
                }
            }
        }
        auxiliarListStates.clear();

        Set<Compostate<State, Action>> alreadyChecked = new HashSet<>();
        //marco todos los estados de c como goal
        for (Compostate<State, Action> state : mccc) { //fixme en realidad "performe" a ver si se puede hacer mÃ¡s performante
            for (Pair<HAction<State, Action>,Compostate<State, Action>> transition : state.getExploredChildren()) {
                HAction<State, Action> action = transition.getFirst();
                Compostate<State, Action> child = transition.getSecond();
                if(mccc.contains(child)){
                    setGoal(state, action);
                }
            }
            if(state.marked){
                //we set bestChild for marked state as any child that stays in the winning loop
                for (Compostate<State, Action> child : state.getChildrenExploredThroughControllable()){
                    if(mccc.contains(child)){
                        state.setBestControllable(0,child);
                        break;
                    }
                }
                if (state.getBestControllable().getFirst()==-1){
                    state.setBestControllable(0,null);
                }
                alreadyChecked.add(state);
            }
        }

        //we start from marked states and check parents until all states in mccc have distance and bestChild for the director
        //marked states in mccc already have distance set
        if(!mccc.isEmpty()){
            updateDistances(alreadyChecked,mccc,mccc.size()- alreadyChecked.size());
        }

        propagateGoal(mccc, null);
    }

    private void updateDistances(Set<Compostate<State, Action>> alreadyChecked, Set<Compostate<State, Action>> goalsToUpdate, int amountToUpdate) {
        int allChecked = 0;
        if(alreadyChecked.isEmpty()){
            for (Compostate<State, Action> s : goalsToUpdate){
                assertEquals("in updateDistances we have a set distance but we think we dont", (int) s.getBestControllable().getFirst(), -1);
                if(s.hasGoalChild()){
                    for (Pair<HAction<State, Action>,Compostate<State, Action>> childPair : s.getExploredChildren()){
                        Compostate<State, Action> child = childPair.getSecond();
                        HAction<State, Action> action = childPair.getFirst();
                        if(isGoal(child) && !goalsToUpdate.contains(child)){
                            assertTrue(child.getBestControllable().getFirst()!=-1);
                            if(action.isControllable()){
                                s.setBestControllable(child.getBestControllable().getFirst()+1,child);
                            }else{
                                s.setBestControllable(child.getBestControllable().getFirst()+1,null);
                            }
                            alreadyChecked.add(s);
                            --amountToUpdate;
                            break;
                        }
                    }
                }
            }
        }
        while(allChecked != amountToUpdate){
            Set<Compostate<State, Action>> toCheck = new HashSet<>();
            for(Compostate<State, Action> s : alreadyChecked){
                for(Pair<HAction<State, Action>,Compostate<State, Action>> p : s.getParents()){
                    Compostate<State, Action> parent = p.getSecond();
                    HAction<State, Action> action = p.getFirst();
                    if(goalsToUpdate.contains(parent) && parent.getBestControllable().getFirst() == -1){
                    //as explained in huang-kumar2008, if parent has controllable way of reaching alreadyChecked, that is the bestChild
                    //otherwise any controllable leading to Goal is valid
                        toCheck.add(parent);
                        if(action.isControllable() && (parent.getBestControllable().getFirst() == -1 || parent.getBestControllable().getFirst() > s.getBestControllable().getFirst())){
                            parent.setBestControllable(s.getBestControllable().getFirst()+1,s);
                        }else{
                            //el probl es que distancia le ponemos aca? porque si le ponemos una controlable random, no puede haber probl despues?
                            parent.setBestControllable(s.getBestControllable().getFirst()+1,null);
                        }
                    }
                }
            }
            allChecked += toCheck.size();
            alreadyChecked = toCheck;
        }
    }

    private void propagateGoal(Set<Compostate<State, Action>> newGoals, Set<Compostate<State, Action>> parent) {
        statistics.incPropagateGoalsCalls();

        Set<Compostate<State, Action>> ancestors;
        if(parent != null && parent.size()>0){
            ancestors = ancestorsUpToGoalOrError(parent);
            ancestors.addAll(parent);
        }else{
            ancestors = ancestorsUpToGoalOrError(newGoals);
        }

        Set<Compostate<State, Action>> c = new HashSet<>(ancestors);
        int previous_size = 0;
        //System.err.printLn("we are propagatingGoals with newGoals: " + newGoals.toString());

        while(previous_size != c.size()) {
            previous_size = c.size();

            Set<Compostate<State, Action>> targetsAncestors = gatherWinningTargetsAncestorsForPropagateGoal(c);

            //sacar los que son forzados a irse o no llegan a targets
            c.removeIf(state -> forcedByEnvironmentToLeave(state, c) || !targetsAncestors.contains(state));
        }

        for(Compostate<State, Action> state: c){
            setGoal(state, state.getPotentiallyGoodTransition());
        }

        //newGoals already have their distances, we start from them and update their ancestors distances
        if(!c.isEmpty()){
            updateDistances(newGoals,c,c.size());
        }
    }

    private void findNewErrors() {
        statistics.incFindNewErrorsCalls();

        //System.err.printLn("we are gatheringErrors with loop: " + loop.toString());
        boolean existsAPathToNoneOrGoal = false;

        for (Compostate<State, Action> state: loop) {
            if(!heuristic.fullyExplored(state)){
                existsAPathToNoneOrGoal = true;
                break;
            }
            for (Pair<HAction<State, Action>,Compostate<State, Action>> transition : state.getExploredChildren()) { //hasChildNoneNotInLoop
                Compostate<State, Action> child = transition.getSecond();

                if((isNone(child) && !loop.contains(child)) || isGoal(child)){
                    existsAPathToNoneOrGoal = true;
                    break;
                }
            }
        }

        if(!existsAPathToNoneOrGoal){
            for(Compostate<State, Action> state: loop){
                setError(state);
            }
            if (!isError(initial)){  // don't waste time propagating if initial state is already losing
                propagateError(loop, null);
            }
        } else {
            for(Compostate<State, Action> compostate : loop){
                heuristic.notifyStateIsNone(compostate);
            }
        }
    }

    private void propagateError(Set<Compostate<State, Action>> newErrors, Set<Compostate<State, Action>> parent) {
        statistics.incPropagateErrorsCalls();

        Set<Compostate<State, Action>> ancestors;
        if(parent != null && parent.size() > 0){
            ancestors = ancestorsUpToGoalOrError(parent);
            ancestors.addAll(parent);
        } else {
            ancestors = ancestorsUpToGoalOrError(newErrors);
        }

        Set<Compostate<State, Action>> winners = new HashSet<>(ancestors);
        //System.err.printLn("we are propagatingErrors with newErrors: " + newErrors.toString());
        int previous_size = 0;

        while(previous_size != winners.size()) {
            previous_size = winners.size();

            Set<Compostate<State, Action>> targetsAncestors = gatherWinningTargetsForPropagateError(ancestors);

            Iterator<Compostate<State, Action>> it = winners.iterator();
            while (it.hasNext()) {
                Compostate<State, Action> state = it.next();
                if(forcedToError(state) || !targetsAncestors.contains(state)){
                    it.remove();
                    setError(state);
                }
            }
        }

        for(Compostate<State, Action> state : winners){
            heuristic.notifyStateIsNone(state);
        }
    }

    private Set<Compostate<State, Action>> ancestorsUpToGoalOrError(Set<Compostate<State, Action>> states) {
        Set<Compostate<State, Action>> ancestorsSet = new HashSet<>();
        auxiliarListStates.addAll(states);
        visited.addAll(states);
        for (int i = 0; i < auxiliarListStates.size(); ++i) {
            Compostate<State, Action> state = auxiliarListStates.get(i);

            for (Pair<HAction<State, Action>,Compostate<State, Action>> ancestorActionAndState : state.getParents()) {
                Compostate<State, Action> ancestor = ancestorActionAndState.getSecond();

                if (isNone(ancestor) && visited.add(ancestor)){
                    ancestorsSet.add(ancestor);
                    auxiliarListStates.add(ancestor);
                }
            }
        }
        visited.clear();
        auxiliarListStates.clear();

        return ancestorsSet;
    }

    //fixme esta funcion y gatherWinningTargetsAncestorsForPropagateGoal son muy parecidas, se podrÃ­an unir
    private Set<Compostate<State, Action>> gatherWinningTargetsForPropagateError(Set<Compostate<State, Action>> c) {
        Set<Compostate<State, Action>> targets = new HashSet<>();
        for(Compostate<State, Action> state : c) {
            if (!heuristic.fullyExplored(state)) {
                targets.add(state);
            }
            for (Pair<HAction<State, Action>,Compostate<State, Action>> transition : state.getExploredChildren()) {
                Compostate<State, Action> child = transition.getSecond();

                if (!isError(child) && !c.contains(child)) {
                    targets.add(child);
                }
            }
        }
        return gatherTargetAncestors(c, targets);
    }

    private Set<Compostate<State, Action>> gatherWinningTargetsAncestorsForPropagateGoal(Set<Compostate<State, Action>> c) {
        Set<Compostate<State, Action>> targets = new HashSet<>();
        for(Compostate<State, Action> state : c){

            for (Pair<HAction<State, Action>,Compostate<State, Action>> transition : state.getExploredChildren()) {
                Compostate<State, Action> child = transition.getSecond();

                if(isGoal(child)){
                    targets.add(child);
                }
            }
        }

        return gatherTargetAncestors(c, targets);
    }

    private Set<Compostate<State, Action>> gatherTargetAncestors(Set<Compostate<State, Action>> c, Set<Compostate<State, Action>> targets) {
        Set<Compostate<State, Action>> targetsAncestors = new HashSet<>(targets);

        auxiliarListStates.addAll(targets);
        //agarro los ancestros de todos los targets, no agrego los goals, errores y paro al salir de c
        for (int i = 0; i < auxiliarListStates.size(); ++i) {
            Compostate<State, Action> state = auxiliarListStates.get(i);

            for (Pair<HAction<State, Action>,Compostate<State, Action>> predecesorActionAndState : state.getParents()) {
                Compostate<State, Action> parent = predecesorActionAndState.getSecond();

                if (!isNone(parent) || !c.contains(parent)) continue;

                parent.setPotentiallyGoodTransition(predecesorActionAndState.getFirst()); //fixme no hay que chequear nada?

                if (targetsAncestors.add(parent)) {
                    auxiliarListStates.add(parent);
                }
            }
        }
        auxiliarListStates.clear();
        return targetsAncestors;
    }

    private boolean forcedByEnvironmentToLeave(Compostate<State, Action> state, Set<Compostate<State, Action>> targets) {
        if(heuristic.hasUncontrollableUnexplored(state)){
            heuristic.notifyStateIsNone(state);
            return true;
        }
        boolean existsOneThatStays = false;
        for (Compostate<State, Action> child : state.getChildrenExploredThroughUncontrollable()) {
            if(!(targets.contains(child) || isGoal(child))){
                heuristic.notifyStateIsNone(state);
                return true;
            } else {
                existsOneThatStays = true;
            }
        }

        if(existsOneThatStays) return false; //hay al menos una U que lo deja

        for (Compostate<State, Action> child : state.getChildrenExploredThroughControllable()) {
            if(targets.contains(child) || isGoal(child)) return false;
        }

        heuristic.notifyStateIsNone(state);
        return true;
    }

    private boolean forcedToError(Compostate<State, Action> state) {
        boolean existsActionLeadingToNoneOrGoal = false;
        boolean fullyExplored = heuristic.fullyExplored(state);

        for (Compostate<State, Action> child : state.getChildrenExploredThroughUncontrollable()){
            if(isError(child)){
                return true;
            } else if(child != state){
                existsActionLeadingToNoneOrGoal = true;
            }
        }

        if(existsActionLeadingToNoneOrGoal){
            heuristic.notifyStateIsNone(state);
            return false;
        }

        for (Compostate<State, Action> child : state.getChildrenExploredThroughControllable()){
            if(!isError(child)/* && child != state*/) existsActionLeadingToNoneOrGoal = true;
        }

        heuristic.notifyStateIsNone(state);

        return !existsActionLeadingToNoneOrGoal && fullyExplored;
    }

//-----------------------------------------------

    private boolean closingALoop(Compostate<State, Action> parent, Compostate<State, Action> child) {
        if (child.wasExpanded()) { // if the child has already been considered then we might be closing a loop
            buildAncestorsDAG(child, parent);
            return !dag.getK(child).isEmpty(); // if we closed a loop
        } else {
            return false;
        }
    }


    /** Clears internal data used for efficient loop detection. */
    private void clearLoopDetection() {
        dag.clear();
    }


    /** Returns a map representing a DAG from parent to child.
     *  The child state in a terminal node indicates a loop. */
    private void buildAncestorsDAG(Compostate<State, Action> child, Compostate<State, Action> parent) {
        auxiliarListStates.clear();
        visited.clear();
        auxiliarListStates.add(parent);
        visited.add(parent);
        for (int i = 0; i < auxiliarListStates.size(); ++i) {
            Compostate<State, Action> state = auxiliarListStates.get(i);
            for (Pair<HAction<State, Action>,Compostate<State, Action>> predecesor : state.getParents()) {
                Compostate<State, Action> predState = predecesor.getSecond();
                if (!isNone(predState)) continue;
                dag.put(state, predState);
                //fixme esto se puede mejorar. lo cambiamos viendo si el loop era maximal!
                if (visited.add(predState))// && state != child) // Stop the DAG on child leaves
                    auxiliarListStates.add(predState);
            }
        }
        visited.clear();
        auxiliarListStates.clear();
    }

    /** Gathers the states enclosed by a loop over parent and child states.
     *  Additionally gathers the marked states in the loop. */
    private void gatherLoopStates(Compostate<State, Action> child) {
        probablyWinningStates.clear();
        loop = new HashSet<>();
        auxiliarListStates.add(child);

        for (int i = 0; i < auxiliarListStates.size(); ++i) {
            Compostate<State, Action> state = auxiliarListStates.get(i);
            for (Compostate<State, Action> successor : dag.getK(state)) {
                if (loop.add(successor)){
                    if(successor.hasGoalChild() || successor.marked) probablyWinningStates.add(successor);
                    auxiliarListStates.add(successor);
                }
            }
        }
        auxiliarListStates.clear();
        assert loop.contains(child); // Check this is an actual loop.
    }

    /** Marks a given state as a goal. */
    private void setGoal(Compostate<State, Action> state, HAction<State, Action> action) {
        assertFalse(isError(state));
        boolean wasGoal = isGoal(state);

        if(action == null){
            // fixme: tiene sentido tener esto y también a potentially good transition? (tomi)
            action = state.actionToGoal;
        }

        state.setStatus(Status.GOAL);
        broadcastNewClosedChildToParents(state, Status.GOAL);
        int distance = state.getChildDistance(action);
        if (distance < INF) distance++;
        //System.err.printLn(state + "marked as goal with distance " + distance);
        if (distance < state.getDistance())
            state.setDistance(distance);
        if(!wasGoal)
            setFinal(state);
    }

    /** Marks a given state as an error. */
    public void setError(Compostate<State, Action> state) {
        assert(isNone(state));

        state.setStatus(Status.ERROR);
        broadcastNewClosedChildToParents(state, Status.ERROR);
        setFinal(state);
    }

    /** No further exploration is needed from a given state */
    private void setFinal(Compostate<State,Action> state) {
        heuristic.notifyStateSetErrorOrGoal(state);
    }

    private void broadcastNewClosedChildToParents(Compostate<State, Action> state, Status status) {
        for (Pair<HAction<State, Action>,Compostate<State, Action>> ancestorActionAndState : state.getParents()) {
            HAction<State, Action> action = ancestorActionAndState.getFirst();
            Compostate<State, Action> ancestor = ancestorActionAndState.getSecond();
            if(status == Status.GOAL) ancestor.setHasGoalChild(action);
        }
    }

    public boolean isGoal(Compostate<State, Action> state) {
        return state.isStatus(Status.GOAL);
    }

    public boolean isError(Compostate<State, Action> state) {
        return state.isStatus(Status.ERROR);
    }

    private boolean isNone(Compostate<State,Action> state) {
        return state.isStatus(Status.NONE);
    }

    /** After the synthesis procedure this method builds a director in the
     *  form of an LTS by starting in the initial state and following all the
     *  non-closed descendants. If there is no controller for the given
     *  environment this method returns null. */
    private LTS<Long, Action> buildDirector() {
        long i = 0;
        LTSImpl<Long, Action> result = new LTSImpl<>(i);
        Map<Compostate<State, Action>, Long> ids = new HashMap<>();
        ids.put(initial, i++);
        result.addActions(alphabet.getActions());

        if(isGoal(initial)) {
            result.addState(ids.get(initial));
            descendants.add(initial);
        }

        Set<Compostate<State, Action>> directStates = newHashSet(initial);
        Set<Compostate<State, Action>> directMarked = new HashSet<>();
        if(initial.marked) directMarked.add(initial);
        Set<Long> resultHasControllable = new HashSet<>();

        while (!descendants.isEmpty()) {
            Compostate<State, Action> successor = descendants.remove();
            assertTrue("we added to the director a non goal state",isGoal(successor));
            for (Pair<HAction<State, Action>, Compostate<State, Action>> transition : successor.getExploredChildren()) {
                //we could simply add all uncontrollable childs but we need the "action"
                Action action = transition.getFirst().getAction();
                Compostate<State, Action> child = transition.getSecond();
                assertTrue("All Goal states should know their best distance to a marked state",successor.getBestControllable().getFirst()!=-1);
                boolean toAdd = false;
                if(!transition.getFirst().isControllable()){
                    assertTrue("there is an uncontrollable transition to non-Goal state in the controller",isGoal(child));
                    toAdd=true;
                }else{
                    if(!resultHasControllable.contains(ids.get(successor))){
                        if (successor.getBestControllable().getSecond() == null) {
                            if (isGoal(child)) {
                                toAdd = true;
                                successor.setBestControllable(successor.getBestControllable().getFirst(), child);
                            }
                        } else if (child == successor.getBestControllable().getSecond()) {
                            assertTrue("Best controllable child is not Goal", isGoal(child));
                            toAdd = true;
                        }
                    }
                }

                if (toAdd) {
                    if (!ids.containsKey(child)) {
                        ids.put(child, i++);
                        descendants.add(child);
                        result.addState(ids.get(child));
                        directStates.add(child);
                        if(child.marked) directMarked.add(child);
                    }
                    if(alphabet.getHAction(action).isControllable()){
                        resultHasControllable.add(ids.get(successor));
                    }

                    result.addTransition(ids.get(successor), action, ids.get(child));
                }
            }
        }
        statistics.setControllerUsedStates(result.getStates().size());
        statistics.setControllerUsedTransitions(result.getTransitionsNumber());
//        assertGoodDirector(result, directStates, directMarked);
        return result;
    }

    private void assertGoodDirector(LTSImpl<Long, Action> result, Set<Compostate<State, Action>> directStates, Set<Compostate<State, Action>> directMarked){
        //System.err.printLn("Director check is enabled and could be slowing down the solution");
        Map<Long, BinaryRelation<Action, Long>> trans = result.getTransitions();
        for(Long k : trans.keySet()){
            int c = 0;
            for(Pair<Action, Long> p : trans.get(k)){
                if(alphabet.getHAction(p.getFirst()).isControllable()) ++c;
            }
            assertTrue("we returned a Supervisor, not a Director (>1 controllable action)",c<=1);
        }

        Set<Compostate<State, Action>> markedAncestors = new HashSet<>(directMarked);
        visited.clear();
        auxiliarListStates.clear();
        auxiliarListStates.addAll(directMarked);
        visited.addAll(directMarked);
        for (int i = 0; i < auxiliarListStates.size(); ++i) {
            Compostate<State, Action> state = auxiliarListStates.get(i);

            for (Pair<HAction<State, Action>,Compostate<State, Action>> ancestorActionAndState : state.getParents()) {
                Compostate<State, Action> ancestor = ancestorActionAndState.getSecond();

                if (!directStates.contains(ancestor))
                    continue;
                markedAncestors.add(ancestor);
                if (visited.add(ancestor))
                    auxiliarListStates.add(ancestor);
            }
        }
        visited.clear();
        auxiliarListStates.clear();
        assertEquals("Not all states in the director can be extended to reach a marked state",markedAncestors,directStates);
    }


    /** This enum lists the available abstractions. */
    public enum HeuristicMode {
        Monotonic, Ready, Dummy, BFS, Debugging, TrainedAgent
    }


    /** Auxiliary function to put an element into a map, but keeping the minimum if already set. */
    // TODO NICO: mover a clase de utils
    public static <K, V extends Comparable<V>> boolean putmin(Map<K, V> map, K key, V value) {
        boolean result;
        V old = map.get(key);
        result = (old == null || old.compareTo(value) > 0);
        if (result) {
            map.put(key, value);
        }
        return result;
    }


    /** Auxiliary function to put an element into a map, but keeping the maximum if already set. */
    // TODO NICO: mover a clase de utils
    public static <K, V extends Comparable<V>> boolean putmax(Map<K, V> map, K key, V value) {
        boolean result;
        V old = map.get(key);
        result = (old == null || old.compareTo(value) < 0);
        if (result) {
            map.put(key, value);
        }
        return result;
    }


}