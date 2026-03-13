package ltsa.ui.exploration;

import java.util.ArrayList;
import java.util.HashMap;

import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class RefinedMTS {

	HashMap<RefinedMTSState, ArrayList<RefinedMTSTransition>> transitions;
	RefinedMTSState currentState;
	
	public RefinedMTS() {
		
		transitions = new HashMap<RefinedMTSState, ArrayList<RefinedMTSTransition>>();
		
		RefinedMTSState firstState = new RefinedMTSState();
		currentState = firstState;
		transitions.put(currentState, new ArrayList<RefinedMTSTransition>());
	}
	
	public ArrayList<RefinedMTSTransition> getTransitions(RefinedMTSState state, TransitionType type){
		
		ArrayList<RefinedMTSTransition> result = new ArrayList<RefinedMTSTransition>(); 
		for (RefinedMTSTransition t : transitions.get(state)) {
			if (t.type() == type){
				result.add(t);
			}
		}
		return result;
	}
	
	public ArrayList<RefinedMTSTransition> getTransitions(RefinedMTSState state){
		return transitions.get(state);
	}
	
	public boolean addTransition(String action, RefinedMTSState to, TransitionType type){
		
		if (!transitions.containsKey(to)){
			transitions.put(to, new ArrayList<RefinedMTSTransition>());
		}
		
		boolean result = false;
		RefinedMTSTransition t = new RefinedMTSTransition(currentState, action, to, type);
		if (transitions.containsKey(currentState)){
			result = transitions.get(currentState).add(t);

		}
		return result;
	}
	
	public boolean removeTransition(RefinedMTSState from, String event, RefinedMTSState to, TransitionType type){
		boolean result = false;
		if (transitions.containsKey(from)){ 
			for (RefinedMTSTransition t : transitions.get(from)) {
				if (t.equals(from, event, to, type)){
					result = transitions.get(from).remove(t);
				}
			}
		}
		return result;
	}
	
	public String toString(){
		return "currentState:"+currentState.toString()+"\n transitions:"+ transitions.toString();
	}

	public void performTransition(String event, Long vState) {
		
		ArrayList<RefinedMTSTransition> result = new ArrayList<RefinedMTSTransition>();
		RefinedMTSState toChangeState = new RefinedMTSState();
		for (RefinedMTSTransition refinedTransition : transitions.get(currentState)) {
			// BECAREFUL WITH NO DETERMINISTIC!
			if (refinedTransition.getEvent().equals(event)){
				toChangeState = refinedTransition.to();
				toChangeState.setFirstValue(vState);
				transitions.remove(refinedTransition.to());
				transitions.put(toChangeState, new ArrayList<RefinedMTSTransition>());
				result = transitions.get(currentState);
				result.remove(refinedTransition);
				refinedTransition.setTo(toChangeState);
				result.add(refinedTransition);
				break;
			}
		}
		
		transitions.put(currentState, result);
		currentState = toChangeState;
	}


}
