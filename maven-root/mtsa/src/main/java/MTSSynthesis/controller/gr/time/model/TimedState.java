package MTSSynthesis.controller.gr.time.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.RealExpr;

public class TimedState {
	
	BoolExpr condition;
	Map<TimedState,TimedTrace> successors;
	Set<TimedState> predecessors;
	
	public TimedState(BoolExpr condition) {
		if (condition == null)
			throw new RuntimeException();
		this.condition = condition;
		successors = new HashMap<TimedState, TimedTrace>();
		predecessors = new HashSet<TimedState>();
	}
	
	public void addSuccessor(BoolExpr condition, Set<RealExpr> resetedClocks, String label, TimedState state){
		successors.put(state,new TimedTrace(condition,resetedClocks, label));
		state.predecessors.add(this);
	}
	
	public Set<TimedState> getPredecessors() {
		return predecessors;
	}
	
	public Set<TimedState> getSuccessors() {
		return successors.keySet();
	}
	
	public TimedTrace getTransition(TimedState state) {	
		return successors.get(state);
	}
	
	public BoolExpr getCondition() {
		return condition;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		TimedState other = (TimedState) obj;
		return this.condition.equals(other.condition) &&
		this.successors.equals(other.successors) &&
		this.predecessors.equals(other.predecessors);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[State Condition:(");
		sb.append(condition.toString());
		sb.append("),");
		sb.append("Transitions:{");
		for (TimedState succ : successors.keySet()) {
			sb.append(successors.get(succ).toString() + "->"+succ.getCondition());
		}
		sb.append("}]");
		return sb.toString();
	}

}
