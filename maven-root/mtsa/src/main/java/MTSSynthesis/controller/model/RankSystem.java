package MTSSynthesis.controller.model;

import java.util.Set;

import MTSSynthesis.controller.gr.StrategyState;


public interface RankSystem<State,Memory> {

	public abstract Rank getMax(Set<StrategyState<State,Memory>> strategyStates);

	public abstract Rank getMin(Set<StrategyState<State,Memory>> strategyStates);

	public abstract void increase(StrategyState<State,Memory> strategyState);

	public abstract boolean isInfinity(StrategyState<State,Memory> strategyState);

	public abstract  void set(StrategyState<State,Memory> strategyState, Rank rank);

	public abstract Rank getRank(StrategyState<State,Memory> strategyState);

}