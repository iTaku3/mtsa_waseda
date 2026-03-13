package MTSSynthesis.controller.gr.concurrency;

import java.util.Map;
import java.util.Set;

import MTSSynthesis.controller.model.AbstractRankFunction;
import MTSSynthesis.controller.model.Rank;
import MTSSynthesis.controller.model.RankContext;

public abstract class DoubleRankFunction<State> extends AbstractRankFunction<State> {
	
	protected Map<State,DoubleRank> function;
	protected DoubleRankContext context;
	
	public DoubleRank getAverage(Set<State> states) {
		double total = 0;
		for (State state : states) {
			total += this.function.get(state).getValue();
		}
		return new DoubleRank(total/states.size(), context);
	}

	@Override
	public boolean isInfinity(State state) {
		return this.context.getMax() == function.get(state).getValue();
	}

	@Override
	public DoubleRank getRank(State state) {
		return this.function.get(state);
	}

	@Override
	public RankContext getContext() {
		return this.context;
	}

	@Override
	public String toString() {
		return "Rank Function: " + this.function.toString();
	}

	@Override
	public Rank getNewRank() {
		return new DoubleRank(0, this.context);
	}

}
