package MTSSynthesis.controller.parity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import MTSSynthesis.controller.model.Goal;

public class ParityGoal<State> implements Goal {
	private Map<State, Integer> priorities;
	
	public ParityGoal(Map<State, Integer> priorities) {
		this.priorities = priorities;
	}
	
	public Set<State> getOddPriorityStates() {
		Set<State> odds = new HashSet<State>();
		for (Map.Entry<State, Integer> entry : this.priorities.entrySet()) {
			if (entry.getValue()%2==1) {
				odds.add(entry.getKey());
			}
		};
		return odds;
	}

	public Integer getPriority(State state) {
		return this.priorities.get(state);
	}


}
