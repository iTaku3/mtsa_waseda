package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import MTSTools.ac.ic.doc.commons.collections.InitMap.Factory;
import MTSTools.ac.ic.doc.commons.relations.OrdPair;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.DirectedControllerSynthesisNonBlocking;

/** This class represents the distance from a state to a goal. */
public class HDist extends OrdPair<Integer, Integer> {

    /** This value represents an infinite distance. */
    public static HDist chasm = new HDist(2, DirectedControllerSynthesisNonBlocking.INF);

    /** This value represents zero distance. */
    public static HDist zero = new HDist(0, 0);

    /** Factory for infinite values. */
    public static Factory<HDist> chasmFactory = new Factory<HDist>() {
        public HDist newInstance() { return HDist.chasm; }
    };

    /** Constructor for the HDist class. */
    public HDist(Integer first, Integer second) {
        super(first, second == null ? DirectedControllerSynthesisNonBlocking.INF : second);
    }

    /** This method returns a new HDist with a one step greater distance. */
    public HDist inc() {
        return second == DirectedControllerSynthesisNonBlocking.INF ? this : new HDist(first, second + 1);
    }

}