package MTSSynthesis.controller.gr;

import MTSSynthesis.controller.model.Rank;
import MTSSynthesis.controller.model.RankContext;
import MTSSynthesis.controller.model.RankFunction;
import MTSSynthesis.controller.model.RankSystem;
import org.apache.commons.lang.Validate;

import java.util.Set;

//TODO: StrategyState
public abstract class AbstractGRRankSystem<State, Memory> implements RankSystem<State, Memory> {
    protected abstract RankFunction<State> getRankFunction(Memory memory);

    @Override
    public void increase(StrategyState<State, Memory> strategyState) {
        this.getRankFunction(strategyState.getMemory()).increase(strategyState.getState());
    }

    @Override
    public boolean isInfinity(StrategyState<State, Memory> strategyState) {
        return this.getRankFunction(strategyState.getMemory()).isInfinity(strategyState.getState());
    }

    @Override
    public void set(StrategyState<State, Memory> strategyState, Rank rank) {
        RankFunction<State> rankFunction = this.getRankFunction(strategyState.getMemory());
        rankFunction.setRank(strategyState.getState(), rank);
    }


    @Override
    public Rank getRank(StrategyState<State, Memory> strategyState) {
        RankFunction<State> rankFunction = this.getRankFunction(strategyState.getMemory());
        assert rankFunction != null;
        return rankFunction.getRank(strategyState.getState());
    }

    public RankContext getContext(StrategyState<State, Memory> strategyState) {
        return this.getRankFunction(strategyState.getMemory()).getContext();
    }

    @Override
    public Rank getMax(Set<StrategyState<State, Memory>> strategyStates) {
        //assuming that all strategy states have the same guarantee
        Validate.notEmpty(strategyStates);
        RankFunction<State> rankFunction = this.getRankFunction(strategyStates.iterator().next().getMemory());
        return rankFunction.getMaximum(GRUtils.getStates(strategyStates));
    }

    @Override
    public Rank getMin(Set<StrategyState<State, Memory>> strategyStates) {
        //assuming that all strategy states have the same guarantee
        Validate.notEmpty(strategyStates);
        RankFunction<State> rankFunction = this.getRankFunction(strategyStates.iterator().next().getMemory());
        return rankFunction.getMinimum(GRUtils.getStatesFrom(strategyStates));
    }


}