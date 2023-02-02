package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.abstraction;

import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.abstraction.Recommendation;

import java.util.Comparator;

/**
 * This class provides a Recommendation comparison that allows to create a
 * ranking of events. That is, a sorted list where first we have uncontrollable
 * events in descending order followed by controllable events in ascending order.
 */
public class Ranker<Action> implements Comparator<Recommendation<Action>> {

    /**
     * Compares two recommendations for ranking purposes.
     * Returns:
     * -1 if r1 is smaller than r2;
     * 0 if r1 and r2 are equal; and
     * 1 if r1 is greater than r2.
     */
    @Override
    public int compare(Recommendation<Action> r1, Recommendation<Action> r2) {
        int c1 = r1.getAction().isControllable() ? 1 : 0;
        int c2 = r2.getAction().isControllable() ? 1 : 0;
        int result = c1 - c2;
        if (result == 0)
            result = c1 == 1 ? r1.compareTo(r2) : r2.compareTo(r1);
        return result;
    }

}
