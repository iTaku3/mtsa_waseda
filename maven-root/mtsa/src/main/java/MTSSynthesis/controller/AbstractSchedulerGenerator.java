package MTSSynthesis.controller;

import MTSSynthesis.controller.gr.time.ChoicesBuilder;
import MTSSynthesis.controller.gr.time.Generator;
import MTSSynthesis.controller.gr.time.SchedulerChoicesBuilder;
import MTSSynthesis.controller.gr.time.model.ActivityDefinitions;
import MTSSynthesis.controller.gr.time.model.Choice;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

import java.util.Set;

/**
 * Created by ezecastellano on 11/05/16.
 */
public class AbstractSchedulerGenerator<S,A> extends Generator<S,A> {
    protected ActivityDefinitions<A> activityDefinitions;

    public AbstractSchedulerGenerator(LTS<S,A> environment, Set<A> controllableActions, ActivityDefinitions<A> activityDefinitions){
        super(environment,controllableActions);
        this.activityDefinitions = activityDefinitions;
        ChoicesBuilder<S,A> choicesBuilder = new SchedulerChoicesBuilder<S,A>(environment,actions.getControllableActions());
        this.choices = choicesBuilder.getAllChoices();
    }

    protected boolean compatibleLabel(Set<A> uncontrollableChoices, Set<A> labels) {
        for(A label: labels){
            if(actions.getUncontrollableActions().contains(label) && activityDefinitions.hasRelatedActions(label)){
                for(A related: activityDefinitions.getRelatedActions(label)){
                    if(!related.equals(label) && uncontrollableChoices.contains(related)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected boolean isCompatible(Choice<A> choice, Set<A> uncontrollableChoices) {
        return compatibleLabel(uncontrollableChoices, choice.getChoice())
                && compatibleLabel(uncontrollableChoices, choice.getAlternative());
    }


}
