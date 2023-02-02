package MTSSynthesis.controller.model.gr.concurrency;

import java.util.HashMap;
import java.util.Map;

import MTSSynthesis.controller.gr.StrategyState;

public class ConcurrencyLevel<State>{
	
	private Map<State,Integer> concurrency;
	
	public ConcurrencyLevel() {
		this.concurrency = new HashMap<State, Integer>();
	}
	
	public Integer getLevel(State state){
		if(state instanceof StrategyState){
			return concurrency.get(((StrategyState) state).getState());
		}
		return concurrency.get(state);
	}
	
	public void updateLevel(State state, Integer level) {
		this.concurrency.put(state, level);
	}
	
	public int getMaxLevel() {
		int max = 0;
		for (State state : concurrency.keySet()) {
			int value = this.getLevel(state);
			if(value > max)
				max = value;
		}
		return max;
	}
	
	@Override
	public String toString() {
		return concurrency.toString();
	}
	
	
}
