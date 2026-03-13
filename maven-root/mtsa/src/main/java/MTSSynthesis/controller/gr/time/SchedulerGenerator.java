package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.AbstractSchedulerGenerator;
import MTSSynthesis.controller.gr.time.model.ActivityDefinitions;
import MTSSynthesis.controller.gr.time.model.Choice;
import MTSSynthesis.controller.gr.time.model.EnvScheduler;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import org.apache.commons.lang.math.RandomUtils;

import java.util.*;

public class SchedulerGenerator<S,A> extends AbstractSchedulerGenerator<S,A> {
	protected Set<GenericChooser<S, A, Pair<S,S>>> lasts;
	protected Set<GenericChooser<S, A, Pair<S, S>>> generated;
	protected GenericChooser<S, A, Pair<S,S>> result;

	public SchedulerGenerator(LTS<S,A> environment, Set<A> controllableActions, ActivityDefinitions<A> activityDefinitions) {
		super(environment, controllableActions,activityDefinitions);
		this.generated = new HashSet<GenericChooser<S, A, Pair<S,S>>>();
	}
	
	public GenericChooser<S, A, Pair<S,S>> next(){
		int i = 0;
		result = null; 
		while(i < limit){
			result = new EnvScheduler<S,A>(actions.getControllableActions());
			chooseActions(result, lts.getInitialState(), new HashSet<S>());
			if(generated.contains(result)){
				i++;
				result = null;
			}else{
				generated.add(result);
				return result;
			}
		}
		return result;
	}

	public Set<GenericChooser<S, A, Pair<S,S>>> getGenerated() {
		return generated;
	}
	
	private void chooseActions(GenericChooser<S, A, Pair<S, S>> scheduler, S initial, Set<S> visited) {
		Set<S> added = new HashSet<S>();
		Queue<S> pending  = new LinkedList<S>();
		Set<A> uncontrollableChoices = new HashSet<A>();
		pending.add(initial);
		added.add(initial);
		while(!pending.isEmpty()){
			S state = pending.poll();
			List<Choice<A>> choices = getChoices(state, uncontrollableChoices);
			if(!choices.isEmpty()){
				Choice<A> choice = choices.get(RandomUtils.nextInt(choices.size()));
				//Add uncontrollableChoices to be consistent. 
				for (A label : actions.getUncontrollableActions()) {
					if(choice.getAlternative().contains(label) || choice.getChoice().contains(label)){
						uncontrollableChoices.add(label);
					}
				}
				scheduler.setChoice(state, choice);
				addSuccessors(state, added, pending, choice);
			}
		}
		scheduler.setUncontrollableChoices(uncontrollableChoices);
	}
	
	private List<Choice<A>> getChoices(S state, Set<A> uncontrollableChoices) {
		List<Choice<A>> filteredChoices = new ArrayList<Choice<A>>();
		for(Choice<A> choice : choices.get(state)){
			if(isCompatible(choice, uncontrollableChoices)){
				filteredChoices.add(choice);
			}
		}
		return filteredChoices;
	}

	public Set<GenericChooser<S, A, Pair<S,S>>> getLasts(){
		return this.lasts;
	}

	public GenericChooser<S, A, Pair<S,S>> get() {
		return result;
	}
}

