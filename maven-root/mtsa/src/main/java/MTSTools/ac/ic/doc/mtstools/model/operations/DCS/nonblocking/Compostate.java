package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking;

import static java.util.Collections.emptySet;
import static java.util.Collections.sort;
import static org.junit.Assert.assertTrue;

import java.util.*;

import MTSTools.ac.ic.doc.commons.collections.BidirectionalMap;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelationImpl;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.HAction;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.HEstimate;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.Recommendation;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.Ranker;

/** This class represents a state in the parallel composition of the LTSs
 *  in the environment. These states are used to build the fragment of the
 *  environment required to reach the goal on-the-fly. The controller for
 *  the given problem can then be extracted directly from the partial
 *  construction achieved by using these states. */
public class Compostate<State, Action> {

    /**
     *
     */
    private final DirectedControllerSynthesisNonBlocking<State, Action> dcs;

    /** States by each LTS in the environment that conform this state. */
    private final List<State> states; // Note: should be a set of lists for non-deterministic LTSs

    /** Indicates whether this state is a goal (1) or an error (-1) or not yet known (0). */
    public Status status;

    /** The real distance to the goal state from this state. */
    private int distance;

    /** Depth at which this state has been expanded. */
    private int depth;
   
    /** An identifier used by CompleteFrontierExplorationHeuristic to know how old the values of the estimates are, and whether they need to be recomputed */
    public Integer seq = -1;

    // These 3 fields are used by the Ready Abstraction as a cache, to avoid recomputing the graph every time
    /** Set of vertices in the RA graph. */
    public Set<HAction<State, Action>> vertices;
    /** Edges in the RA graph. */
    public BidirectionalMap<HAction<State, Action>, HAction<State, Action>> edges;
    public Map<HAction<State, Action>, Set<Integer>> readyInLTS;

    /** Indicates whether the state is marked, that is, every contained state is marked. */
    final boolean marked;

    /** Children states expanded following a recommendation of this state. */
    private final BinaryRelation<HAction<State, Action>, Compostate<State, Action>> exploredChildren;

    /** Children states expanded through uncontrollable transitions. */
    private final Set<Compostate<State, Action>> childrenExploredThroughUncontrollable;

    /** Children states expanded through controllable transitions. */
    private final Set<Compostate<State, Action>> childrenExploredThroughControllable;

    /** Parents that expanded into this state. */
    private final BinaryRelation<HAction<State, Action>, Compostate<State, Action>> parents;

    /** Set of actions enabled from this state. */
    public final Set<HAction<State, Action>> transitions;

    /** Stores state distance to a marked state in a winning loop and child of this best path*/
    private Pair<Integer, Compostate<State, Action>> bestControllableChild;

    /** todo esto es para poder guardarme las transiciones a usar en setGoal sin tener pares state/action en gather */
    private HAction<State, Action> potentiallyGoodTransition;

    private boolean hasGoalChild = false;

    /** if the state has a goal child, this action points to a goal child */
    public HAction<State,Action> actionToGoal;

    /** Indicates whether this state was expanded by DCS */
    private boolean wasExpanded = false;

    // Variables for OpenSetExplorationHeuristic --------------------

    /** The estimated distance to the goal from this state. */
    private HEstimate<State, Action> estimate;

    /** A ranking of the outgoing transitions from this state. */
    public List<Recommendation<State, Action>> recommendations;

    /** An iterator for the ranking of recommendations. */
    private Iterator<Recommendation<State, Action>> recommendit;

    /** Current recommendation (or null). */
    Recommendation<State, Action> recommendation;

    /** Indicates whether the state is actively being used. */
    public boolean live;

    /** Indicates whether the state is in the open queue. */
    boolean inOpen;

    /** Stores target states (i.e., already visited marked states) to reach from this state. */
    public List<Set<State>> targets;

    /** Indicates whether the state is controlled or not. */
    public boolean controlled;

    /** Indicates that the state has a uncontrollable conflincting action */
    public boolean heuristicStronglySuggestsIsError = false;

