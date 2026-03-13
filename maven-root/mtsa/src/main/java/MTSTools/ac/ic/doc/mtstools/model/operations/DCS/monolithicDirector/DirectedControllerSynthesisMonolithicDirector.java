package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.monolithicDirector;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.MarkedWithIllegalLTSImpl;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.Statistics;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.Compostate;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.HAction;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**This class implements the algorithm presented in
 * Directed Control of Discrete Event Systems for Safety and Nonblocking
 * Jing Huang, Student Member, IEEE, and Ratnesh Kumar, Fellow, IEEE.
 * Thoug it is not made by the authors.**/
public class DirectedControllerSynthesisMonolithicDirector<State, Action>{

    // the main plant to control
    MarkedWithIllegalLTSImpl<State, Action> plant;
    // the reversed plant, for ease of use
    LTS<State, Action> reversedPlant;

    Set<Action> controllables;
    Set<Action> disturbances;

    Set<State> X_e;

    //aux structure
    List<Set<State>> SLINs = new ArrayList<>();
    List<Set<State>> SLAs = new ArrayList<>();

    //result
    MarkedWithIllegalLTSImpl<Long,Action> director;

    /** Statistic information about the procedure. */
    private Statistics statistics = new Statistics();

    /** This method starts the monolithic directed synthesis of a director.
     *  @return the controller for the environment in the form of an LTS that
     *      when composed with the environment reaches the goal, only by
     *      enabling or disabling controllable actions and monitoring
     *      uncontrollable actions. Or null if no such controller exists. */
    public LTS<Long,Action> synthesize(
            MarkedWithIllegalLTSImpl<State, Action> inputPlant,
            Set<Action> inputControllable,
            Set<Action> inputDisturbances)
    {
        Set<Action> auxIntersection = new HashSet<>(inputControllable);
        auxIntersection.retainAll(inputDisturbances);
        assert(auxIntersection.isEmpty()); //disturbances cannot be controllable
        director = new MarkedWithIllegalLTSImpl<Long, Action>(0L);
        director.addActions(inputPlant.getActions());

        plant = inputPlant;
        controllables = inputControllable;
        disturbances = inputDisturbances;

        statistics.clear();
        int statesAmount = plant.getStates().size();
        for(int i = 0; i<statesAmount; i++) statistics.incExpandedStates();
        int transitionsAmount = plant.getTransitionsNumber();
        for(int i = 0; i<transitionsAmount; i++) statistics.incExpandedTransitions();
        statistics.start();

        buildReversedPlant();
        buildX_e();

        algorithm4();
        //uses plant to build director

        statistics.end();

        if(director != null){
            statistics.setControllerUsedStates(director.getStates().size());
            statistics.setControllerUsedTransitions(director.getTransitionsNumber());
            assertGoodDirector(director);
        }
        return director;
    }

    private void buildReversedPlant(){
        reversedPlant = new LTSImpl<>(plant.getInitialState());
        reversedPlant.addStates(plant.getStates());
        reversedPlant.addActions(plant.getActions());
        for (State s : plant.getStates()){
            for(Pair<Action, State> child : plant.getTransitions(s)){
                reversedPlant.addTransition(child.getSecond(), child.getFirst(), s);
            }
        }
    }

    /*see definition 2 in the paper*/
    private void buildX_e(){
        X_e = new HashSet<>();
        for (State s : plant.getStates()) {
            boolean isTerminal = plant.getTransitions(s).isEmpty();
            boolean isMarked = plant.isMarked(s);
            boolean hasUncontrollable = false;
            boolean hasUncontrollableNotDisturbance = false;
            boolean hasControllable = false;
            for (Pair<Action, State> child : plant.getTransitions(s)) {
                Action a = child.getFirst();
                if (!controllables.contains(a)) {
                    hasUncontrollable = true;
                    if (!disturbances.contains(a)) {
                        hasUncontrollableNotDisturbance = true;
                    }
                } else {
                    hasControllable = true;
                }
            }
            if ((isMarked && !isTerminal && !hasUncontrollable)
                    || (hasControllable && !hasUncontrollableNotDisturbance && hasUncontrollable)) {
                //see definition 2 in paper
                X_e.add(s);
            }
        }
    }

    private Long stateToLong(State s){
        return Long.parseLong(s.toString());
    }

    private Set<State> reachInOneStep(Set<State> X_mono,Set<State> X_k){
        Set<State> result = new HashSet<>();
        for(State s : X_k){
            for(Pair<Action, State> parent : reversedPlant.getTransitions(s)){
                if(X_mono.contains(parent.getSecond())){
                    result.add(parent.getSecond());
                }
            }
        }
        return result;
    }

