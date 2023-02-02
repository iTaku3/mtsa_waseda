package MTSSynthesis.controller.gr.time.model;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSImpl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ezecastellano on 13/05/16.
 */
public class DoubleLinkedLTS<State,Action> extends LTSImpl<State,Action>{
    Map<State, BinaryRelation<Action, State>> inverseTransitions;

    public DoubleLinkedLTS(State initial){
        super(initial);
    }

    @Override
    public boolean addState(State state) {
        if (super.addState(state)) {
            this.getInternalTransitions().put(state,this.newRelation());
            this.inverseTransitions.put(state,this.newRelation());
            return true;
        }
        return false;
    }

    public DoubleLinkedLTS(LTS<State,Action> lts){
        super(lts.getInitialState());
        Map<State, BinaryRelation<Action, State>> transitions = lts.getTransitions();
        for(State state: transitions.keySet()){
            this.addState(state);
        }
        for(State state: transitions.keySet()){
            for (Pair<Action, State> transition : transitions.get(state)) {
                this.addTransition(state,transition.getFirst(),transition.getSecond());
            }
        }
    }

    @Override
    public boolean addTransition(State from, Action label, State to) {
        this.validateNewTransition(from,label,to);
        return this.getTransitions(from).addPair(label, to) && this.inverseTransitions.get(to).addPair(label,from);
    }

    @Override
    public boolean removeTransition(State from, Action label, State to) {
        return super.removeTransition(from,label,to) && this.inverseTransitions.get(to).removePair(label, from);
    }

    public Set<State> getPredecessors(State state){
        Set<State> predecessors = new HashSet<State>();
        for (Pair<Action,State> transition: inverseTransitions.get(state)) {
            predecessors.add(transition.getSecond());
        }
        return predecessors;
    }

    public Collection<State> getReachableStatesBy(State state) {
        return super.getReachableStatesBy(state);
    }
}
