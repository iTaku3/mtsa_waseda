package MTSSynthesis.controller.gr.basics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import MTSSynthesis.controller.gr.StrategyState;

public class StateDecisionPoints<State,Memory> extends DecisionPoints {
	StrategyState<State, Memory> state;
	List<StrategyState<State, Memory>> succesors;
	public StateDecisionPoints(StrategyState<State, Memory> state, Set<StrategyState<State, Memory>> contrSuccessors, boolean zero){
		this(contrSuccessors.size(),zero);
		this.state = state;
		this.succesors = new ArrayList<StrategyState<State, Memory>>(this.length);
		for (StrategyState<State, Memory> strategyState : contrSuccessors) {
			succesors.add(strategyState);
		}
	}

	public StateDecisionPoints(int length, boolean zero) {
		super(length, zero);
	}
	
	public StrategyState<State, Memory> getState() {
		return state;
	}
	
	public Set<StrategyState<State, Memory>> getAvailableSucc() {
		Set<StrategyState<State, Memory>> available = new HashSet<StrategyState<State, Memory>>();
		for (int i = 0; i < length; i++) {
			if(decisions[i])
				available.add(succesors.get(i));
		}
		return available;
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
