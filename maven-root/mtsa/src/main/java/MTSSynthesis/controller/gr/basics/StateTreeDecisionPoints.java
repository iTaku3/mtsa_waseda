package MTSSynthesis.controller.gr.basics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import MTSSynthesis.controller.gr.StrategyState;

public class StateTreeDecisionPoints<State,Memory> extends DecisionPoints {
	StrategyState<State, Memory> state;
	Map<StrategyState<State, Memory>, Set<StateTreeDecisionPoints<State, Memory>>> succesors;
	List<StrategyState<State, Memory>> realSuccesors;
	public StateTreeDecisionPoints(StrategyState<State, Memory> state, Set<StrategyState<State, Memory>> contrSuccessors, boolean zero){
		this(contrSuccessors.size(),zero);
		this.state = state;
		this.succesors = new HashMap<StrategyState<State, Memory>,Set<StateTreeDecisionPoints<State, Memory>>>(this.length);
		this.realSuccesors = new ArrayList<StrategyState<State, Memory>>(this.length);
		this.realSuccesors.addAll(contrSuccessors);
	}

	public StateTreeDecisionPoints(int length, boolean zero) {
		super(length, zero);
	}
	
	public StrategyState<State, Memory> getState() {
		return state;
	}
	
	public List<StrategyState<State, Memory>> getRealSuccesors() {
		return realSuccesors;
	}
	
	public Set<StrategyState<State, Memory>> getAvailableSucc() {
		Set<StrategyState<State, Memory>> available = new HashSet<StrategyState<State, Memory>>();
		for (int i = 0; i < length; i++) {
			if(decisions[i])
				available.add(realSuccesors.get(i));
		}
		return available;
	}
	
	@Override
	public int hashCode() {
		return state.hashCode();
	}
	
	public Boolean isFinal(){
		return succesors.isEmpty();
	}
	
	@Override
	public boolean equals(Object obj) {
		return state.equals(((StateTreeDecisionPoints<State, Memory>)obj).state);
	}
	
	public Set<StateTreeDecisionPoints<State, Memory>> getNextDecisionPoints(){
		Set<StateTreeDecisionPoints<State, Memory>> available = new HashSet<StateTreeDecisionPoints<State, Memory>>();
		for (int i = 0; i < length; i++) {
			if(decisions[i])
				available.addAll(succesors.get(realSuccesors.get(i)));
		}
		return available;
	}
	
	public void updateReal(StrategyState<State, Memory> succ, Set<StateTreeDecisionPoints<State, Memory>> next) {
		succesors.put(succ, next);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append(state.toString());
		sb.append(":[");
		sb.append(super.toString());
		sb.append("]}");

		return sb.toString();
	}

	
}
