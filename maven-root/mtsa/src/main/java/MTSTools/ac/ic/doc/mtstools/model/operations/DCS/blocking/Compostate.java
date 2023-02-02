package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelationImpl;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.abstraction.HEstimate;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.abstraction.Ranker;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.abstraction.Recommendation;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.assertSame;

/** This class represents a state in the parallel composition of the LTSs
 *  in the environment. These states are used to build the fragment of the
 *  environment required to reach the goal on-the-fly. The controller for
 *  the given problem can then be extracted directly from the partial
 *  construction achieved by using these states. */
public class Compostate<State, Action> implements Comparable<Compostate<State, Action>> {
    private final DirectedControllerSynthesisBlocking<State, Action> directedControllerSynthesisBlocking;
    /** States by each LTS in the environment that conform this state. */
    private final List<State> states; // Note: should be a set of lists for non-deterministic LTSs

    /** The estimated distance to the goal from this state. */
    private HEstimate estimate;

    /** Indicates whether this state is a goal (1) or an error (-1) or not yet known (0). */
    private Status status;

    /** The real distance to the goal state from this state. */
    private int distance;

    /** Depth at which this state has been expanded. */
    private int depth;

    /** A ranking of the outgoing transitions from this state. */
    public List<Recommendation<Action>> recommendations;

    /** An iterator for the ranking of recommendations. */
    private Iterator<Recommendation<Action>> recommendit;

    /** Current recommendation (or null). */
    public Recommendation<Action> recommendation;

    /** Indicates whether the state is actively being used. */
    private boolean live;

    /** Indicates whether the state is in the open queue. */
    public boolean inOpen;

    /** Indicates whether the state is controlled or not. */
    private boolean controlled;

    /** Indicates what guarantees the compostate fulfills */
    public final Set<Integer> markedByGuarantee;

    /** Indicates what assumptions the compostate negates */
    public final Set<Integer> markedByAssumption;

    private Integer loopID;
    private Integer bestDistToWinLoop;

    /** Children states expanded following a recommendation of this state. */
    private final BinaryRelation<HAction<Action>, Compostate<State, Action>> exploredChildren;

    /** Children states expanded through uncontrollable transitions. */
    private final Set<Compostate<State, Action>> childrenExploredThroughUncontrollable;

    /** Children states expanded through controllable transitions. */
    private final Set<Compostate<State, Action>> childrenExploredThroughControllable;

    /** Parents that expanded into this state. */
    private final BinaryRelation<HAction<Action>, Compostate<State, Action>> parents;

    /** Set of actions enabled from this state. */
    private final Set<HAction<Action>> transitions;

    /** Stores target states (i.e., already visited marked states) to reach from this state. */
    private List<Set<State>> targets = emptyList();

    private boolean hasGoalChild = false;
    private boolean hasErrorChild = false;


    /** Constructor for a Composed State. */
    public Compostate(DirectedControllerSynthesisBlocking<State, Action> directedControllerSynthesisBlocking, List<State> states) {
        this.directedControllerSynthesisBlocking = directedControllerSynthesisBlocking;
        this.states = states;
        this.status = Status.NONE;
        this.distance = DirectedControllerSynthesisBlocking.INF;
        this.depth = DirectedControllerSynthesisBlocking.INF;
        this.live = false;
        this.inOpen = false;
        this.controlled = true; // we assume the state is controlled until an uncontrollable recommendation is obtained
        this.exploredChildren = new BinaryRelationImpl<>();
        this.childrenExploredThroughUncontrollable = new HashSet<>();
        this.childrenExploredThroughControllable = new HashSet<>();
        this.parents = new BinaryRelationImpl<>();
        this.loopID = -1;
        this.bestDistToWinLoop = -1;
        this.markedByGuarantee = new HashSet<>();
        for(Map.Entry<Integer, Integer> entry : directedControllerSynthesisBlocking.guarantees.entrySet()) {
            int gNumber = entry.getKey();
            int gIndex = entry.getValue();

            if (directedControllerSynthesisBlocking.defaultTargets.get(gIndex).contains(states.get(gIndex))) {
                markedByGuarantee.add(gNumber);
                directedControllerSynthesisBlocking.composByGuarantee.get(gNumber).add(this);
            }
        }

        this.markedByAssumption = new HashSet<>();
        for(Map.Entry<Integer, Integer> entry : directedControllerSynthesisBlocking.assumptions.entrySet()) {
            int aNumber = entry.getKey();
            int aIndex = entry.getValue();

            if (directedControllerSynthesisBlocking.defaultTargets.get(aIndex).contains(states.get(aIndex))) {
                markedByAssumption.add(aNumber);
                directedControllerSynthesisBlocking.notComposByAssumption.get(aNumber).add(this);
            }
        }

        this.transitions = buildTransitions();
    }


