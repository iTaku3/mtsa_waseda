package MTSSynthesis.controller.model;

import MTSSynthesis.controller.gr.StrategyState;
import java.util.Set;

public interface RankSystem<State, Memory> {

  public abstract Rank getMax(Set<StrategyState<State, Memory>> strategyStates);

  public abstract Rank getMin(Set<StrategyState<State, Memory>> strategyStates);

  public abstract void increase(StrategyState<State, Memory> strategyState);

  public abstract boolean isInfinity(StrategyState<State, Memory> strategyState);

  public abstract void set(StrategyState<State, Memory> strategyState, Rank rank);

  public abstract Rank getRank(StrategyState<State, Memory> strategyState);
}
