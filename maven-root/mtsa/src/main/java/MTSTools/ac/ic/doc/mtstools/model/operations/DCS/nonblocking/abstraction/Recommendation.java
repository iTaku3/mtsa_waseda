package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.Compostate;

/** This class represents a recommended course of action computed by the
 *  heuristic procedure. */
public class Recommendation<State, Action> implements Comparable<Recommendation<State, Action>> {

    /** The action recommended to explore. */
    private HAction<State, Action> action;

    /** The estimated distance to the goal. */
    private HEstimate<State, Action> estimate;

    /** Constructor for a recommendation. */
    public Recommendation(Compostate<State, Action> compostate, HAction<State, Action> action, HEstimate<State, Action> estimate) {
        this.action = action;
        this.estimate = estimate;
    }


    /** Returns the action this recommendation suggests. */
    public HAction<State, Action> getAction() {
        return action;
    }


    /** Returns the estimate distance to the goal for this recommendation. */
    public HEstimate<State, Action> getEstimate() {
        return estimate;
    }


    /** Compares two recommendations by (<=). */
    @Override
    public int compareTo(Recommendation<State, Action> o) {
        int result = estimate.compareTo(o.estimate);
        return result;
    }


    /** Returns the string representation of this recommendation. */
    @Override
    public String toString() {
        return action.toString() + estimate;
    }

}