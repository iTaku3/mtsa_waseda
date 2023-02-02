package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.model.Choice;
import MTSSynthesis.controller.gr.time.model.ChoiceType;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

import java.util.*;

/**
 * Created by ezecastellano on 09/05/16.
 */
public abstract class ChoicesBuilder<S, A> {
    protected Map<S, Set<A>> controllable;
    protected Map<S, Set<A>> uncontrollable;
    protected Map<S, Set<A>> ends;

    public ChoicesBuilder(LTS<S, A> lts, Set<A> controllableActions) {
        this.controllable = new HashMap<S, Set<A>>();
        this.uncontrollable = new HashMap<S, Set<A>>();
        this.ends = new HashMap<S, Set<A>>();
        for(S state : lts.getStates()){
            identifyTransitionType(lts.getTransitions(state), controllableActions, state);
        }
    }

    private void identifyTransitionType(BinaryRelation<A, S> stateTransitions, Set<A> cActions, S state) {
        Set<A> controllable = new HashSet<A>();
        Set<A> uncontrollable = new HashSet<A>();
        Set<A> ends = new HashSet<A>();
        for(Pair<A, S> transition : stateTransitions){
            A label = transition.getFirst();
            SchedulerUtils<A> su = new SchedulerUtils<A>();
            ChoiceType type = su.getChoiceType(label, cActions);
            if(type.equals(ChoiceType.CONTROLLABLE))
                controllable.add(label);
            else if(type.equals(ChoiceType.UNCONTROLLABLE)){
                uncontrollable.add(label);
            }else if(type.equals(ChoiceType.ENDS))
                ends.add(label);
            else
                throw new RuntimeException();
        }
        this.controllable.put(state,controllable);
        this.uncontrollable.put(state,uncontrollable);
        this.ends.put(state,ends);
    }


    public abstract List<Choice<A>> generateChoices(S state);


    public Map<S , List<Choice<A>>> getAllChoices(){
        Map<S , List<Choice<A>>>  result = new HashMap<S , List<Choice<A>>>();
        for (S st : controllable.keySet()) {
            List<Choice<A>> choices = this.generateChoices(st);
            result.put(st, choices);
        }
        return result;
    }
}
