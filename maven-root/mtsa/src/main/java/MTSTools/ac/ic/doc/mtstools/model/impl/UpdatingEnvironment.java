package MTSTools.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.MapSetBinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.TransitionSystem;
import ltsa.control.util.ControllerUtils;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.util.*;

/**
 * Created by lnahabedian on 31/08/16.
 */
public class UpdatingEnvironment implements TransitionSystem<Long,String> {

    private Set<Long> states;
    private Set<String> actions;
    private Map<Long, BinaryRelation<String, Long>> transitions;
    private Long initialState;
    private Long lastState;

    public UpdatingEnvironment(MTS<Long, String> automaton) {

        initialState = automaton.getInitialState();
        states = new HashSet<Long>(automaton.getStates());
        actions = new HashSet<>(automaton.getActions());
        transitions = automaton.getTransitions(MTS.TransitionType.REQUIRED);
        lastState = new Long(Collections.max(states));
    }

    @Override
    public Set<Long> getStates() {
        return states;
    }

    @Override
    public Set<String> getActions() {
        return actions;
    }

    @Override
    public Long getInitialState() {
        return initialState;
    }

    @Override
    public boolean addState(Long state) {
        if (states.add(state)) {
            this.transitions.put(state, this.newRelation());
            return true;
        }
        return false;
    }

    protected BinaryRelation<String, Long> newRelation() {
        return new MapSetBinaryRelation<String, Long>();
    }

    @Override
    public boolean addStates(Collection<? extends Long> states){
        if (this.states.addAll(states)){
            for (Long state : states){
                this.transitions.put(state,this.newRelation());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean addAction(String action) {
        return actions.add(action);
    }

    @Override
    public boolean addActions(Collection<? extends String> actions) {
        return this.actions.addAll(actions);
    }

    @Override
    public void removeAction(String action) {
        actions.remove(action);
    }

    @Override
    public boolean removeUnreachableStates() {

        Long state = getInitialState();
        Collection<Long> reachableStates = getReachableStatesBy(state);
        Collection<Long> unreachable = CollectionUtils.subtract(getStates(), reachableStates);
        this.removeStates(unreachable);
        return !unreachable.isEmpty();

    }

    protected Collection<Long> getReachableStatesBy(Long state) {
        Collection<Long> reachableStates = new HashSet<Long>((int) (this.getStates().size() / .75f + 1), 0.75f);
        Queue<Long> toProcess = new LinkedList<Long>();
        toProcess.offer(state);
        reachableStates.add(state);
        while (!toProcess.isEmpty()) {
            for (Pair<String, Long> transition : getTransitionsFrom(toProcess.poll())) {
                if (!reachableStates.contains(transition.getSecond())) {
                    toProcess.offer(transition.getSecond());
                    reachableStates.add(transition.getSecond());
                }
            }
        }
        return reachableStates;
    }

    public BinaryRelation<String, Long> getTransitionsFrom(Long state) {
        return transitions.get(state);
    }

    @Override
    public void setInitialState(Long state) {
        initialState = state;
    }

    protected void removeStates(Collection<Long> toRemove) {
        this.removeTransitions(toRemove);
        states = new HashSet<Long>(CollectionUtils.subtract(getStates(), toRemove));    }

    protected void removeTransitions(Collection<Long> toRemove) {
        transitions.keySet().removeAll(toRemove);
        for (BinaryRelation<String, Long> rel : transitions.values()) {
            for (Iterator<Pair<String, Long>> iter = rel.iterator(); iter.hasNext(); ) {
                Long state = iter.next().getSecond();
                if (toRemove.contains(state)) {
                    iter.remove();
                }
            }
        }
    }

    public boolean addTransition(Long from, String label, Long to) {
        this.validateNewTransition(from, label, to);
        boolean added = this.getTransitionsFrom(from).addPair(label, to);
        return added;
    }

    protected void validateNewTransition(Long from, String label, Long to) {
        Validate.isTrue(this.actions.contains(label), "Action: " + label + " is not in the alphabet");
        Validate.isTrue(this.states.contains(from), "State: " + from + " doesn't exists.");
        Validate.isTrue(this.states.contains(to), "State: " + to + " doesn't exists.");

    }

    public Map<Long,BinaryRelation<String,Long>> getTransitions() {
        return transitions;
    }

    public UpdatingEnvironment copy() {

        MTS<Long, String> automata = ControllerUtils.UpdateEnvironment2MTS(this);
        return new UpdatingEnvironment(automata);

    }

    public Long newState() {
        this.addState(new Long(++lastState));
        return lastState;
    }
}