    /** Returns the states that conform this composed state. */
    public List<State> getStates() {
        return states;
    }


    /** Returns the distance from this state to the goal state (INF if not yet computed). */
    public int getDistance() {
        return distance;
    }


    /** Sets the distance from this state to the goal state. */
    public void setDistance(int distance) {
        this.distance = distance;
    }


    /** Returns the depth of this state in the exploration tree. */
    public int getDepth() {
        return depth;
    }


    /** Sets the depth for this state. */
    public void setDepth(int depth) {
        if (this.depth > depth)
            this.depth = depth;
    }


    /** Indicates whether this state has been evaluated, that is, if it has
     *  a valid ranking of recommendations. */
    public boolean isEvaluated() {
        return recommendations != null;
    }

    public boolean isDeadlock(){
        return getTransitions().isEmpty();
    }


    ///** Returns whether this state is marked (i.e., all its components are marked). */
    //public boolean isMarked() { // removed during aggressive inlining
    //  return marked;
    //}


    /** Returns the target states to be reached from this state as a list of sets,
     *  which at the i-th position holds the set of target states of the i-th LTS. */
    public List<Set<State>> getTargets() {
        return targets;
    }


    /** Returns the target states of a given LTS to be reached from this state. */
    @SuppressWarnings("unchecked")
    public Set<State> getTargets(int lts) {
        return targets.isEmpty() ? (Set<State>)emptySet() : targets.get(lts);
    }


    /** Sets the given set as target states for this state (creates
     *  aliasing with the argument set). */
    public void setTargets(List<Set<State>> targets) {
        this.targets = targets;
    }


    /** Adds a state to this state's targets. */
    public void addTargets(Compostate<State, Action> compostate) {
        List<State> states = compostate.getStates();
        if (targets.isEmpty()) {
            targets = new ArrayList<>(directedControllerSynthesisBlocking.ltssSize);
            for (int lts = 0; lts < directedControllerSynthesisBlocking.ltssSize; ++lts)
                targets.add(new HashSet<>());
        }
        for (int lts = 0; lts < directedControllerSynthesisBlocking.ltssSize; ++lts)
            targets.get(lts).add(states.get(lts));
    }


    /** Returns this state's status. */
    public Status getStatus() {
        return status;
    }


    /** Sets this state's status. */
    public void setStatus(Status status) {
//            logger.fine(this.toString() + " status was: " + this.status + " now is: " + status);
        if (this.status != Status.ERROR || status == Status.ERROR)
            this.status = status;
    }


    /** Indicates whether this state's status equals some other status. */
    public boolean isStatus(Status status) {
        return this.status == status;
    }

    public void setLoopID(Integer loopID){
        this.loopID = loopID;
    }

    public Integer getLoopID(){
        return this.loopID;
    }

    public void setBestDistToWinLoop(Integer bestDistToWinLoop){
        this.bestDistToWinLoop = bestDistToWinLoop;
    }

    public Integer getBestDistToWinLoop(){
        return this.bestDistToWinLoop;
    }

    /** Sorts this state's recommendations in order to be iterated properly. */
    public Recommendation<Action> rankRecommendations() {
        Recommendation<Action> result = null;
        if (!recommendations.isEmpty()) {
            recommendations.sort(new Ranker<>());
            result = recommendations.get(0);
        }
        return result;
    }


    /** Sets up the recommendation list. */
    public void setupRecommendations() {
        if (recommendations == null)
            recommendations = new ArrayList<>();
    }


    /** Adds a new recommendation to this state and returns whether an
     *  error action has been introduced (no other recommendations should
     *  be added after an error is detected).
     *  Recommendations should not be added after they have been sorted and
     *  with an iterator in use. */
    public boolean addRecommendation(HAction<Action> action, HEstimate estimate) {
        boolean uncontrollableAction = !action.isControllable();
        if (controlled) { // may not work with lists of recommendations
            if (uncontrollableAction)
                controlled = false;
        }
        if (!estimate.isConflict()) // update recommendation
            recommendations.add(new Recommendation(action, estimate));
        boolean error = uncontrollableAction && estimate.isConflict();
        if (error){// an uncontrollable state with at least one INF estimate is an automatic error
            recommendations.clear();
            this.setStatus(Status.ERROR);
        }
        return error;
    }


