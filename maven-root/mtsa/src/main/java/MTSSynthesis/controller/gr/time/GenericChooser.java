package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.model.Choice;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class GenericChooser<S,A,D> {
	
	Map<S, Choice<A>> choices;
	Set<A> uncontrollableChoices;

	public void setUncontrollableChoices(Set<A> uncontrollableChoices) {
		this.uncontrollableChoices = uncontrollableChoices;
	}
	
	public GenericChooser(){
		this.choices = new HashMap<S, Choice<A>>();
	}
	
	public void setChoice(S s,Choice<A> c){
		if(c == null){
			throw new RuntimeException("Don't set null as a choice!");
		}
		this.choices.put(s, c);
	}

	protected abstract Set<A> getControllableActions();
	
	public Choice<A> getChoice(S s){
		return this.choices.get(s);
	}
	
	public boolean isDefined(S s){
		return this.choices.keySet().contains(s) && this.getChoice(s)!=null;
	}
	
	public Set<S> getStates(){
		return this.choices.keySet();
	}

	public LTS<D, A> applyTo(LTS<D, A> lts, Translator<S,D> translator){
		LTS<D,A> result = new LTSImpl<D, A>(lts.getInitialState());
		result.addStates(lts.getStates());
		result.addActions(lts.getActions());
		for (D s : result.getStates()) {
			applyToState(s,lts,result,translator);
		}
		result.removeUnreachableStates();
		return result;
	}
	
	
	private void applyToState(D s, LTS<D, A> lts,	LTS<D, A> result, Translator<S, D> translator) {
		BinaryRelation<A, D> transitions = lts.getTransitions(s);
		//The state may not be defined for this scheduler if it's not reachable for every mts after applying the scheduler.
		if(this.choices.containsKey(translator.translate(s))){
			Choice<A> c = this.choices.get(translator.translate(s));
			//If has alternative implies the choice is controllable. 
			if(!c.hasAlternative()){
				for(A label : c.getChoice()){
					addTransitions(s, result, transitions, label);
				}
			}
			else{
				//Controllable action may not be in the available actions
				for(A label : c.getChoice()){
					if(transitions.getImage(label).isEmpty()){
						boolean choosed = false;
						//Try to choose another controllable action.
						for(A k : getControllableActions()){
							if(!transitions.getImage(k).isEmpty()){
								addTransitions(s, result, transitions, k);
								choosed = true;
								break;
							}
						}
						//There is no controllable action available.
						if(!choosed){
							if(c.hasAlternative()){
								for(A alt : c.getAlternative()){
									addTransitions(s, result, transitions, alt);
								}
							}else{
								throw new RuntimeException("There is no action available after applying the scheduller.");
							}
						}
					}else{//The same controllable transition is available.
						addTransitions(s, result, transitions, label);
					}
				}
			}
		}		
	}

	private void addTransitions(D s, LTS<D, A> result, BinaryRelation<A, D> transitions, A label) {
		for(D to: transitions.getImage(label)){
			result.addTransition(s, label, to);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof GenericChooser<?, ?, ?>){
			GenericChooser<?, ?, ?> gc = (GenericChooser<?, ?, ?>) obj;
			return this.choices.equals(gc.choices);
		}
		else
			return false;
	}
	
	
	@Override
	public int hashCode() {
		return choices.hashCode();
	}

	@Override
	public String toString() {
		return choices.toString();
	}

}
