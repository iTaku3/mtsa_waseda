package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.abstraction;

import MTSTools.ac.ic.doc.commons.collections.InitMap;
import MTSTools.ac.ic.doc.commons.relations.OrdPair;

/**
 * This class represents the distance from a state to a goal.
 */
class HDist extends OrdPair<Integer, Integer> {
    /** Constant used to represent an infinite distance to the goal. */
    public static final int INF = Integer.MAX_VALUE;

    /**
     * This value represents an infinite distance.
     */
    public static HDist chasm = new HDist(2, INF);

    /**
     * This value represents zero distance.
     */
    public static HDist zero = new HDist(0, 0);

    /**
     * Factory for infinite values.
     */
    public static InitMap.Factory<HDist> chasmFactory = () -> HDist.chasm;

    /**
     * Constructor for the HDist class.
     */
    public HDist(Integer first, Integer second) {
        super(first, second == null ? INF : second);
    }

    /**
     * This method returns a new HDist with a one step greater distance.
     */
    public HDist inc() {
        return second == INF ? this : new HDist(first, second + 1);
    }

}