    private void fromSupervisorToDirector(Set<State> X_mono, Set<State> X_k, Set<State> X_kprev, Set<State> X_pico){
        while(!X_mono.isEmpty()){
            for(State s : X_k){
                Pair<Action,State> controllableToX_prev = null;
                Pair<Action,State> controllableToX_pico = null;
                for(Pair<Action, State> child : plant.getTransitions(s)){
                    if(controllables.contains(child.getFirst())){
                        if(X_pico.contains(child.getSecond())){
                            controllableToX_pico = child;
                        }
                        if(X_kprev.contains(child.getSecond())) {
                            controllableToX_prev = child;
                        }
                    }
                }
                if(controllableToX_prev != null){
                    Action a = controllableToX_prev.getFirst();
                    Long t = stateToLong(controllableToX_prev.getSecond());
                    director.addTransition(stateToLong(s),a,t);
                }
                else if(controllableToX_pico != null){
                    Action a = controllableToX_pico.getFirst();
                    Long t = stateToLong(controllableToX_pico.getSecond());
                    director.addTransition(stateToLong(s),a,t);
                }
                else {
                    assert (!X_e.contains(s));
                }
            }
            X_mono.removeAll(X_k);
            X_kprev = X_k;
            X_k = reachInOneStep(X_mono,X_k);
        }
    }

    private void algorithm2(Set<State> SLIN){
        Set<State> X_k = new HashSet<State>();
        Set<State> X_kprev = new HashSet<State>(); //k-1
        Set<State> X_mono = new HashSet<State>(SLIN);
        for (State s : SLIN) {
            director.addState(stateToLong(s));
        }
        for(State s : SLIN) {
            if(plant.isIllegal(s)) {
                director.makeIllegal(stateToLong(s));
            }
            if(plant.isMarked(s)){
                director.mark(stateToLong(s));
                X_k.add(s);
            }
            for(Pair<Action, State> child : plant.getTransitions(s)){
                if(!controllables.contains(child.getFirst())){
                    assert (SLIN.contains(child.getSecond()));
                    director.addTransition(stateToLong(s),child.getFirst(),stateToLong(child.getSecond()));
                }
            }
        }
        fromSupervisorToDirector(X_mono,X_k,X_kprev,SLIN);
    }

    private void algorithm3(Set<State> SLA, Set<State> X_r){
        Set<State> X_k = new HashSet<State>();
        Set<State> X_kprev = X_r; //k-1
        Set<State> X_mono = new HashSet<State>(SLA);
        for(State s : SLA) {
            director.addState(stateToLong(s));
        }
        for(State s : SLA) {
            if(plant.isIllegal(s)) {
                director.makeIllegal(stateToLong(s));
            }
            if(plant.isMarked(s)){
                director.mark(stateToLong(s));
            }
            for(Pair<Action, State> child : plant.getTransitions(s)){
                if(!controllables.contains(child.getFirst())){
                    assert (SLA.contains(child.getSecond()) || X_r.contains(child.getSecond()));
                    director.addTransition(stateToLong(s),child.getFirst(),stateToLong(child.getSecond()));
                }
                if(X_r.contains(child.getSecond())){
                    X_k.add(s);
                }
            }
        }
        fromSupervisorToDirector(X_mono,X_k,X_kprev,SLA);
    }

    /*given a plant, computes a nonblocking director*/
    private void algorithm4(){
        Set<State> X = new HashSet<>(plant.getStates());
        SLINs.clear();
        getSLINsof(X); //the result is in SLINs
        if (SLINs.isEmpty()){
            director = null; // no nonblocking director exists
            return;
        }

        Set<State> X_k = new HashSet<State>();
        for (Set<State> Slin : SLINs){
            algorithm2(Slin);
            X_k.addAll(Slin);
        }

        Set<State> X_prime = new HashSet<State>(plant.getStates());
        while(true){
            if (!director.getTransitions(director.getInitialState()).isEmpty()){
                //if the director knows how to behave for the initial state, we have a working director
                return;
            }

            X_prime.removeAll(X_k);
            SLAs.clear();
            Set<State> modifiable_X_prime = new HashSet<>(X_prime);
            getSLAsof(modifiable_X_prime, X_k); //the result is in SLAs
            if (SLAs.isEmpty()){
                director = null; // no nonblocking director exists
                return;
            }

            for (Set<State> Sla : SLAs){
                algorithm3(Sla, X_k);
            }

            for (Set<State> Sla : SLAs){
                X_k.addAll(Sla);
            }
        }
    }

