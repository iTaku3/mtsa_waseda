package MTSSynthesis.controller.gr;

import java.util.HashSet;
import java.util.Set;

public class GRUtils {

	//TODO: Strategy State
	public static <State,Memory> Set<State> getStatesFrom(Set<StrategyState<State, Memory>> strategyStates) {
		Set<State> states = new HashSet<State>();
		for (StrategyState<State, Memory> strategyState : strategyStates) {
			states.add(strategyState.getState());
		}
		return states;
	}

	public static <State,Memory> Set<State> getStates(Set<StrategyState<State, Memory>> strategyStates) {
		Set<State> states = new HashSet<State>();
		for (StrategyState<State, Memory> strategyState : strategyStates) {
			states.add(strategyState.getState());
		}
		return states;
	}
	
	public static <State, Memory> Set<StrategyState<State, Memory>> getStrategyStatesFrom(Memory guarantee, Set<State> states) {
		Set<StrategyState<State, Memory>> result = new HashSet<StrategyState<State,Memory>>();
		for (State state : states) {
			result.add(new StrategyState<State, Memory>(state, guarantee));
		}
		return result;
	}
}
