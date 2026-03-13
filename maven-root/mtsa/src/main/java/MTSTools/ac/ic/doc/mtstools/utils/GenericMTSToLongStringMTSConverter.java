package MTSTools.ac.ic.doc.mtstools.utils;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.MAYBE;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
 
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;

/**
 * This class converts a generic MTS into a MTS<Long,String>. It was created in order to 
 * adapt generic MTS into MTS<Long,String> since some MTSA functions handle only this kind of MTSs.
 * The actions are transformed using the toString() method, and the states are numbered from 0 starting
 * from the initial state.
 * @author fdario
 *
 */
public class GenericMTSToLongStringMTSConverter<S, A> implements Transformer<MTS<S,A>, MTS<Long, String>>{
  
  private Map<S,Long> mapping;
  
	@Override
	public MTS<Long,String> transform(MTS<S,A> mts) {		
		Map<S,Long> stateMapping = this.createStateMapping(mts);
		mapping = stateMapping;
				
		MTS<Long,String> result = new MTSImpl<Long, String>(stateMapping.get(mts.getInitialState()));
		result.addStates(stateMapping.values());
		for(A action: mts.getActions()) {
			result.addAction(action.toString());
		}
		
		for(TransitionType type: TransitionType.values()) {
			if (MAYBE.equals(type)) continue;
			for(S state: mts.getStates()) {
				for(Pair<A,S> transition: mts.getTransitions(state, type)) {
					result.addTransition(
							stateMapping.get(state),
							transition.getFirst().toString(),
							stateMapping.get(transition.getSecond()),
							type);					
				}
			}
				
		}
				
		return result;		
	}
	
	private Map<S,Long> createStateMapping(MTS<S,?> mts) {
		Map<S,Long> result = new HashMap<S, Long>();
		Long nextValue = 0L;
		result.put(mts.getInitialState(), nextValue++);
		for(S state: mts.getStates()) {
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
