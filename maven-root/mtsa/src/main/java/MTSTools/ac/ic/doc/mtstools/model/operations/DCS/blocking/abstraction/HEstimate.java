package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.abstraction;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.reverse;
import static java.util.Collections.sort;

/**
 * This class represents an heuristic estimate of the distance from a state to a goal.
 */
public class HEstimate implements Comparable<HEstimate>, Cloneable {

    /**
     * List of distances considered in this heuristic estimate.
     */
    public final List<HDist> values;

    /**
     * Constructor for an HEstimate.
     */
    public HEstimate(int capacity) {
        values = new ArrayList<>(capacity);
    }

    /**
     * Constructor for an initialized HEstimate.
     */
    public HEstimate(int size, HDist init) {
        this(size);
        for (int i = 0; i < size; ++i)
            values.add(init);
    }

    /**
     * Sorts this estimate in descending order.
     */
    public void sortDescending() {
        sort(values);
        reverse(values);
    }

    /**
     * Compares to estimates using a lexicographical comparison.
     */
    @Override
    public int compareTo(HEstimate o) {
        int result = 0;
        for (int i = 0; result == 0 && i < values.size(); ++i)
            result = get(i).compareTo(o.get(i));
        return result;
    }

    /**
     * Returns the distance at the i-th index.
     */
    public HDist get(int i) {
        return values.get(i);
    }

    /**
     * Sets the distance for the i-th index.
     */
    public boolean set(int i, HDist d) {
        boolean result;
        if (result = (get(i).compareTo(d) > 0))
            values.set(i, d);
        return result;
    }

    /**
     * Reduces this estimate to its maximum value.
     */
    public void reduceMax() {
        HDist max = get(0);
        for (int i = 1; i < values.size(); ++i) {
            if (get(i).compareTo(max) > 0)
                max = get(i);
        }
        values.clear();
        values.add(max);
    }

    /**
     * Indicates whether this estimate represents a conflict.
     */
    public boolean isConflict() {
        return get(0).getSecond().equals(HDist.INF);
    }

    /**
     * Returns whether two HEstimates are equals.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof HEstimate)
            result = this.compareTo((HEstimate) obj) == 0;
        return result;
    }

    /**
     * Returns the hash code for an HEstimate.
     */
    @Override
    public int hashCode() {
        return values.hashCode();
    }

    /**
     * Returns a copy of this object.
     */
    @Override
    public Object clone() {
        HEstimate result = new HEstimate(values.size());
        result.copy(this);
        return result;
    }

    /**
     * Copies the values of an estimate into this estimate.
     */
    public void copy(HEstimate e) {
        values.clear();
        values.addAll(e.values);
    }

    /**
     * Returns the string representation of this estimate.
     */
    @Override
    public String toString() {
        return values.toString();
    }

}
