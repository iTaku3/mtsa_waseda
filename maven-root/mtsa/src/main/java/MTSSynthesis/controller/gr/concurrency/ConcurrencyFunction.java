package MTSSynthesis.controller.gr.concurrency;

import java.util.HashMap;
import java.util.Set;

import MTSSynthesis.controller.model.gr.concurrency.ConcurrencyLevel;

public class ConcurrencyFunction<State>  extends DoubleRankFunction<State>  {
	public ConcurrencyFunction(Set<State> states, ConcurrencyLevel<State> concurrency) {
		this.function = new HashMap<State,DoubleRank>();
		this.context = new DoubleRankContext(concurrency.getMaxLevel());
		for (State state : states) {
			this.function.put(state, new DoubleRank(concurrency.getLevel(state), this.context));
		}
	}
}
