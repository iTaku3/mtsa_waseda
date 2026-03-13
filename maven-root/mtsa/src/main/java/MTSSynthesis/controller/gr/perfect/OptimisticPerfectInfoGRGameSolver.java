package MTSSynthesis.controller.gr.perfect;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSSynthesis.controller.gr.GRRank;
import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.gr.GRGame;


/**
 * The diference between OptimisticPerfectInfoGRGameSolver and PerfectInfoGRGameSolver
 * is that when rankMayIncrease (satisfies the actual goal) do not add all succesors, 
 * just the better candidates in GRRank.
 * 
 * @author ecastellano
 *
 * @param <State>
 */
public class OptimisticPerfectInfoGRGameSolver<State> extends PerfectInfoGRGameSolver<State> {

	public OptimisticPerfectInfoGRGameSolver(GRGame<State> game,GRRankSystem<State> rankSystem) {
		super(game, rankSystem);
	}
	
	protected void addControllableSuccesors(State state,StrategyState<State, Integer> source, int nextMemoryToConsider,
			boolean rankMayIncrease, Set<StrategyState<State, Integer>> successors) {
		Set<StrategyState<State, Integer>> candidates = new HashSet<StrategyState<State, Integer>>();
		Set<State> succs = this.getGame().getControllableSuccessors(state);
		for (State succ : succs) {
			if (this.isBetterThan(source,new StrategyState<State,Integer>(succ, nextMemoryToConsider), rankMayIncrease)) {
				Validate.isTrue(this.isWinning(succ), "state: " + succ + " it's not winning.");
				StrategyState<State,Integer> target = new StrategyState<State,Integer>(succ, nextMemoryToConsider);
				if(rankMayIncrease)
					updateBestCandidates(target, candidates);
				else
					candidates.add(target);
			} 
			else if (getGRGoal().buildPermissiveStrategy() && this.isWinning(succ)) {
				StrategyState<State,Integer> target = new StrategyState<State,Integer>(succ, nextMemoryToConsider);
				successors.add(target);		
				this.getWorseRank().add(new Pair<StrategyState<State,Integer>,StrategyState<State,Integer>>(source, target));
			}
		}
		successors.addAll(candidates);
	}
	
	private void updateBestCandidates(StrategyState<State, Integer> newCandidate, Set<StrategyState<State, Integer>> bestCandidates) {
		boolean better = bestCandidates.isEmpty();
		Set<StrategyState<State, Integer>> removableCandidates = new HashSet<StrategyState<State, Integer>>();
		for (StrategyState<State, Integer> candidate : bestCandidates) {
			int compare = this.compareCandidates(newCandidate, candidate);
			if(compare > 0){
				removableCandidates.add(candidate);
				better = true;
			}else if(compare ==0){
				better = true;
			}
		}
		
		bestCandidates.removeAll(removableCandidates);
		
		if(better)
			bestCandidates.add(newCandidate);
	}
	
	private int compareCandidates(StrategyState<State, Integer> newCandidate,
			StrategyState<State, Integer> candidate) {
		GRRank newRank = this.getRankSystem().getRank(newCandidate);
		GRRank oldRank = this.getRankSystem().getRank(candidate);
		return oldRank.compareTo(newRank);
	}

}