    /*Algorithm 6 in the paper. Given a reference set of states X_r,
     * and a set of states X, computes SLA(X, X_r). The states that controllably stay in X or reach X_r.
     * Input set X can be modified */
    private void getSLAsof(Set<State> X, Set<State> X_r){
        X.removeAll(plant.getIllegalStates());

        int previousSize = 0;
        while (previousSize != X.size()){
            // in each iteration, remove U(X,X_r): the states of X that are not X_r attractable.
            previousSize = X.size();

            X.retainAll(predecesorsOfIn(X_r, X));
            Set<State> stayIn = new HashSet<>(X);
            stayIn.addAll(X_r);

            Iterator<State> it = X.iterator();
            while(it.hasNext()){
                State current = it.next();
                if(forcedToLeave(current, stayIn)){
                    stayIn.remove(current);
                    it.remove();
                }
            }
        }

        List<Set<State>> potentialSLINs = partitionIntoSCC(X);

        for (Set<State> potSLIN : potentialSLINs){
            Set<State> aux = new HashSet<>(potSLIN);
            aux.addAll(X_r);
            if (isAttractable(potSLIN, aux, X_r)){
                SLAs.add(potSLIN);
            } else{
                getSLAsof(potSLIN, X_r);
            }
        }
    }

    /*returns true if all statesInDoubt have a path to X_r only traversing states in
    * "stayIn" and can controllably stay in "stayIn". X_r and statesInDoubt are subsets of "stayIn"*/
    private boolean isAttractable(Set<State> statesInDoubt, Set<State> stayIn, Set<State> X_r){
        for (State s : statesInDoubt){
            if (forcedToLeave(s, stayIn)){
                return false;
            }
        }

        Set<State> predecesors = new HashSet<>(statesInDoubt);
        predecesors.retainAll(predecesorsOfIn(X_r, stayIn));
        if(predecesors.size() != statesInDoubt.size()){
            //there is a stateInDoubt that cannot reach X_r through states in stayIn
            return false;
        }

        return true;
    }

    /*targets are included in the return value*/
    private Set<State> predecesorsOfIn(Set<State> targets, Set<State> stayIn){
        Queue<State> remaining = new LinkedList<>(targets);
        Set<State> visited = new HashSet<>(targets);
        while(!remaining.isEmpty()){
            State current = remaining.poll();
            for(Pair<Action, State> parent : reversedPlant.getTransitions(current)){
                State parentState = parent.getSecond();
                if(stayIn.contains(parentState) && !visited.contains(parentState)){
                    remaining.add(parentState);
                    visited.add(parentState);
                }
            }
        }
        return visited;
    }

    /*Algorithm 5 in the paper. Modifies input set of states.
    * Needs SLINs to be cleared before calling this function*/
    private void getSLINsof(Set<State> states){
        states.removeAll(plant.getIllegalStates());

        int previousSize = 0;
        while (previousSize != states.size()){
            previousSize = states.size();
            Iterator<State> it = states.iterator();
            while(it.hasNext()){
                if(forcedToLeave(it.next(), states)){
                    it.remove();
                }
            }
        }

        List<Set<State>> potentialSLINs = partitionIntoSCC(states);
        for (Set<State> potSLIN : potentialSLINs){
            if (isNonBlocking(potSLIN)){
                if(isInvariant(potSLIN)){
                    SLINs.add(potSLIN);
                } else{
                    getSLINsof(potSLIN);
                }
            }
        }
    }

    /*checks if any state is forced to leave the input set of states */
    private boolean isInvariant(Set<State> states){
        for (State s : states){
            if (forcedToLeave(s, states)){
                return false;
            }
        }
        return true;
    }

    /*assumes the input is a strongly connected component. So checking nonblocking
    * is just checking that is has a marked state */
    private boolean isNonBlocking(Set<State> SCC){
        for (State s : SCC){
            if (plant.isMarked(s)){
                return true;
            }
        }
        return false;
    }

    /*note that this function takes into account controlability as described
    * in the paper. If "s" has only one controllable transition, it is forced to take it*/
    private boolean forcedToLeave(State s, Set<State> goodStates){
        boolean hasControllableToGoodState = false;
        for(Pair<Action, State> child : plant.getTransitions(s)){
            Action a = child.getFirst();
            State childS = child.getSecond();
            boolean aIsControllable = controllables.contains(a);
            if (!aIsControllable && !goodStates.contains(childS)){
                return true;
            }
            if (aIsControllable && goodStates.contains(childS)){
                hasControllableToGoodState = true;
            }
        }
        if(X_e.contains(s) && !hasControllableToGoodState){
            return true;
        }
        return false;
    }

