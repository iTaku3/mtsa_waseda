package MTSSynthesis.controller.parity;

import java.util.Map;
import java.util.Set;

import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.Rank;
import MTSSynthesis.controller.model.RankSystem;

public class ParityRankSystem<State> implements RankSystem<State, Integer> {
	Map<State, ParityRank> ranks;
	Map<Integer, Integer> maximalValues;
	Integer maxPriority;
	
	public ParityRankSystem(ParityGame<State> game) {
		game.getStates();
	}

	public Integer max(Integer index) {
		return this.maximalValues.get(index);
	}

	@Override
	public ParityRank getMax(Set<StrategyState<State,Integer>> states) {
		ParityRank max = new ParityRank(0);	
		for (StrategyState<State,Integer> state : states) {
			ParityRank rank = this.getRank(state);
			if (rank.compareTo(max) > 0)
				max = rank;
		}
		return max;
	}

	@Override
	public ParityRank getMin(Set<StrategyState<State,Integer>> states) {
		ParityRank min = new ParityRank(this.maxPriority);	
		for (StrategyState<State,Integer> state : states) {
			ParityRank rank = this.getRank(state);
			if (rank.compareTo(min) < 0)
				min = rank;
		}
		return min;
	}

	@Override
	public void increase(StrategyState<State,Integer> state) {
		throw new RuntimeException("Increase a ParityRank is not implemented.");
	}

	@Override
	public boolean isInfinity(StrategyState<State,Integer> state) {
		return this.getRank(state).isInfinity();
				
	}

	@Override
	public void set(StrategyState<State,Integer> state, Rank rank) {
		this.ranks.put(state.getState(), (ParityRank) rank);
	}

	@Override
	public ParityRank getRank(StrategyState<State,Integer> state) {
		return this.ranks.get(state.getState());
	}
	

}
