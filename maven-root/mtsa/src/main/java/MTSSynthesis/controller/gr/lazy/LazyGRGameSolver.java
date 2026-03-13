package MTSSynthesis.controller.gr.lazy;

import java.util.HashSet;
import java.util.Set;

import MTSSynthesis.controller.gr.perfect.PerfectInfoGRGameSolver;
import MTSSynthesis.controller.model.Strategy;
import org.apache.commons.lang.Validate;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.gr.GRGame;

public class LazyGRGameSolver<State> extends PerfectInfoGRGameSolver<State> {
	
	private int maxLazyness;

	public LazyGRGameSolver(GRGame<State> game, GRRankSystem<State> rankSystem) {
		super(game, rankSystem);
		this.maxLazyness = 0;
	}
	
	public LazyGRGameSolver(GRGame<State> game, GRRankSystem<State> rankSystem, Integer maxLazyness) {
		super(game, rankSystem);
		this.maxLazyness = maxLazyness;
	}
	
	public int getMaxLazyness() {
		return maxLazyness;
	}
	
	public Strategy<State,Integer> buildStrategy() {
		this.solveGame();
		
		Strategy<State,Integer> result = new Strategy<State, Integer>();
		
		Set<State> winningStates = this.getWinningStates();

		for (State state : winningStates) {
			for (int guaranteeId = 1 ; guaranteeId <= getGRGoal().getGuaranteesQuantity() ; guaranteeId++) {
				for(int lazyness = maxLazyness; lazyness >= 0; lazyness--){
					StrategyState<State,Integer> source = new StrategyState<State,Integer>(state, guaranteeId, lazyness);
					
					int nextMemoryToConsider = this.getNextGuaranteeStrategy(guaranteeId, state);
					
					boolean goalIsSatisfied = getGRGoal().getGuarantee(guaranteeId).contains(state);
					
					//If either a guarantee 
					//or a failure was just visited then it is ok for the successor of state to have higher rank.
					boolean rankMayIncrease =  goalIsSatisfied 
							|| getGRGoal().getFailures().contains(state);
					
					boolean lazynessCanDecrease = source.getLazyness() > 0;
					
					
					Set<StrategyState<State,Integer>> successors = new HashSet<StrategyState<State,Integer>>();
					
					this.addUncontrollableSuccesors(state, source, nextMemoryToConsider, rankMayIncrease, successors, lazynessCanDecrease, goalIsSatisfied);
					
					this.addControllableSuccesors(state, source, nextMemoryToConsider, rankMayIncrease, successors, lazynessCanDecrease, goalIsSatisfied);
					Validate.notEmpty(successors, "\n State:" + source + " should have at least one successor.");
					
					result.addSuccessors(source, successors);
				}
			}
		}
		return result;
	}
	
	protected void addUncontrollableSuccesors(State state, StrategyState<State, Integer> source, 
			int nextMemoryToConsider, boolean rankMayIncrease, Set<StrategyState<State, Integer>> successors, boolean lazynessCanDecrease, boolean goalIsSatisfied) {
		for (State succ : this.getGame().getUncontrollableSuccessors(state)) {
			boolean isBetterThan = this.isBetterThan(source, new StrategyState<State,Integer>(succ, nextMemoryToConsider),rankMayIncrease);
			Validate.isTrue(isBetterThan,"State: " + succ + " must have a better rank than state: " + state);
			StrategyState<State,Integer> target;
			if(goalIsSatisfied)
				target = new StrategyState<State,Integer>(succ, nextMemoryToConsider, maxLazyness);
			else
				target = new StrategyState<State,Integer>(succ, nextMemoryToConsider, source.getLazyness());
			
			successors.add(target);
		}
	}
	
	protected void addControllableSuccesors(State state,
			StrategyState<State, Integer> source, int nextMemoryToConsider,
			boolean rankMayIncrease,
			Set<StrategyState<State, Integer>> successors,
			boolean lazynessCanDecrease, boolean goalIsSatisfied) {
		this.addControllableSuccesors(state, source, nextMemoryToConsider, rankMayIncrease, successors, lazynessCanDecrease, goalIsSatisfied, false);
	}

	protected void addControllableSuccesors(State state,StrategyState<State, Integer> source, int nextMemoryToConsider,
			boolean rankMayIncrease, Set<StrategyState<State, Integer>> successaors, boolean lazynessCanDecrease, boolean goalIsSatisfied, boolean base) {
		
		Set<StrategyState<State, Integer>> controllableSuccessors = new HashSet<StrategyState<State,Integer>>();
		
		for (State succ : this.getGame().getControllableSuccessors(state)) {
			if (this.isBetterThan(source,new StrategyState<State,Integer>(succ, nextMemoryToConsider), rankMayIncrease)) {
				Validate.isTrue(this.isWinning(succ), "state: " + succ + " it's not winning.");
				
				StrategyState<State,Integer> target;
				if(goalIsSatisfied)
					target = new StrategyState<State,Integer>(succ, nextMemoryToConsider, maxLazyness);
				else
					target = new StrategyState<State,Integer>(succ, nextMemoryToConsider, source.getLazyness());
				controllableSuccessors.add(target);
			}
			else if(lazynessCanDecrease && this.isWinning(succ)){
				Validate.isTrue(this.isWinning(succ), "state: " + succ + " it's not winning.");
				StrategyState<State,Integer> target = new StrategyState<State,Integer>(succ, nextMemoryToConsider, source.getLazyness()-1);
				controllableSuccessors.add(target);
			}		
			else if (getGRGoal().buildPermissiveStrategy() && this.isWinning(succ)) {
				StrategyState<State,Integer> target = new StrategyState<State,Integer>(succ, nextMemoryToConsider);
				controllableSuccessors.add(target);		
				this.getWorseRank().add(new Pair<StrategyState<State,Integer>,StrategyState<State,Integer>>(source, target));
			}
		}
		
		successaors.addAll(controllableSuccessors);
		
	}
	
}
