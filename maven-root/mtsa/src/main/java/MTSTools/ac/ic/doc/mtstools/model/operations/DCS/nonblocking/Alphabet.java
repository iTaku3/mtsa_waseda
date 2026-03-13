package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.HAction;

/** This class encapsulates the alphabet of the plant.
 *  Provides the required functionality for actions with perfect hashing.
 *  @see HAction*/
public class Alphabet<State, Action> implements Iterable<HAction<State, Action>> {

    private final List<LTS<State, Action>> ltss;

    /** Actions indexed by hash. */
    public List<Action> actions;

    /** HActions indexed by hash. */
    public List<HAction<State, Action>> hactions;

    /** Map of actions to their respective hash codes. */
    private Map<Action, HAction<State, Action>> codes;

    /** Controllable action flags indexed by hash. */
    public BitSet controlbits;

    /** Set of controllable actions. */
    public final Set<Action> controllable;

    /** Constructor for the Alphabet.
     * @param directedControllerSynthesisNonBlocking TODO*/
    public Alphabet(List<LTS<State, Action>> ltss, Set<Action> controllable) {
        actions = new ArrayList<>();
        hactions = new ArrayList<>();
        codes = new HashMap<>();
        controlbits = new BitSet();
        this.ltss = ltss;
        this.controllable = controllable;
        init();
    }


    /** Initialization. Populates the alphabet with the transitions from the LTSs. */
    private void init() {
        Set<Action> actions = new HashSet<>();
        for (int i = 0; i < this.ltss.size(); ++i)
            actions.addAll(this.ltss.get(i).getActions());
        List<Action> labels = new ArrayList<>(actions.size());
        for (Action action : actions)
            labels.add(action);
        Collections.sort(labels, new Comparator<Action>() {
            public int compare(Action o1, Action o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        for (Action label : labels) {
            HAction<State, Action> code = codes.get(label);
            if (code == null)
                codes.put(label, code = new HAction<State, Action>(this, label));
        }
    }


    /** Returns the size of the alphabet (the number of actions). */
    public int size() {
        return actions.size();
    }


    /** Provides an iterator for the actions in the alphabet. */
    @Override
    public Iterator<HAction<State, Action>> iterator() {
        return codes.values().iterator();
    }


    /** Given an action returns its perfectly hashed proxy. */
    public HAction<State, Action> getHAction(Action action) {
        return codes.get(action);
    }


    /** Returns a collection of all the actions that conform the alphabet. */
    public List<Action> getActions() {
        return actions;
    }


    /** Returns the list of HActions indexed by hash. */
    public List<HAction<State, Action>> getHActions() {
        return hactions;
    }

}