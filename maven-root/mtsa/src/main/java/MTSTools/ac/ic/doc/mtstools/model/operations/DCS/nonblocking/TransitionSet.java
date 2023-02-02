package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.HAction;

/** This class represents a set of synchronized transitions, that is to say, that
 *  a transition belongs to the set if all intervening LTSs allow the action. */
public class TransitionSet<State, Action> implements Cloneable {

    private final List<LTS<State, Action>> ltss;

    /** Maps actions to the number of LTSs enabling them. */
    private int[] enabledCount;

    /** The set of enabled transitions by each LTS. */
    private List<BitSet> enabledByLTS;

    /** The set of enabled transitions. */
    private Set<HAction<State, Action>> enabledActions;


    /** Constructor for a Transition Set (avoids initialization if alphabet is null). */
    public TransitionSet(List<LTS<State,Action>> ltss, Alphabet<State, Action> alphabet) {
        this.ltss = ltss;
        if (alphabet != null)
            init(alphabet);
    }


    /** Initialization for a Transition Set, marks as enabled for each LTS
     *  all the actions that do not belong to its alphabet. */
    private void init(Alphabet<State, Action> alphabet) {
        enabledByLTS = new ArrayList<>(this.ltss.size());
        enabledCount = new int[alphabet.size()];
        enabledActions = new HashSet<>();
        for (int i = 0; i < this.ltss.size(); ++i)
            enabledByLTS.add(new BitSet(alphabet.size()));
        for (HAction<State, Action> hAction : alphabet) {
            Action action = hAction.getAction();
            int hash = hAction.hashCode();
            int count = 0;
            for (int i = 0; i < this.ltss.size(); ++i) {
                Set<Action> actionsOf_i = this.ltss.get(i).getActions();
                if (!actionsOf_i.contains(action)) {
                    enabledByLTS.get(i).set(hash);
                    count++;
                }
            }
            enabledCount[hash] = count;
            if (count == this.ltss.size())
                enabledActions.add(hAction);
        }
    }


    /** Adds an action enabled by the given LTS.
     *  Returns true if the action was not enabled and it became enabled
     *  as a consequence of this addition. */
    public boolean add(int lts, HAction<State, Action> action) {
        boolean result = false;
        int hash = action.hashCode();
        if (!enabledByLTS.get(lts).get(hash)) {
            enabledByLTS.get(lts).set(hash);
            int count = ++enabledCount[hash];
            result = count == this.ltss.size();
        }
        if (result)
            enabledActions.add(action);
        return result;
    }


    /** Removes an action enabled by the given LTS.
     *  Returns true if the action was enabled and it became disabled
     *  as a consequence of this removal. */
    public boolean remove(int lts, HAction<State, Action> action) {
        boolean result = false;
        int hash = action.hashCode();
        if (enabledByLTS.get(lts).get(hash)) {
            enabledByLTS.get(lts).clear(hash);
            int count = enabledCount[hash]--;
            result = count == this.ltss.size();
        }
        if (result)
            enabledActions.remove(action);
        return result;
    }


    /** Returns the set of enabled actions of this Transition Set. */
    public Set<HAction<State, Action>> getEnabled() {
        return enabledActions;
    }

    /** Returns whether an action belongs to this Transition Set.
     *  That is, if all intervening LTSs allow the action. */
    public boolean contains(HAction<State, Action> action) {
        return enabledCount[action.hash/*hashCode()*/] == this.ltss.size(); // replaced during aggressive inlining
    }


    /** Returns a new instance of a Transition Set by deep copying this. */
    @Override
    public TransitionSet<State, Action> clone() {
        TransitionSet<State, Action> result = new TransitionSet<State, Action>(this.ltss, null);
        result.enabledByLTS = new ArrayList<>(this.enabledByLTS.size());
        result.enabledActions = new HashSet<>(this.enabledActions);
        result.enabledCount = new int[this.enabledCount.length];
        System.arraycopy(this.enabledCount, 0, result.enabledCount, 0, this.enabledCount.length);
        for (int i = 0; i < this.enabledByLTS.size(); ++i) {
            BitSet enabledBy_i = new BitSet();
            enabledBy_i.or(enabledByLTS.get(i));
            result.enabledByLTS.add(enabledBy_i);
        }
        return result;
    }


    /** Overrides the internal representation of this Transition Set to
     *  reflect that of another set. */
    public void copy(TransitionSet<State, Action> other) {
        enabledActions.clear();
        enabledActions.addAll(other.enabledActions);
        System.arraycopy(other.enabledCount, 0, this.enabledCount, 0, this.enabledCount.length);
        for (int i = 0; i < this.enabledByLTS.size(); ++i) {
            BitSet enabledBy_i = this.enabledByLTS.get(i);
            enabledBy_i.clear();
            enabledBy_i.or(other.enabledByLTS.get(i));
        }
    }


    /** Returns a string representation of this set. */
    @Override
    public String toString() {
        return enabledActions.toString();
    }

}