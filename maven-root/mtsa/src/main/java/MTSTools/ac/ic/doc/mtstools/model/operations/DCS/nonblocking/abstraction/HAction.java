package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.Alphabet;

/** This class serves as a proxy for the Action type argument.
 *  Internally it is represented simply by its hash value.
 *  Together with the alphabet allows for a high-performance
 *  representation of transitions. */
public class HAction<State, Action> implements Comparable<HAction<State, Action>> {

    private final Alphabet<State, Action> alphabet;

    /** Hash value for this action. */
    public final int hash;


    /** Constructor for an HAction given a common Action. */
    public HAction(Alphabet<State, Action> alphabet, Action action) {
        this.alphabet = alphabet;
        hash = this.alphabet.actions.size();
        this.alphabet.actions.add(action);
        this.alphabet.hactions.add(this);
        if (this.alphabet.controllable.isEmpty() || this.alphabet.controllable.contains(action))
            this.alphabet.controlbits.set(hash);
    }


    /** Returns true if two HActions are equals. */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj != null && obj instanceof HAction) {
            @SuppressWarnings("unchecked")
            HAction<State, Action> other = (HAction<State, Action>)obj;
            result = this.hash == other.hash;
        }
        return result;
    }


    /** Returns the hash code for this HAction. */
    @Override
    public int hashCode() {
        return hash;
    }


    /** Indicates whether this action is controllable. */
    public boolean isControllable() {
        return this.alphabet.controlbits.get(hash);
    }


    /** Returns the Action this HAction proxies. */
    public Action getAction() {
        return this.alphabet.actions.get(hash);
    }

    /** Return the String representation of this HAction. */
    @Override
    public String toString() {
        return getAction().toString();
    }


    /** Compares two actions. */
    @Override
    public int compareTo(HAction<State, Action> o) {
        return hash - o.hash;
    }

}