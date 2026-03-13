package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.AbstractSchedulerGenerator;
import MTSSynthesis.controller.gr.time.model.ActivityDefinitions;
import MTSSynthesis.controller.gr.time.model.Choice;
import MTSSynthesis.controller.gr.time.model.EnvScheduler;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import org.apache.commons.lang.math.RandomUtils;

import java.util.*;

public class SchedulerGeneratorLight<S,A> extends AbstractSchedulerGenerator<S,A> implements SkeletonBuilder<S,A,Pair<S,S>>{
	Set<Map<S, Integer>> generated;
	Map<S,Integer> result;
	Set<Map<S,Integer>> lasts;

	public SchedulerGeneratorLight(LTS<S,A> environment, Set<A> controllableActions, ActivityDefinitions<A> activityDefinitions) {
		super(environment,controllableActions,activityDefinitions);
		this.generated = new HashSet<Map<S, Integer>>();
	}

	public Set<Map<S,Integer>> next(int cant){
		int i = 0;
		this.lasts  = new HashSet<Map<S,Integer>>();
		if(result!=null){
			this.lasts.add(result);
			i++;
		}
		while(next()!=null && i<cant){
			this.lasts.add(result);
			i++;
		}
		return this.lasts;
	}
	
	public GenericChooser<S, A, Pair<S,S>> get() {
		return build(result);
	}

	public Set<Map<S, Integer>> getLasts(){
		return this.lasts;
	}
	
	public Map<S,Integer> next(){
		int i = 0;
		result = null; 
		while(i < limit){
			Map<S,Integer> skeleton = new HashMap<S,Integer>();
			chooseActions(skeleton,lts.getInitialState(), new HashSet<S>());
			if(generated.contains(skeleton)){
				i++;
			}else{
				generated.add(skeleton);
				result = skeleton;
				break;
			}
		}
		return result;
	}
	
	public GenericChooser<S, A, Pair<S, S>> build(Map<S, Integer> skeleton) {
		EnvScheduler<S,A> result = new EnvScheduler<S,A>(actions.getControllableActions());
		for (S s: skeleton.keySet()) {
			result.setChoice(s, choices.get(s).get(skeleton.get(s)));
		}
		return result;
	}

	public Set<Map<S,Integer>> getGenerated() {
		return generated;
	}
	
	private void chooseActions(Map<S, Integer> skeleton, S initial, Set<S> visited) {
		Set<S> added = new HashSet<S>();
		Queue<S> pending  = new LinkedList<S>();
		Set<A> uncontrollableChoices = new HashSet<A>();
		pending.add(initial);
		added.add(initial);
		while(!pending.isEmpty()){
			S state = pending.poll();
			List<Integer> virtual_ids = getChoices(state, uncontrollableChoices);
			if(!virtual_ids.isEmpty()){
				int virtual_id = RandomUtils.nextInt(virtual_ids.size());
				int real_id = virtual_ids.get(virtual_id);
				Choice<A> choice = this.choices.get(state).get(real_id);
				//Add uncontrollableChoices to be consistent. 
				for (A label : actions.getUncontrollableActions()) {
					if(choice.getAlternative().contains(label) || choice.getChoice().contains(label)){
						uncontrollableChoices.add(label);
					}
				}
				skeleton.put(state, real_id);
				addSuccessors(state, added, pending, choice);
			}
		}
	}
	
	private List<Integer> getChoices(S state, Set<A> uncontrollableChoices) {
		List<Integer> filteredChoices = new ArrayList<Integer>();
		List<Choice<A>> actualChoices = choices.get(state);
		for(int i=0; i < actualChoices.size(); i++){
			if(isCompatible(actualChoices.get(i), uncontrollableChoices)){
				filteredChoices.add(i);
			}
		}
		return filteredChoices;
	}

}

