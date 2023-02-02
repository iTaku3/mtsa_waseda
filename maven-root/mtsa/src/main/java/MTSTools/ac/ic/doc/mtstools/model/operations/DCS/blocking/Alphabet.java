package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking;

import MTSTools.ac.ic.doc.mtstools.model.LTS;

import java.util.*;

/**
 * This class encapsulates the alphabet of the plant.
 * Provides the required functionality for actions with perfect hashing.
 *
 * @see HAction
 */
public class Alphabet<Action> implements Iterable<HAction<Action>> {

    /**
     * Actions indexed by hash.
     */
    public List<Action> actions;

    /**
     * HActions indexed by hash.
     */
    public final List<HAction<Action>> hactions;

    /**
     * Map of actions to their respective hash codes.
     */
    private final Map<Action, HAction<Action>> codes;

    /**
     * Controllable action flags indexed by hash.
     */
    public BitSet controlbits;


    /**
     * Constructor for the Alphabet.
     */
    public <State> Alphabet(DirectedControllerSynthesisBlocking<State, Action> directedControllerSynthesisBlocking, List<LTS<State, Action>> ltss) {
        actions = new ArrayList<>();
        hactions = new ArrayList<>();
        codes = new HashMap<>();
        controlbits = new BitSet();
        Set<Action> actions1 = new HashSet<>();
        for (LTS<?, Action> actionLTS : ltss) {
            actions1.addAll(actionLTS.getActions());
        }
        List<Action> labels = new ArrayList<>(actions1.size());
        labels.addAll(actions1);
        labels.sort(Comparator.comparing(Object::toString));
        for (Action label : labels) {
            HAction<Action> code = codes.get(label);
            if (code == null) {
                HAction<Action> value = new HAction<>(directedControllerSynthesisBlocking, this, label);
                codes.put(label, value);
            }
        }
    }


    /**
     * Returns the size of the alphabet (the number of actions).
     */
    public int size() {
        return actions.size();
    }


    /**
     * Provides an iterator for the actions in the alphabet.
     */
    @Override
    public Iterator<HAction<Action>> iterator() {
        return codes.values().iterator();
    }


    /**
     * Given an action returns its perfectly hashed proxy.
     */
    public HAction<Action> getHAction(Action action) {
        return codes.get(action);
    }


    /**
     * Returns a collection of all the actions that conform the alphabet.
     */
    public List<Action> getActions() {
        return actions;
    }


    /**
     * Returns the list of HActions indexed by hash.
     */
    public List<HAction<Action>> getHActions() {
        return hactions;
    }

}
