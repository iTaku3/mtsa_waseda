package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.model.Choice;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import org.apache.commons.lang.math.RandomUtils;

import java.util.*;

public class ControllerGenerator<S,A> extends Generator<S,A>{
	private Set<GenericChooser<S, A, S>> generated;
	private ControllerChooser<S, A> result;
	private Set<ControllerChooser<S,A>> lasts;
	
	public ControllerGenerator(LTS<S,A> controller, Set<A> controllableActions, Set<S> finalStates) {
		super(controller,controllableActions);
		this.generated = new HashSet<GenericChooser<S, A, S>>();
		ChoicesBuilder<S,A> choicesBuilder = new ControllerChoicesBuilder<S,A>(controller,controllableActions,finalStates);
		this.choices = choicesBuilder.getAllChoices();
	}

	public Set<ControllerChooser<S,A>> getLasts(){
		return this.lasts;
	}
	
	public Set<ControllerChooser<S,A>> next(int cant){
		int i = 0;
		this.lasts  = new HashSet<ControllerChooser<S,A>>();
		if(get()!=null){
			this.lasts.add(get());
			i++;
		}
		while(next()!=null && i<cant){
			this.lasts.add(get());
			i++;
		}
		return this.lasts;
	}
	
	public ControllerChooser<S,A> next(){
		int i = 0;
		result = null; 
		while(i < limit){
			result = new ControllerChooser<S,A>(actions.getControllableActions());
			chooseActions(result,lts.getInitialState(), new HashSet<S>());
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
	
	public ControllerChooser<S, A> get() {
		return result;
	}

	public Set<GenericChooser<S, A, S>> getGenerated() {
		return generated;
	}
	
	private void chooseActions(ControllerChooser<S, A> scheduler, S initial, Set<S> visited) {
		Set<S> added = new HashSet<S>();
		Queue<S> pending  = new LinkedList<S>();
		pending.add(initial);
		added.add(initial);
		while(!pending.isEmpty()){
			S state = pending.poll();
			List<Choice<A>> choices = getChoices(state);
			if(!choices.isEmpty()){
				Choice<A> choice = choices.get(RandomUtils.nextInt(choices.size()));
				scheduler.setChoice(state, choice);
				this.addSuccessors(state, added, pending, choice);
			}
		}
	}

	private List<Choice<A>> getChoices(S state) {
		return choices.get(state);
	}

}

