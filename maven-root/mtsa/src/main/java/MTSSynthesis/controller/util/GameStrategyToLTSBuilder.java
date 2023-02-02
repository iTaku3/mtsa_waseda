package MTSSynthesis.controller.util;

import java.util.Set;

import MTSSynthesis.controller.model.Strategy;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSImpl;
import MTSSynthesis.controller.gr.StrategyState;

//TODO: Change to GameStrategyToLTSBuilder
public class GameStrategyToLTSBuilder {
	private static GameStrategyToLTSBuilder instance = new GameStrategyToLTSBuilder();
	public static GameStrategyToLTSBuilder getInstance() { return instance; }
	private GameStrategyToLTSBuilder() {}

	/**
	 * Subtract the LTS out of the provided structure (lts) according to a strategy
	 * @param lts the initial structure to be refined according to the strategy
	 * @param strategy the strategy built from a previously defined game
	 * @return the LTS subtracted out of the provided structure (lts) according to a strategy
	 */
	public <State, Action> LTS<StrategyState<State, Integer>, Action> buildLTSFrom(LTS<State, Action> lts, 
			Strategy<State, Integer> strategy) {
		StrategyState<State, Integer> initialState = new StrategyState<State,Integer>(lts.getInitialState(), 1);
		LTS<StrategyState<State, Integer>, Action> result = new LTSImpl<StrategyState<State,Integer>, Action>(initialState);
		result.addStates(strategy.getStates());
		result.addActions(lts.getActions());
		
		for (StrategyState<State, Integer> strategyState : strategy.getStates()) {
			for (Pair<Action, State> transition : lts.getTransitions(strategyState.getState())) {
	
				State to = transition.getSecond();

				StrategyState<State, Integer> rankedState = this.getRankedState(to, strategy.getSuccessors(strategyState));
				if (rankedState!=null) {
					if (!result.getStates().contains(rankedState)) {
						throw new RuntimeException("The strategy built has dead end states. \n State "+ rankedState + " is a deadlock state." );
					} 
					
					result.addTransition(strategyState, transition.getFirst(), rankedState);
				}
			}
		}
		
		result.removeUnreachableStates();
	
		return result;
	}	
	
	public <State> StrategyState<State, Integer> getRankedState(State state, Set<StrategyState<State, Integer>> successors) {
		for (StrategyState<State, Integer> strategyState : successors) {
			if (strategyState.getState().equals(state)) {
				return strategyState;
			}
		}
		return null;
	}
}
