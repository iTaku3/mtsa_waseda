package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

/** This class represents an Heuristic State, that is a state in the
 *  abstraction that is used to estimate the distance to the goal. */
class HState<State, Action> {

    /** The id of the LTS to which this state belongs. */
    public final int lts;

    /** The original state this represents in the abstraction. */
    public final State state;

    /** Precomputed hash code. */
    private final int hash;

    /** Flags if this state represents a marked state. */
    public final boolean marked;

    private final List<LTS<State, Action>> ltss;


    /** Constructor for an Heuristic State. */
    public HState(int lts, State state, int hash, boolean marked, List<LTS<State,Action>> ltss) {
        this.lts = lts;
        this.state = state;
        this.hash = hash;
        this.marked = marked;
        this.ltss = ltss;
    }


    /** Returns the outgoing transitions from this state in its own LTS. */
    public BinaryRelation<Action, State> getTransitions() {
        return ltss.get(lts).getTransitions(state); // accessing LTSs
    }


    ///** Returns whether this state represents a marked state. */
    //public boolean isMarked() { // removed during aggressive inlining
    //  return marked;
    //}


    /** Returns if this state has a transition with the given action. */
    public boolean contains(HAction<State, Action> action) {
        Set<State> targets = getTransitions().getImage(action.getAction());
        return !(targets == null || targets.isEmpty());
    }


    /** Returns if this state has a self-loop transition with the given action. */
    public boolean isSelfLoop(HAction<State, Action> action) {
        return getTransitions().getImage(action.getAction()).contains(this.state);
    }


    /** The hash code for this state. */
    @Override
    public int hashCode() {
        return hash;
    }


    /** Returns whether this state is equals to a given object. */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof HState) {
            @SuppressWarnings("unchecked")
            HState<State, Action> other = (HState<State, Action>)obj;
            result = this.lts == other.lts &&
                    this.state.equals(other.state);
        }
        return result;
    }


    /** Returns the string representation of this state. */
    @Override
    public String toString() {
        return state.toString();
    }

}