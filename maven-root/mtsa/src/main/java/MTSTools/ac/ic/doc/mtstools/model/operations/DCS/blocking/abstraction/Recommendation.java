package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.abstraction;

import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.HAction;

/**
 * This class represents a recommended course of action computed by the
 * heuristic procedure.
 */
public class Recommendation<Action> implements Comparable<Recommendation<Action>> {

    /**
     * The action recommended to explore.
     */
    private final HAction<Action> action;

    /**
     * The estimated distance to the goal.
     */
    private final HEstimate estimate;

    /**
     * Constructor for a recommendation.
     */
    public Recommendation(HAction<Action> action, HEstimate estimate) {
        this.action = action;
        this.estimate = estimate;
    }


    /**
     * Returns the action this recommendation suggests.
     */
    public HAction<Action> getAction() {
        return action;
    }


    /**
     * Returns the estimate distance to the goal for this recommendation.
     */
    public HEstimate getEstimate() {
        return estimate;
    }


    /**
     * Compares two recommendations by (<=).
     */
    @Override
    public int compareTo(Recommendation<Action> o) {
        return estimate.compareTo(o.estimate);
    }


    /**
     * Returns the string representation of this recommendation.
     */
    @Override
    public String toString() {
        return action.toString() + estimate;
    }

}
