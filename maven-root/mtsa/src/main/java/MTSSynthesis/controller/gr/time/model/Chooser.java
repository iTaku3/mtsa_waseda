package MTSSynthesis.controller.gr.time.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import MTSSynthesis.controller.gr.time.StrategyIterator;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;

public class Chooser<S,A> {
	
	StrategyIterator<S,A> iterator;
	Map<S, Choice<A>> choices;

	public Chooser(StrategyIterator<S, A> strategyIterator){
		this.iterator = strategyIterator;
		this.choices = new HashMap<S, Choice<A>>();
	}
	
	public void setChoice(S s,Choice<A> c){
		if(c == null){
			throw new RuntimeException("Don't set null as a choice!");
		}
		this.choices.put(s, c);
	}
	
	public Choice<A> getChoice(S s){
		return this.choices.get(s);
	}
	
	public boolean isDefined(S s){
		return this.choices.keySet().contains(s) && this.getChoice(s)!=null;
	}
	
	public Set<S> getStates(){
		return this.choices.keySet();
	}

	public MTS<S, A> applyTo(MTS<S, A> mts){
		MTS<S, A> result = new MTSImpl<S, A>(mts.getInitialState());
		result.addStates(mts.getStates());
		result.addActions(mts.getActions());
		for (S s : result.getStates()) {
			applyToState(s,mts,result);
		}
		return result;
	}
	
	
	private void applyToState(S s, MTS<S, A> mts,	MTS<S, A> result) {
		BinaryRelation<A, S> transitions = mts.getTransitions(s, TransitionType.REQUIRED);
		//The state may not be defined for this scheduler if it's not reachable for every mts after applying the scheduler.
		if(this.choices.containsKey(s)){
			Choice<A> c = this.choices.get(s);
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

	private void addTransitions(S s, MTS<S, A> result, BinaryRelation<A, S> transitions, A label) {
		for(S to: transitions.getImage(label)){
			result.addTransition(s, label, to ,TransitionType.REQUIRED);
		}
	}
	
	protected Set<A> getControllableActions(){
		return this.iterator.getControllableActions();
	}

}
