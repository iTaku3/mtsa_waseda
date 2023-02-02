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
public class SchedulerChoicesBuilder<S,A> extends ChoicesBuilder<S,A> {
    public SchedulerChoicesBuilder(LTS<S, A> lts, Set<A> controllableActions) {
        super(lts, controllableActions);
    }
    @Override
    public List<Choice<A>> generateChoices(S st) {
        List<Choice<A>> choices = new ArrayList<Choice<A>>();
        //We are choosing all the controllable set, since we assume
        //the controllers that we are going to schedule
        //have only one controllable action enabled.
        Set<A> controllableActions = controllable.get(st);
        if(uncontrollable.get(st).isEmpty() && ends.get(st).isEmpty()){
            choices.add(new Choice<A>(controllableActions));
        }else{
            if(!controllableActions.isEmpty()){
                for (A u : uncontrollable.get(st)) {
                    Set<A> alternative  = new HashSet<A>();
                    alternative.add(u);
                    choices.add(new Choice<A>(controllableActions,alternative));
                    choices.add(new Choice<A>(alternative));
                }
            }else{
                for (A u : uncontrollable.get(st)) {
                    Set<A> choice  = new HashSet<A>();
                    choice.add(u);
                    choices.add(new Choice<A>(choice));
                }
            }
            if(!ends.get(st).isEmpty()){
                choices.add(new Choice<A>(ends.get(st)));
                if(!controllableActions.isEmpty()){
                    choices.add(new Choice<A>(controllableActions,ends.get(st)));
                }
            }
        }
        return choices;
    }



}
