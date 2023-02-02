package MTSSynthesis.controller.gr.concurrency;

import java.util.HashMap;
import java.util.Set;

public class TransientFunction<State> extends DoubleRankFunction<State>{
	
	public TransientFunction(Set<State> allStates) {
		this.function = new HashMap<State,DoubleRank>();
		//TODO: Estimate max #TransientStates
		int MAX_TRANSIENT_VALUE = allStates.size();
		this.context = new DoubleRankContext(MAX_TRANSIENT_VALUE);
		for (State state : allStates) {
			this.function.put(state, new DoubleRank(0, this.context));
		}
	}
}
