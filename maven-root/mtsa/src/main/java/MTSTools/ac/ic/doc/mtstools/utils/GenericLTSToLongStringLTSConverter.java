package MTSTools.ac.ic.doc.mtstools.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
 
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
public class GenericLTSToLongStringLTSConverter<S, A> implements Transformer<LTS<S,A>, LTS<Long, String>>{
  
  private Map<S,Long> mapping;
  
	@Override
	public LTS<Long,String> transform(LTS<S,A> lts) {		
		Map<S,Long> stateMapping = this.createStateMapping(lts);
		mapping = stateMapping;
				
		LTS<Long,String> result = new LTSImpl<Long, String>(stateMapping.get(lts.getInitialState()));
		result.addStates(stateMapping.values());
		for(A action: lts.getActions()) {
			result.addAction(action.toString());
		}
		
		for(S state: lts.getStates()) {
			for(Pair<A,S> transition: lts.getTransitions(state)) {
				result.addTransition(
						stateMapping.get(state),
						transition.getFirst().toString(),
						stateMapping.get(transition.getSecond()));					
			}
		}
				
		return result;		
	}
	
	private Map<S,Long> createStateMapping(LTS<S,?> lts) {
		Map<S,Long> result = new HashMap<S, Long>();
		Long nextValue = 0L;
		result.put(lts.getInitialState(), nextValue++);
		for(S state: lts.getStates()) {
			if (!result.containsKey(state)) {
				result.put(state, nextValue++);
			}
		}
		return result;
	}

	/*
	 * Method needed to get mapping from environment/plant/game state numbers to controller state numbers,
	 * used elsewhere in MDP translation
	 */
	public Map<S,Long> getStateMapping()
	{
	  return mapping;
	}
}
