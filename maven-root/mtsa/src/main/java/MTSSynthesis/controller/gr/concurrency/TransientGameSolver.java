package MTSSynthesis.controller.gr.concurrency;

import java.util.Queue;
import java.util.Set;

import MTSSynthesis.controller.model.Game;
import MTSSynthesis.controller.model.Rank;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSSynthesis.controller.gr.StrategyState;

public class TransientGameSolver<S,A> extends HeuristicPrunningGameSolver<S, A> {

	public TransientGameSolver(Game<S> game, LTS<S, A> lts, DoubleRankFunction<S> function, Set<S> finalStates) {
		super(game, lts, function, finalStates);
	}
	
	@Override
	protected Rank best(StrategyState<S, Integer> strategyState) {
		//The best rank is my best successor.
		S state = strategyState.getState();
		Rank bestRank = super.getBestFromSuccessors(state);
		if(!isTransient(lts.getTransitions(state),getGame().getControllableSuccessors(state))){
			bestRank.increase();
		}
		return bestRank;
	}
	
	private Boolean isTransient(BinaryRelation<A, S> binaryRelation, Set<S> controllable) {
		for (Pair<A,S> strategyState : binaryRelation) {
			if(controllable.contains(strategyState.getSecond())){
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected Rank getBest(Set<S> strategySuccesors) {
		return this.function.getMinimum(strategySuccesors);
	}

	@Override
	protected boolean betterCondition(int compare) {
		return compare > 0;
	}

	@Override
	protected void initialise(Queue<StrategyState<S, Integer>> pending) {
		for (S state : lts.getStates()) {
			pending.add(new StrategyState<S,Integer>(state, 1));
			if(!isTransient(lts.getTransitions(state),getGame().getControllableSuccessors(state))){
				function.getRank(state).increase();
			}
		}
	}

}
