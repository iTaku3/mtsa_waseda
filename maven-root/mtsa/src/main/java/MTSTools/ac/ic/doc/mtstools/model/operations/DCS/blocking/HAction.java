package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking;

/**
 * This class serves as a proxy for the Action type argument.
 * Internally it is represented simply by its hash value.
 * Together with the alphabet allows for a high-performance
 * representation of transitions.
 */
public class HAction<Action> implements Comparable<HAction<Action>> {

    private final DirectedControllerSynthesisBlocking<?, Action> directedControllerSynthesisBlocking;
    /**
     * Hash value for this action.
     */
    public final int hash;


    /**
     * Constructor for an HAction given a common Action.
     */
    public HAction(DirectedControllerSynthesisBlocking<?, Action> directedControllerSynthesisBlocking, Alphabet<Action> alphabet, Action action) {
        this.directedControllerSynthesisBlocking = directedControllerSynthesisBlocking;
        hash = alphabet.actions.size();
        alphabet.actions.add(action);
        alphabet.hactions.add(this);
        if (directedControllerSynthesisBlocking.controllable.isEmpty() || directedControllerSynthesisBlocking.controllable.contains(action))
            alphabet.controlbits.set(hash);
    }


    /**
     * Returns true if two HActions are equals.
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof HAction) {
            @SuppressWarnings("unchecked")
            HAction other = (HAction) obj;
            result = this.hash == other.hash;
        }
        return result;
    }


    /**
     * Returns the hash code for this HAction.
     */
    @Override
    public int hashCode() {
        return hash;
    }


    /**
     * Indicates whether this action is controllable.
     */
    public boolean isControllable() {
        return directedControllerSynthesisBlocking.alphabet.controlbits.get(hash);
    }


    /**
     * Returns the Action this HAction proxies.
     */
    public Action getAction() {
        return directedControllerSynthesisBlocking.alphabet.actions.get(hash);
    }

    /**
     * Return the String representation of this HAction.
     */
    @Override
    public String toString() {
        return getAction().toString();
    }


    /**
     * Compares two actions.
     */
    @Override
    public int compareTo(HAction o) {
        return hash - o.hash;
    }

}