    /** Returns whether the iterator points to a valid recommendation. */
    public boolean hasValidRecommendation() {
        return /*isEvaluated()*/recommendations != null/**/ && recommendation != null; // replaced during aggressive inlining
    }


    /** Returns whether the iterator points to a valid uncontrollable recommendation. */
    public boolean hasValidUncontrollableRecommendation() {
        return /*hasValidRecommendation()*/recommendation != null/**/ && !recommendation.getAction().isControllable(); // replaced during aggressive inlining
    }

    /** Returns whether the iterator points to a valid controllable recommendation. */
    public boolean hasValidControllableRecommendation() { //strongly relies on recommendations being uncontrollable first and controllable last
        return recommendation != null && recommendations.get(recommendations.size() - 1).getAction().isControllable();
    }

    /** Advances the iterator to the next recommendation. */
    public Recommendation<Action> nextRecommendation() {
        Recommendation<Action> result = recommendation;
        updateRecommendation();
        return result;
    }


    /** Initializes the recommendation iterator guaranteeing representation invariants. */
    public void initRecommendations() {
        recommendit = recommendations.iterator();
        updateRecommendation();
    }


    /** Initializes the recommendation iterator and current estimate for the state. */
    private void updateRecommendation() {
        if (recommendit.hasNext()) {
            recommendation = recommendit.next();
            estimate = recommendation.getEstimate(); // update this state estimate in case the state is reopened
        } else {
            recommendation = null;
        }
    }


    /** Clears all recommendations from this state. */
    public void clearRecommendations() {
        if (isEvaluated()) {
            recommendations.clear();
            recommendit = null;
            recommendation = null;
        }
    }


    /** Returns whether this state is being actively used. */
    public boolean isLive() {
        return live;
    }

    public boolean hasGoalChild(){
        return hasGoalChild;
    }

    public void setHasGoalChild() {
        this.hasGoalChild = true;
    }


    public boolean hasErrorChild(){
        return hasErrorChild;
    }

    public void setHasErrorChild() {
        this.hasErrorChild = true;
    }


    /** Returns whether this state has a child with the given status. */
    public boolean hasStatusChild(Status status) {
        boolean result = false;
        for (Pair<HAction<Action>, Compostate<State, Action>> transition : getExploredChildren()) {
            if (result = transition.getSecond().status == status) break;
        }
        return result;
    }


    /** Adds this state to the open queue (reopening it if was previously closed). */
    public boolean open() {
        assertSame("reopening compostate " + toString() + " with status != NONE", status, Status.NONE);
        boolean result = false;
        live = true;
        if (!inOpen) {
            if (inOpen = !hasStatusChild(Status.NONE)) {
                result = directedControllerSynthesisBlocking.open.add(this);
            } else { // we are reopening a state, thus we reestablish it's exploredChildren instead
                for (Pair<HAction<Action>, Compostate<State, Action>> transition : getExploredChildren()) {
                    Compostate<State, Action> child = transition.getSecond();
                    if (child.isStatus(Status.NONE) && child.hasValidRecommendation()){
                        if (!child.isLive())// || (!child.inOpen && !transition.getFirst().isControllable()))
                            result |= child.open();
                    }
                }
                if (inOpen = (!result || isControlled()))
                    result = directedControllerSynthesisBlocking.open.add(this);
            }
        }
        return result;
    }


    /** Closes this state to avoid further exploration. */
    public void close() {
        live = false;
    }


    /** Returns whether this state is controllable or not. */
    public boolean isControlled() {
        return controlled;
    }


    /** Returns the set of actions enabled from this composed state. */
    public Set<HAction<Action>> getTransitions() {
        return transitions;
    }

