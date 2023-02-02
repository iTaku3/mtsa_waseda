package MTSSynthesis.controller.util;

import java.util.Set;

import MTSSynthesis.controller.model.Strategy;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSSynthesis.controller.gr.StrategyState;

//TODO: Change to GameStrategyToLTSBuilder
public class GameStrategyToMTSBuilder {
	private static final String WORSE_PREFIX = "#w#_";
	private static GameStrategyToMTSBuilder instance = new GameStrategyToMTSBuilder();
	public static GameStrategyToMTSBuilder getInstance() { return instance; }
	private GameStrategyToMTSBuilder() {}
	
	
	public <State, Action> MTS<StrategyState<State, Integer>, Action> buildMTSFrom(MTS<State, Action> mts, 
			Strategy<State, Integer> strategy
			//please remove!!!
			, Set<Pair<StrategyState<State, Integer>, StrategyState<State, Integer>>> worseRank
			) {
		return buildMTSFrom(mts, strategy, worseRank,0);
	}
	
	public <State, Action> MTS<StrategyState<State, Integer>, Action> buildMTSFrom(MTS<State, Action> mts, 
			Strategy<State, Integer> strategy 
			//please remove!!!
			, Set<Pair<StrategyState<State, Integer>, StrategyState<State, Integer>>> worseRank, Integer maxLazyness
			) {
		
		StrategyState<State, Integer> initialState = new StrategyState<State,Integer>(mts.getInitialState(), 1, maxLazyness);
		MTS<StrategyState<State, Integer>, Action> result = new MTSImpl<StrategyState<State,Integer>, Action>(initialState);
		result.addStates(strategy.getStates());
		result.addActions(mts.getActions());
		
		for (StrategyState<State, Integer> strategyState : strategy.getStates()) {
			for (Pair<Action, State> transition : mts.getTransitions(strategyState.getState(), TransitionType.REQUIRED)) {
	
				State to = transition.getSecond();

				StrategyState<State, Integer> rankedState = this.getRankedState(to, strategy.getSuccessors(strategyState));
				if (rankedState!=null) {
					if (!result.getStates().contains(rankedState)) {
						throw new RuntimeException("The strategy built has dead end states. \n State "+ rankedState + " is a deadlock state." );
					} 
					
					if (worseRank.contains(Pair.create(strategyState, rankedState))) {
						@SuppressWarnings("unchecked")
						Action concat = (Action)WORSE_PREFIX.concat(transition.getFirst().toString());
						result.addAction(concat);
						result.addRequired(strategyState, concat, rankedState);
					} else {
						result.addRequired(strategyState, transition.getFirst(), rankedState);
					}
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
