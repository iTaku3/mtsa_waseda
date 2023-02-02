package MTSSynthesis.controller.gr.lazy;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.RandomUtils;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.gr.GRGame;

public class LazyRandomGRGameSolver<State> extends LazyGRGameSolver<State> {

	public LazyRandomGRGameSolver(GRGame<State> game, GRRankSystem<State> rankSystem) {
		super(game, rankSystem);
	}
	
	public LazyRandomGRGameSolver(GRGame<State> game, GRRankSystem<State> rankSystem, Integer maxLazyness) {
		super(game, rankSystem, maxLazyness);
	}
	
	@Override
	protected void addControllableSuccesors(State state,StrategyState<State, Integer> source, int nextMemoryToConsider,
			boolean rankMayIncrease, Set<StrategyState<State, Integer>> successors, boolean lazynessCanDecrease, boolean goalIsSatisfied) {
		
		Set<StrategyState<State, Integer>> possibleSuccessors = new HashSet<StrategyState<State, Integer>>();
		for (State succ : this.getGame().getControllableSuccessors(state)) {
			if (this.isBetterThan(source,new StrategyState<State,Integer>(succ, nextMemoryToConsider), rankMayIncrease)) {
				Validate.isTrue(this.isWinning(succ), "state: " + succ + " it's not winning.");
				
				StrategyState<State,Integer> target;
				if(goalIsSatisfied)
					target = new StrategyState<State,Integer>(succ, nextMemoryToConsider, this.getMaxLazyness());
				else
					target = new StrategyState<State,Integer>(succ, nextMemoryToConsider, source.getLazyness());
				
				possibleSuccessors.add(target);
			}
			else if(lazynessCanDecrease && this.isWinning(succ)){
				Validate.isTrue(this.isWinning(succ), "state: " + succ + " it's not winning.");
				StrategyState<State,Integer> target = new StrategyState<State,Integer>(succ, nextMemoryToConsider, source.getLazyness()-1);
				possibleSuccessors.add(target);
			}		
			else if (getGRGoal().buildPermissiveStrategy() && this.isWinning(succ)) {
				StrategyState<State,Integer> target = new StrategyState<State,Integer>(succ, nextMemoryToConsider);
				possibleSuccessors.add(target);		
				this.getWorseRank().add(new Pair<StrategyState<State,Integer>,StrategyState<State,Integer>>(source, target));
			}
		}
		
		for (StrategyState<State, Integer> strategyState : possibleSuccessors) {
			if(RandomUtils.nextBoolean())
				successors.add(strategyState);
		}
		if(successors.isEmpty() && !possibleSuccessors.isEmpty()){
			int toAdd = RandomUtils.nextInt() % possibleSuccessors.size();
			successors.add((StrategyState<State, Integer>) possibleSuccessors.toArray()[toAdd]);
		}
		
	}
	

}
