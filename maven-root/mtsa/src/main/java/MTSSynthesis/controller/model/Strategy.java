package MTSSynthesis.controller.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import MTSSynthesis.controller.gr.StrategyState;

public class Strategy<S, M> {
	
	Map<StrategyState<S, M>, Set<StrategyState<S, M>>> map;
	
	public Strategy() {
		this.map = new HashMap<StrategyState<S,M>, Set<StrategyState<S,M>>>();
	}
	
	public void addSuccessors(StrategyState<S, M> root, Set<StrategyState<S, M>> successors){
		Set<StrategyState<S, M>> succ = this.map.get(root);
		if (succ==null) {
			succ = new HashSet<StrategyState<S,M>>();
		}
		succ.addAll(successors);
		this.map.put(root, succ);
	}
	
	public Set<StrategyState<S, M>> getSuccessors(StrategyState<S, M> state) {
		return this.map.get(state);
	}
	
	public Set<StrategyState<S,M>> getStates() {
		return this.map.keySet();
	}
	
	public boolean isSuccessor(StrategyState<S, M> successor,StrategyState<S, M> predecessor) {
		return this.getSuccessors(predecessor)!= null && this.getSuccessors(predecessor).contains(successor);
	}
}
