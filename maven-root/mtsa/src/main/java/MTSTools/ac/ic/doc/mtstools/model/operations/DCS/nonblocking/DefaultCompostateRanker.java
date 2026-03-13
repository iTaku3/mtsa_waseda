package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking;

import java.util.Comparator;

/**
 * Default ranker for Compostate. Compares their estimates lexicographically.
 */
public class DefaultCompostateRanker<State, Action> implements Comparator<Compostate<State, Action>>{

    @Override
    public int compare(Compostate<State, Action> o1, Compostate<State, Action> o2) {
        int result = o1.getEstimate().compareTo(o2.getEstimate());
        if (result == 0)
            result = o1.getDepth()- o2.getDepth();
        return result;
    }
}
