package MTSSynthesis.controller.parity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.Game;
import MTSSynthesis.controller.model.Rank;
import MTSSynthesis.controller.model.RankBasedGameSolver;
import MTSSynthesis.controller.model.Strategy;


public class ParityGameSolver<State> extends RankBasedGameSolver<State, Integer> {

	private ParityGame<State> game;
	private ParityRankSystem<State> rankSystem;
	
	@Override
	public Set<State> getWinningStates() {
		Set<State> winning = new HashSet<State>();
		for (State state : game.getStates()) {
			if (!this.rankSystem.getRank(new StrategyState<State,Integer>(state,0)).isInfinity()) {
				winning.add(state);
			}
		}
		return winning; 
	}

	public Game<State> getGame(){
		return game;
	}
	
	@Override
	public boolean isWinning(State state) {
		return !this.rankSystem.getRank(new StrategyState<State,Integer>(state,0)).isInfinity();
	}

	@Override
	public Strategy<State, Integer> buildStrategy() {
		this.solveGame();
		
		Strategy<State,Integer> strategy = new Strategy<State,Integer>();
		
		for (State state : this.getWinningStates()) {
			Set<StrategyState<State, Integer>> successors = new HashSet<StrategyState<State, Integer>>();
			for (State succ : this.game.getSuccessors(state)) {
				StrategyState<State, Integer> succStrategyState = new StrategyState<State, Integer>(succ,0);
				StrategyState<State, Integer> strategyState = new StrategyState<State, Integer>(state, 0);
				if (this.getRank(strategyState).compareTo(this.getRank(succStrategyState))>0) {
					successors.add(succStrategyState);
				}
			}
		}
		return strategy;
	}

	@Override
	protected void addPredecessorsTo(Queue<StrategyState<State, Integer>> pending, StrategyState<State, Integer> strategyState, Rank bestRank) {
		Set<State> predecessors = this.game.getPredecessors(strategyState.getState());
		pending.addAll(this.getStrategyStates(predecessors)); 		
	}

	private Collection<StrategyState<State, Integer>> getStrategyStates(Set<State> predecessors) {
		Collection<StrategyState<State, Integer>> strategyPredeccessors = new HashSet<StrategyState<State,Integer>>();
		for (State state : predecessors) {
			strategyPredeccessors.add(new StrategyState<State, Integer>(state, 0));
		}
		return strategyPredeccessors;
	}

	@Override
	protected void updateRank(StrategyState<State, Integer> state, Rank bestRank) {
		this.rankSystem.set(state, bestRank); 
	}

	@Override
	protected void initialise(Queue<StrategyState<State, Integer>> pending) {
//		all with odd priority
		pending.addAll(this.getStrategyStates(this.game.getGoal().getOddPriorityStates()));
	}

	@Override
	protected Rank best(StrategyState<State, Integer> state) {
		
		ParityRank bestRank = this.getBestFromSuccessors(state);
		
		Integer priority = this.game.getPriority(state.getState());
		if (priority%2==1) { 
			bestRank.increase(priority, this.rankSystem); 
		} else {
			bestRank.truncate(priority);
		}
		return bestRank;

	}

	protected ParityRank getBestFromSuccessors(StrategyState<State, Integer> state) {
		ParityRank best;
		if (this.game.isUncontrollable(state.getState())) {
			best = this.rankSystem.getMax(buildStrategyStatesSet(state, this.game.getUncontrollableSuccessors(state.getState())));
		} else {
			best = this.rankSystem.getMin(buildStrategyStatesSet(state, this.game.getControllableSuccessors(state.getState())));
		}
		return best;
	}

	private Set<StrategyState<State, Integer>> buildStrategyStatesSet(StrategyState<State, Integer> state, Set<State> successors) {
		Set<StrategyState<State,Integer>> result = new HashSet<StrategyState<State,Integer>>();
		for (State successor : successors) {
			result.add(new StrategyState<State,Integer>(successor,state.getMemory()));
		}
		
		return result;
	}

	@Override
	protected ParityRank getRank(StrategyState<State, Integer> state) {
		return this.rankSystem.getRank(state);
	}

	@Override
	protected Set<State> getGameStates() {
		return this.game.getStates();
	}

}