    // Variables for FeatureBasedExplorationHeuristic --------------------

    /** Number of uncontrollable transitions */
    public int uncontrollableTransitions;

    /** Set of transitions that were not yet expanded by DCS */
    public int unexploredTransitions;

    /** Number of uncontrollable transitions that were not yet expanded by DCS */
    int uncontrollableUnexploredTransitions;

    /** Estimates of transitions, only used when using ra feature **/
    HashMap<HAction<State, Action>, HEstimate<State, Action>> estimates;
    public final Integer uncontrollablesCount;

    Action actionnoname;//actionの名前を入れる
    Action actionnoname2;
    Action actionnoname3;
    Action actionnoname4;
    Action actionnoname5;
    Action actionnoname6;
    List<String> complist;
    int coun=0;


    /** Constructor for a Composed State. */
    public Compostate(DirectedControllerSynthesisNonBlocking<State, Action> directedControllerSynthesisNonBlocking, List<State> states) {
        this.dcs = directedControllerSynthesisNonBlocking;
        this.states = states;
        this.status = Status.NONE;
        this.distance = DirectedControllerSynthesisNonBlocking.INF;
        this.depth = DirectedControllerSynthesisNonBlocking.INF;
        this.exploredChildren = new BinaryRelationImpl<>();
        this.childrenExploredThroughUncontrollable = new HashSet<>();
        this.childrenExploredThroughControllable = new HashSet<>();
        this.parents = new BinaryRelationImpl<>();
        this.bestControllableChild = new Pair<>(-1,null);
        boolean marked = true;
        for (int lts = 0; marked && lts < this.dcs.ltssSize; ++lts)
            marked = this.dcs.defaultTargets.get(lts).contains(states.get(lts));
        this.marked = marked;
        this.transitions = buildTransitions();
        dcs.heuristic.initialize(this);
        this.uncontrollablesCount = countUncontrollables();
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

    /** Returns this state's status. */
    public Status getStatus() {
        return status;
    }

    /** Sets this state's status. */
    public void setStatus(Status status) {
        // System.err.println(this + " status was: " + this.status + " now is: " + status);
        if (this.status != Status.ERROR || status == Status.ERROR)
            this.status = status;
    }

    /** Indicates whether this state's status equals some other status. */
    public boolean isStatus(Status status) {
        return this.status == status;
    }

    public boolean hasGoalChild(){
        return hasGoalChild;
    }

    public void setHasGoalChild(HAction<State, Action> actionToGoal) {
        this.actionToGoal = actionToGoal;
        this.hasGoalChild = true;
    }

    /** Returns whether this state has a child with the given status. */
    public boolean hasStatusChild(Status status) {
        for (Pair<HAction<State, Action>,Compostate<State, Action>> transition : getExploredChildren())
            if (transition.getSecond().isStatus(status)) return true;
        return false;
    }

    /** Returns the set of actions enabled from this composed state. */
    public Set<HAction<State, Action>> getTransitions() {
        return transitions;
    }

    /** Initializes the set of actions enabled from this composed state. */
    private Set<HAction<State, Action>> buildTransitions() { // Note: here I can code the wia and ia behavior for non-deterministic ltss
        if (dcs.facilitators == null) {
            for (int i = 0; i < states.size(); ++i) {
                for (Pair<Action,State> transition : dcs.ltss.get(i).getTransitions(states.get(i))) {
                    HAction<State, Action> action = dcs.alphabet.getHAction(transition.getFirst());
                    dcs.allowed.add(i, action);
                }
            }
        } else {
            for (int i = 0; i < states.size(); ++i)
                if (!dcs.facilitators.get(i).equals(states.get(i)))
                    for (Pair<Action,State> transition : dcs.ltss.get(i).getTransitions(dcs.facilitators.get(i))) {
                        HAction<State, Action> action = dcs.alphabet.getHAction(transition.getFirst());
                        dcs.allowed.remove(i, action); // remove old non-shared facilitators transitions
                    }
            for (int i = 0; i < states.size(); ++i)
                if (!dcs.facilitators.get(i).equals(states.get(i)))
                    for (Pair<Action,State> transition : dcs.ltss.get(i).getTransitions(states.get(i))) {
                        HAction<State, Action> action = dcs.alphabet.getHAction(transition.getFirst());
                        dcs.allowed.add(i, action); // add new non-shared facilitators transitions
                    }
        }
        Set<HAction<State, Action>> result = new HashSet<>(dcs.allowed.getEnabled());
        dcs.facilitators = states;
        return result;
    }

    /** Adds an expanded child to this state. */
    public void addChild(HAction<State, Action> action, Compostate<State, Action> child) {
        if(action.isControllable()){
            childrenExploredThroughControllable.add(child);
        } else {
            childrenExploredThroughUncontrollable.add(child);
        }

        exploredChildren.addPair(action, child);
    }

    /** Returns all transition leading to exploredChildren of this state. */
    public BinaryRelation<HAction<State, Action>,Compostate<State, Action>> getExploredChildren() {
        return exploredChildren;
    }

    public Set<Compostate<State, Action>> getChildrenExploredThroughUncontrollable() {
        return childrenExploredThroughUncontrollable;
    }

    public Set<Compostate<State, Action>> getChildrenExploredThroughControllable() {
        return childrenExploredThroughControllable;
    }

    /** Returns the distance to the goal of a child of this compostate following a given action. */
    public int getChildDistance(HAction<State, Action> action) {
        int result = DirectedControllerSynthesisNonBlocking.UNDEF; // exploredChildren should never be empty or null
        for (Compostate<State, Action> compostate : exploredChildren.getImage(action)) { // Note: maximum of non-deterministic exploredChildren
            if (result < compostate.getDistance())
                result = compostate.getDistance();
        }
        return result;
    }

    /** Adds an expanded parent to this state. */
    public void addParent(HAction<State, Action> action, Compostate<State, Action> parent) {
        parents.addPair(action, parent);
        setDepth(parent.getDepth() + 1);
    }

    //fixme: habría que usar esta función en algún lado?
    /** Removes a closed parent from this state. */
    public void removeParent(HAction<State, Action> action, Compostate<State, Action> parent) {
        parents.removePair(action, parent);
    }

    /** Returns the inverse transition leading to parents of this state. */
    public BinaryRelation<HAction<State, Action>,Compostate<State, Action>> getParents() {
        return parents;
    }

    //fixme: creo que esta función se puede borrar
    /** Clears the internal state removing parent and exploredChildren. */
    public void clear() {
        exploredChildren.clear();
        if (!dcs.nonblocking) // this is a quick fix to allow reopening weak states marked as errors
            dcs.compostates.remove(states);
    }

    /** Returns the string representation of a composed state. */
    @Override
    public String toString() {
        return states.toString();
    }

    public HAction<State, Action> getPotentiallyGoodTransition() {
        return potentiallyGoodTransition;
    }

    public void setPotentiallyGoodTransition(HAction<State, Action> potentiallyGoodTransition) {
        this.potentiallyGoodTransition = potentiallyGoodTransition;
    }

    public void setBestControllable(Integer i, Compostate<State, Action> c) {
        assertTrue("set best controllable to non goal state", this.isStatus(Status.GOAL));
        bestControllableChild = new Pair<>(i,c);
    }

    public Pair<Integer, Compostate<State, Action>> getBestControllable() {
        return bestControllableChild;
    }

    public boolean wasExpanded() {
        return wasExpanded;
    }

    public void setExpanded() {
        wasExpanded = true;
    }

    public HEstimate<State, Action> getEstimate(HAction<State, Action> action) {
        return estimates.get(action);
    }

    public void addEstimate(HAction<State, Action> action, HEstimate<State, Action> estimate) {
        estimates.put(action, estimate);
    }

    public HEstimate<State, Action> getEstimate() {
        return estimate;
    }

    /** Indicates whether this state has been evaluated, that is, if it has
     *  a valid ranking of recommendations. */
    public boolean isEvaluated() {
        return recommendations != null;
    }

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
            targets = new ArrayList<>(dcs.ltssSize);
            for (int lts = 0; lts < dcs.ltssSize; ++lts)
                targets.add(new HashSet<>());
        }
        for (int lts = 0; lts < dcs.ltssSize; ++lts)
            targets.get(lts).add(states.get(lts));
    }

    /** Sorts this state's recommendations in order to be iterated properly. */
    public Recommendation<State, Action> rankRecommendations() {
        Recommendation<State, Action> result = null;
        if (!recommendations.isEmpty()) {
            recommendations.sort(new Ranker<>());
            result = recommendations.get(0);
        }

        if(!recommendations.isEmpty()){
            //System.out.println(complist);
            for(int j=0; j<complist.size(); j=j+2){
                for(int i=0; i<recommendations.size(); i++){
                    String a = complist.get(j);
                    String b = recommendations.get(i).getAction().toString();
                    //System.out.println("a");
                    //System.out.println(a);
                    //System.out.print("b");
                    //System.out.println(b);
                    if(a.equals(b)){
                        //System.out.println("maru");
                        for(int k=i+1; k<recommendations.size(); k++){
                            String c = complist.get(j+1);
                            String d = recommendations.get(k).getAction().toString();
                            //System.out.println("c");
                            //System.out.println(c);
                            //System.out.print("d");
                            //System.out.println(d);
                            if(c.equals(d)){
                                //System.out.println("maru2");
                                //System.out.println(recommendations);

                                //Collections.swap(recommendations, i, k);
                                recommendations.add(i, recommendations.get(k));
                                recommendations.remove(k+1);
                                //coun = coun+1;
                                //System.out.println(coun);


                                //System.out.println("afterrecomendationlist");
                                //System.out.println(recommendations);
                            }

                        }
                    }
                }
                //System.out.println("789");
            }

        }

        return result;
    }

    /** Sets up the recommendation list. */
    public void setupRecommendations(List<String> a) {//リストの箱を作るだけ、中身はなし
        //System.out.println("setup");


        //actionnoname=a.get(0);
        //actionnoname2=a.get(1);
        complist = a;

        //System.out.println("complistcompo");
        //System.out.println(complist);
        //System.out.println(actionnoname2);

        if (recommendations == null)
            recommendations = new ArrayList<>();

    }

    /** Adds a new recommendation to this state and returns whether an
     *  error action has been introduced (no other recommendations should
     *  be added after an error is detected).
     *  Recommendations should not be added after they have been sorted and
     *  with an iterator in use. */
    public boolean addRecommendation(HAction<State, Action> action, HEstimate<State, Action> estimate) {
        boolean controllableAction = action.isControllable();
        controlled &= controllableAction; // may not work with lists of recommendations

        if(!estimate.isConflict())
            recommendations.add(new Recommendation<>(this, action, estimate));
        if(!controllableAction && estimate.isConflict()){
            this.heuristicStronglySuggestsIsError = true;
            return true;
        }
        return false;
    }

    /** Advances the iterator to the next recommendation. */
    public Recommendation<State, Action> nextRecommendation() {
        Recommendation<State, Action> result = recommendation;
        updateRecommendation();
        return result;
    }

    /** Peek the next recommendation, without advancing the iterator. */
    public Recommendation<State, Action> peekRecommendation() {
        return recommendation;
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

    /** Returns whether this state is controllable or not. */
    public boolean isControlled() {
        return controlled;
    }

    private Integer countUncontrollables() {
        Integer result = 0;
        for (HAction<State, Action> a : this.transitions) {
            if (!a.isControllable()) result++;
        }
        return result;
    }

    public Integer getControllablesExpandedCount() {
        return childrenExploredThroughControllable.size();
    }
        
}