    /*receives a set of states, a part of the plant, and returns a partition of that set into Strongly
    * Connected Components. Note that a single state is a SCC because of the empty path.
    * Also, ignore loops that include states outside of the input set of states.*/
    private List<Set<State>> partitionIntoSCC(Set<State> states){
        /* Kosaraju’s algorithm
        1) Create an empty stack ‘S’ and do DFS traversal of a graph. In DFS traversal, after calling recursive DFS for
            adjacent vertices of a vertex, push the vertex to stack. In the above graph, if we start DFS from vertex 0,
            we get vertices in stack as 1, 2, 4, 3, 0.
        2) Reverse directions of all arcs to obtain the transpose graph. (reversedPlant)
        3) One by one pop a vertex from S while S is not empty. Let the popped vertex be ‘v’. Take v as source and
            do DFS (call DFSUtil(v)). The DFS starting from v prints strongly connected component of v. In the above
            example, we process vertices in order 0, 3, 4, 2, 1 (One by one popped from stack).*/
        Stack<State> SCCorder = new Stack<>();
        Set<State> visited = new HashSet<>();
        for (State current : states){
            if (!visited.contains(current)){
                forwardDFS(current, states, visited, SCCorder);
            }
        }

        List<Set<State>> result = new ArrayList<>();
        visited.clear();
        while (!SCCorder.isEmpty()){
            State current = SCCorder.pop();
            if(!visited.contains(current)){
                Set<State> currentSCC = new HashSet<>();
                backwardsDFS(current, states, visited, currentSCC);
                assert(!currentSCC.isEmpty());
                result.add(currentSCC);
            }
        }
        return result;
    }

    private void backwardsDFS(State current, Set<State> states, Set<State> visited, Set<State> currentSCC){
        visited.add(current);
        Stack<State> stack = new Stack<>();
        stack.add(current);
        while(!stack.empty()){
            current = stack.peek();
            boolean hasUnvisitedChild = false;
            for(Pair<Action, State> child : reversedPlant.getTransitions(current)){
                if(states.contains(child.getSecond()) && !visited.contains(child.getSecond())){
                    hasUnvisitedChild = true;
                    visited.add(child.getSecond());
                    stack.push(child.getSecond());
                }
            }
            if(!hasUnvisitedChild) {
                stack.pop();
                currentSCC.add(current);
            }
        }
    }

    private void forwardDFS(State current, Set<State> states, Set<State> visited, Stack<State> SCCorder){
        visited.add(current);
        Stack<State> stack = new Stack<>();
        stack.add(current);
        while(!stack.empty()){
            current = stack.peek();
            boolean hasUnvisitedChild = false;
            for(Pair<Action, State> child : plant.getTransitions(current)){
                if(states.contains(child.getSecond()) && !visited.contains(child.getSecond())){
                    hasUnvisitedChild = true;
                    visited.add(child.getSecond());
                    stack.push(child.getSecond());
                }
            }
            if(!hasUnvisitedChild) {
                stack.pop();
                SCCorder.add(current);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void assertGoodDirector(LTSImpl<Long, Action> result){
        System.err.println("Director check is enabled and could be slowing down the solution");
        Set<Long> markedAncestors = new HashSet<>();
        for(Long s : result.getStates()){
            int c = 0;
            if(plant.isMarked((State) s)) markedAncestors.add(s);
            for (Pair<Action, Long> trans : result.getTransitions(s)){
                if(controllables.contains(trans.getFirst())) ++c;
            }
            boolean goodState = X_e.contains((State)s) ? (c==1) : (c<=1);
            assertTrue("we returned a Supervisor, not a Director (>1 controllable action)",goodState);

            for (Pair<Action, State> transPlant : plant.getTransitions((State)s)){
                if(!controllables.contains(transPlant.getFirst())){
                    boolean resultHasTrans = false;
                    for (Pair<Action, Long> transResult : result.getTransitions(s)){
                        if (transPlant.getFirst().equals(transResult.getFirst()) &&
                                (stateToLong(transPlant.getSecond())).equals(transResult.getSecond())) {
                            resultHasTrans = true;
                            break;
                        }
                    }
                    assertTrue("we are not returning a legal controller, it prohibits uncontrollable transitions",
                            resultHasTrans);
                }
            }
        }

        Set<Long> visited = new HashSet<>(markedAncestors);
        List<Long> auxiliarListStates = new ArrayList<>(markedAncestors);
        for (int i = 0; i < auxiliarListStates.size(); ++i) {
            Long s = auxiliarListStates.get(i);

            for (Pair<Action, State> ancestorActionAndState : reversedPlant.getTransitions((State)s)) {
                Long ancestor = stateToLong(ancestorActionAndState.getSecond());
                if(result.getStates().contains(ancestor) && result.getTransitions(ancestor)
                        .contains(new Pair(ancestorActionAndState.getFirst(),s))){
                    markedAncestors.add(ancestor);
                    if (visited.add(ancestor))
                        auxiliarListStates.add(ancestor);
                }
            }
        }
        assertEquals("Not all states in the director can be extended to reach a marked state",
                            markedAncestors,result.getStates());
    }

    /** Returns the statistic information about the procedure. */
    public Statistics getStatistics() {
        return statistics;
    }
}