    /** Initializes the set of actions enabled from this composed state. */
    private Set<HAction<Action>> buildTransitions() { // Note: here I can code the wia and ia behavior for non-deterministic ltss
        Set<HAction<Action>> result = new HashSet<>();
        if (directedControllerSynthesisBlocking.facilitators == null) {
            for (int i = 0; i < states.size(); ++i) {
                for (Pair<Action,State> transition : directedControllerSynthesisBlocking.ltss.get(i).getTransitions(states.get(i))) {
                    HAction<Action> action = directedControllerSynthesisBlocking.alphabet.getHAction(transition.getFirst());
                    directedControllerSynthesisBlocking.allowed.add(i, action);
                }
            }
        } else {
            for (int i = 0; i < states.size(); ++i)
                if (!directedControllerSynthesisBlocking.facilitators.get(i).equals(states.get(i)))
                    for (Pair<Action,State> transition : directedControllerSynthesisBlocking.ltss.get(i).getTransitions(directedControllerSynthesisBlocking.facilitators.get(i))) {
                        HAction<Action> action = directedControllerSynthesisBlocking.alphabet.getHAction(transition.getFirst());
                        directedControllerSynthesisBlocking.allowed.remove(i, action); // remove old non-shared facilitators transitions
                    }
            for (int i = 0; i < states.size(); ++i)
                if (!directedControllerSynthesisBlocking.facilitators.get(i).equals(states.get(i)))
                    for (Pair<Action,State> transition : directedControllerSynthesisBlocking.ltss.get(i).getTransitions(states.get(i))) {
                        HAction<Action> action = directedControllerSynthesisBlocking.alphabet.getHAction(transition.getFirst());
                        directedControllerSynthesisBlocking.allowed.add(i, action); // add new non-shared facilitators transitions
                    }
        }
        result.addAll(directedControllerSynthesisBlocking.allowed.getEnabled());
        directedControllerSynthesisBlocking.facilitators = states;

        //this removes mixed compostates, if there are uncontrollable transitions we ignore controllable ones
        boolean hasU = false;
        for(HAction<Action> ha : result){
            if (!ha.isControllable()) hasU = true;
        }
        if(hasU){
            result.removeIf(HAction::isControllable);
        }
        return result;
    }


    /** Adds an expanded child to this state. */
    public void addChild(HAction<Action> action, Compostate<State, Action> child) {
        if(action.isControllable()){
            childrenExploredThroughControllable.add(child);
        }else {
            childrenExploredThroughUncontrollable.add(child);
        }

        exploredChildren.addPair(action, child);
    }


    /** Returns all transition leading to exploredChildren of this state. */
    public BinaryRelation<HAction<Action>, Compostate<State, Action>> getExploredChildren() {
        return exploredChildren;
    }

    public Set<Compostate<State, Action>> getChildrenExploredThroughUncontrollable() {
        return childrenExploredThroughUncontrollable;
    }

    public Set<Compostate<State, Action>> getChildrenExploredThroughControllable() {
        return childrenExploredThroughControllable;
    }



    /** Returns all exploredChildren of this state. */
    public List<Compostate<State, Action>> getExploredChildrenCompostates() {
        List<Compostate<State, Action>> childrenCompostates = new ArrayList<>();
        for (Pair<HAction<Action>, Compostate<State, Action>> transition : exploredChildren){
            Compostate<State, Action> child = transition.getSecond();
            childrenCompostates.add(child);
        }
        return childrenCompostates;
    }


    /** Returns the distance to the goal of a child of this compostate following a given action. */
    public int getChildDistance(HAction<Action> action) {
        int result = DirectedControllerSynthesisBlocking.UNDEF; // exploredChildren should never be empty or null
        for (Compostate<State, Action> compostate : exploredChildren.getImage(action)) { // Note: maximum of non-deterministic exploredChildren
            if (result < compostate.getDistance())
                result = compostate.getDistance();
        }
        return result;
    }


    /** Adds an expanded parent to this state. */
    public void addParent(HAction<Action> action, Compostate<State, Action> parent) {
        parents.addPair(action, parent);
        setDepth(parent.getDepth() + 1);
    }

    /** Returns the inverse transition leading to parents of this state. */
    public BinaryRelation<HAction<Action>, Compostate<State, Action>> getParents() {
        return parents;
    }

    public Set<Compostate<State, Action>> getParentsOfStatus(Status st){
        Set<Compostate<State, Action>> result = new HashSet<>();
        for (Pair<HAction<Action>, Compostate<State, Action>> ancestorActionAndState : parents) {
            Compostate<State, Action> ancestor = ancestorActionAndState.getSecond();
            if(ancestor.isStatus(st)) result.add(ancestor);
        }
        return result;
    }

    /** Compares two composed states by their estimated distance to a goal by (<=). */
    @Override
    public int compareTo(Compostate o) {
        int result = estimate.compareTo(o.estimate);
        if (result == 0)
            result = this.depth - o.depth;
        return result;
    }


    /** Clears the internal state removing parent and exploredChildren. */
    public void clear() {
        exploredChildren.clear();
        /** Indicates whether the procedure consider a non-blocking requirement (by default we consider a stronger goal). */
        boolean nonblocking = false;
        if (!nonblocking) // this is a quick fix to allow reopening weak states marked as errors
            directedControllerSynthesisBlocking.compostates.remove(states);
    }


    /** Returns the string representation of a composed state. */
    @Override
    public String toString() {
        return states.toString();
    }

}
