package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.model.Choice;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ezecastellano on 10/05/16.
 */
public class RestrictedSchedulerChoicesBuilder<S,A> extends SchedulerChoicesBuilder<S,A> {
    Set<S> finalStates;
    public RestrictedSchedulerChoicesBuilder(LTS<S, A> lts, Set<A> controllableActions, Set<S> finalStates) {
        super(lts, controllableActions);
        this.finalStates = finalStates;
    }


    @Override
    public List<Choice<A>> generateChoices(S state){
        List<Choice<A>> choices;
        if(!finalStates.contains(state)){
            choices = super.generateChoices(state);
        }else{
            choices = new ArrayList<Choice<A>>();
        }
        return choices;
    }

}
