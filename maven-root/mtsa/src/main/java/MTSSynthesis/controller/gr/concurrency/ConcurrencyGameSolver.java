package MTSSynthesis.controller.gr.concurrency;

import java.util.Queue;
import java.util.Set;

import MTSSynthesis.controller.model.Game;
import MTSSynthesis.controller.model.Rank;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSSynthesis.controller.gr.StrategyState;

public class ConcurrencyGameSolver<S,A> extends HeuristicPrunningGameSolver<S, A> {

	public ConcurrencyGameSolver(Game<S> game, LTS<S, A> lts, DoubleRankFunction<S> function, Set<S> finalStates) {
		super(game, lts, function, finalStates);
	}
	
	@Override
	protected Rank best(StrategyState<S, Integer> strategyState) {
		//The best rank is my best successor.
		Rank bestRank = super.getBestFromSuccessors(strategyState.getState());
		return bestRank;
	}
	
	@Override
	protected Rank getBest(Set<S> strategySuccesors) {
		return this.function.getMaximum(strategySuccesors);
	}

	@Override
	protected boolean betterCondition(int compare) {
		return compare < 0;
	}

	@Override
	protected void initialise(Queue<StrategyState<S, Integer>> pending) {
		for (S state : lts.getStates()) {
			pending.add(new StrategyState<S,Integer>(state, 1));
		}
	}

}
