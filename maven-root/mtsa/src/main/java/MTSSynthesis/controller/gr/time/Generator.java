package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.model.Choice;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Created by ezecastellano on 11/05/16.
 */
public abstract class Generator<S,A> {
    protected Map<S , List<Choice<A>>> choices;
    protected Long maximum;
    protected LTS<S,A> lts;
    protected GroupedActions<A> actions;
    protected int limit;

    public  Generator(LTS<S,A> lts, Set<A> controllableActions){
        this.maximum = null;
        this.lts = lts;
        this.actions = new GroupedActions<A>(lts.getActions(),controllableActions);
        this.limit = 2048;
    }

    public Long getEstimation(){
        if(this.maximum == null){
            Long acum = 1L;
            for (S k : choices.keySet()) {
                int size = choices.get(k).size();
                if(size > 0){
                    acum *= size;
                }
            }
            this.maximum = acum;
        }
        return this.maximum;
    }

    protected void addSuccessors(S state, Set<S> added, Queue<S> pending, Choice<A> choice) {
        for (A label : choice.getAvailableLabels()) {
            for(S successor : lts.getTransitions(state).getImage(label)){
                if(!added.contains(successor)){
                    pending.add(successor);
                    added.add(successor);
                }
            }
        }
    }
}
