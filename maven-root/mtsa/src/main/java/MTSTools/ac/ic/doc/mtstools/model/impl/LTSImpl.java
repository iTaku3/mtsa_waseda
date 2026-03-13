package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class LTSImpl<State, Action> 
	extends AbstractTransitionSystem<State, Action>
	implements LTS<State, Action> {

	private Map<State, BinaryRelation<Action, State>> transitions;

	public LTSImpl(State initialState) {
		super(initialState);
	}	
	

	public boolean addState(State state) {		
		if (super.addState(state)) {
			this.getInternalTransitions().put(state,this.newRelation());
			return true;
		}
		return false;
	}

	protected Map<State, BinaryRelation<Action, State>> getInternalTransitions() {
		if (this.transitions == null) {
			this.transitions = new HashMap<State, BinaryRelation<Action, State>>();
		}
		return this.transitions;
	}

	public boolean addTransition(State from, Action label, State to) {
		this.validateNewTransition(from,label,to);
		return this.getTransitions(from).addPair(label, to);
	}
	
	public Map<State, BinaryRelation<Action, State>> getTransitions() {
		return Collections.unmodifiableMap(this.transitions);
	}

	public int getTransitionsNumber() {
		int answer = 0;
		for (BinaryRelation<Action,State> binRel : this.transitions.values()){
			answer += binRel.size();
		}
		return answer;
	}

	public BinaryRelation<Action, State> getTransitions(State state) {
		return this.transitions.get(state);
	}

	public boolean removeTransition(State from, Action label, State to) {
		return this.transitions.get(from).removePair(label, to);
	}
	

	protected BinaryRelation<Action, State> getTransitionsFrom(State state) {
		return getTransitions(state);
	}

	public void removeAction(Action action) {
		for (State state : getStates()) {
			for (State stateTo: getTransitionsFrom(state).getImage(action)) {
				removeTransition(state, action, stateTo);
			}
		}
		getInternalActions().remove(action);
	}

	@Override
	protected void removeTransitions(Collection<State> unreachableStates) {
		this.removeTransitions(this.transitions, unreachableStates);
	}

	@Override
	public String toString() {
		Hashtable dict = new Hashtable();
		int i = 0;
		String stateName = "";
		
		for(State state : getStates()){
			i++;
			if(i == 1){
				stateName = "M";
			}else{
				stateName = String.format("E_%d",i);
			}			
			dict.put(state, stateName);
		}
		
		String result = "";
		String stateValues = "";
		i = 0;
		int j = 0;
		boolean firstState = true;
		boolean firstAction = true;
		for(State state : getStates()){
			i++;
			if(firstState){
				stateName = "M";
				firstState = false;
			}else{
				result += ",\n";
				stateName = String.format("E_%d",i);
			}
			
			stateValues = "";
			firstAction = true;
			j = 0;
			for (Pair<Action, State> transition: getTransitionsFrom(state)) {
				j++;
				if(firstAction){
					firstAction = false;
				}else{
					stateValues += " | ";
				}
				try{
					stateValues += String.format("%s -> %s", (String)transition.getFirst(), dict.get(transition.getSecond()));
				}catch(Exception e){
					stateValues += String.format("action_%d -> %s", j, dict.get(transition.getSecond()));
				}
			}
			result += String.format("%s = (%s)", stateName, stateValues);
			
		}
		result += ".\n";
		return result;
	}
}
