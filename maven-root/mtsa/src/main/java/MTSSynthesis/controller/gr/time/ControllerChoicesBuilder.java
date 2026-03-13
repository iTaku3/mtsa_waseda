package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.model.Choice;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ezecastellano on 09/05/16.
 */
public class ControllerChoicesBuilder<S,A> extends ChoicesBuilder<S,A> {
    private Set<S> finalStates;

    public ControllerChoicesBuilder(LTS<S, A> lts, Set<A> controllableActions, Set<S> finalStates) {
        super(lts,controllableActions);
        this.finalStates = finalStates;
    }

    @Override
    public List<Choice<A>> generateChoices(S state){
        List<Choice<A>> choices;
        if(!finalStates.contains(state)){
            Set<A> controllableActions = controllable.get(state);
            Set<A> uncontrollableAction = new HashSet<A>();
            uncontrollableAction.addAll(uncontrollable.get(state));
            uncontrollableAction.addAll(ends.get(state));
            choices = (this.getChoices(controllableActions,uncontrollableAction));
        }else{
            choices = new ArrayList<Choice<A>>();
        }
        return choices;
    }

    //@ezecastellano: We are generating controllers with one or zero controllable actions enabled in each state.
    protected List<Choice<A>> getChoices(Set<A> controllableActions, Set<A> uncontrollableActions){
        List<Choice<A>> choices = new ArrayList<Choice<A>>();

        for (A c : controllableActions) {
            Set<A> choice  = new HashSet<A>();
            choice.add(c);
            choice.addAll(uncontrollableActions);
            choices.add(new Choice<A>(choice));
        }

        if(!uncontrollableActions.isEmpty()){
            choices.add(new Choice<A>(uncontrollableActions));
        }

        return choices;
    }


}
