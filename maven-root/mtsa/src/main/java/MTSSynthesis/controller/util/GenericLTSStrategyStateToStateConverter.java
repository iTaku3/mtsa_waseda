package MTSSynthesis.controller.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import MTSSynthesis.controller.gr.StrategyState;
 
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSImpl;


/**
 * This class converts a generic LTS into a LTS<Long,String>. It was created in order to 
 * adapt generic LTS into LTS<Long,String> since some MTSA functions handle only this kind of LTSs.
 * The actions are transformed using the toString() method, and the states are numbered from 0 starting
 * from the initial state.
 * @author mcerrutti
 *
 */
public class GenericLTSStrategyStateToStateConverter<S, A, M> implements Transformer<LTS<StrategyState<S,M>,A>, LTS<S, A>>{
  
  private Map<StrategyState<S,M>,S> mapping;
  
	@Override
	public LTS<S,A> transform(LTS<StrategyState<S,M>,A> lts) {		
		Map<StrategyState<S,M>,S> stateMapping = this.createStateMapping(lts);
		mapping = stateMapping;
				
		LTS<S,A> result = new LTSImpl<S, A>(stateMapping.get(lts.getInitialState()));
		result.addStates(stateMapping.values());
		for(A action: lts.getActions()) {
			result.addAction(action);
		}
		
		for(StrategyState<S,M> state: lts.getStates()) {
			for(Pair<A,StrategyState<S,M>> transition: lts.getTransitions(state)) {
				result.addTransition(
						stateMapping.get(state),
						transition.getFirst(),
						stateMapping.get(transition.getSecond()));					
			}
		}
				
		return result;		
	}
	
	private Map<StrategyState<S,M>,S> createStateMapping(LTS<StrategyState<S,M>,A> lts) {
		Map<StrategyState<S,M>,S> result = new HashMap<StrategyState<S,M>,S>();
		result.put(lts.getInitialState(), lts.getInitialState().getState());
		for(StrategyState<S,M> state: lts.getStates()) {
			if (!result.containsKey(state.getState())) {
				result.put(state, state.getState());
			}
		}
		return result;
	}

	/*
	 * Method needed to get mapping from environment/plant/game state numbers to controller state numbers,
	 * used elsewhere in MDP translation
	 */
	public Map<StrategyState<S,M>,S> getStateMapping()
	{
	  return mapping;
	}
}
