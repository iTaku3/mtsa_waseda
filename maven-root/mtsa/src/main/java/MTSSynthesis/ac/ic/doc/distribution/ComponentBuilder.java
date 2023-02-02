package MTSSynthesis.ac.ic.doc.distribution;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSSynthesis.ac.ic.doc.distribution.model.ComponentBuiltFromDistribution;
import MTSSynthesis.ac.ic.doc.distribution.model.ComponentBuiltFromDistributionImpl;
import MTSSynthesis.ac.ic.doc.distribution.model.DeterminisationModalityMismatch;
import MTSSynthesis.ac.ic.doc.distribution.model.DeterminisationModalityMismatchImpl;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;

/**
 * TODO add comment
 * @author gsibay
 *
 */
public class ComponentBuilder<S, A> {

	
	public ComponentBuiltFromDistribution<S, A> buildComponent(MTS<S, A> m, Set<A> componentAlphabet, Set<A> fullAlphabet) {
		
		Set<DeterminisationModalityMismatch<S, A>> mismatches = new HashSet<DeterminisationModalityMismatch<S,A>>();
		
		// silentActions are the ones not in the component alphabet
		Set<A> silentActions = new HashSet<A>(fullAlphabet);
		silentActions.removeAll(componentAlphabet); 
		
		S currentState = m.getInitialState();
		
		// the initial state of the composed model is the closure of the initial state of the monolithic one
		Set<S> closuredInitialState = this.getClosure(Collections.singleton(currentState), silentActions, m);

		MTS<Set<S>, A> component = new MTSImpl<Set<S>, A>(closuredInitialState);
	
		
		Set<Set<S>> unprocessedClosuredStates = new HashSet<Set<S>>();
		Set<Set<S>> processedClosuredStates = new HashSet<Set<S>>();
		
		unprocessedClosuredStates.add(closuredInitialState);
		
		Set<S> closuredState;
		Set<S> nextClosuredState;
		while(!unprocessedClosuredStates.isEmpty()) {
			// get the next state to process
			closuredState = unprocessedClosuredStates.iterator().next();
			
			// move it from the unprocessed to the processed states
			unprocessedClosuredStates.remove(closuredState);
			processedClosuredStates.add(closuredState);
			
			// Create the componentCurrentState and add the transitions
			for (A action : componentAlphabet) {

				// The required and maybe transitions are on "action"
				Map<S, Set<S>> reqTransitionsFromStateOnAction = new HashMap<S, Set<S>>();
				Map<S, Set<S>> mayTransitionsFromStateOnAction = new HashMap<S, Set<S>>();
				
				for (S s : closuredState) {	
					// The required and maybe transitions are on "action" 
					reqTransitionsFromStateOnAction.put(s, this.calculateTransitionsFromState(m, s, action, TransitionType.REQUIRED));
					mayTransitionsFromStateOnAction.put(s, this.calculateTransitionsFromState(m, s, action, TransitionType.MAYBE));
				}	
				
				// calculate and add the mismatches for this action and closuredState
				mismatches.addAll(this.getModalityMissmatches(closuredState, action, reqTransitionsFromStateOnAction, mayTransitionsFromStateOnAction));
				
				// get next state for action (get the inmediate next states and then closure them)
				nextClosuredState = new HashSet<S>();
				HashSet<S> nextStatesFollowingAction = new HashSet<S>();
				nextStatesFollowingAction.addAll(this.getTargetStates(reqTransitionsFromStateOnAction));
				nextStatesFollowingAction.addAll(this.getTargetStates(mayTransitionsFromStateOnAction));
				nextClosuredState.addAll(this.getClosure(nextStatesFollowingAction, silentActions, m));
				
				if(!nextClosuredState.isEmpty()) { // there is a next state
					if(!processedClosuredStates.contains(nextClosuredState)){
						// it is a new state not yet processed
						unprocessedClosuredStates.add(nextClosuredState);
						component.addState(nextClosuredState);
					}
	
					component.addAction(action);
					
					TransitionType type;
					
					if(this.allEmpty(reqTransitionsFromStateOnAction)){
						type = TransitionType.MAYBE;
					} else { // there is at least one required transition on action so when determinised its modality is set to required
						type = TransitionType.REQUIRED;
					}
					component.addTransition(closuredState, action, nextClosuredState, type);
				}
				
			}
		
		}
		
		return new ComponentBuiltFromDistributionImpl<A, S>(component, mismatches);
	}


	private boolean allEmpty(Map<S, Set<S>> reqTransitionsFromStateOnAction) {
		boolean allEmpty = true;
		
		Set<Entry<S, Set<S>>> entrySet = reqTransitionsFromStateOnAction.entrySet();
		for (Entry<S, Set<S>> entry : entrySet) {
			allEmpty = entry.getValue().isEmpty() && allEmpty;
		}
		return allEmpty;
	}


	/**
	 * flats the map and get the target state of all the transitions
	 * @param transitionsFromState
	 * @return
	 */
	private Set<S> getTargetStates(Map<S, Set<S>> transitionsFromState) {
		Set<S> result = new HashSet<S>();
		Set<Entry<S, Set<S>>> entrySet = transitionsFromState.entrySet();
		for (Entry<S, Set<S>> entry : entrySet) {
			result.addAll(entry.getValue());
		}
		return result;
	}


	/**
	 * reqTransitionsFromState and 
	 * mayTransitionsFromState have to be transitions on action
	 * 
	 * @param closuredCurrentState
	 * @param action
	 * @param reqTransitionsFromStateOnAction
	 * @param mayTransitionsFromStateOnAction
	 * @return
	 */
	private Set<DeterminisationModalityMismatch<S, A>> getModalityMissmatches(
			Set<S> closuredCurrentState, A action,
			Map<S, Set<S>> reqTransitionsFromStateOnAction,
			Map<S, Set<S>> mayTransitionsFromStateOnAction) {
		Set<DeterminisationModalityMismatch<S, A>> result = new HashSet<DeterminisationModalityMismatch<S,A>>();
		 
		//check from every state if there is a mismatch with anotherState
		for (S state : closuredCurrentState) {
			// if from state there is a required transition on action there could be a mismatch
			if(!reqTransitionsFromStateOnAction.get(state).isEmpty()) {
				// check all other states for a mismatch
				for (S anotherState : closuredCurrentState) {
					// if anotherState has a maybe transition then there is a mismatch
					if(!anotherState.equals(state) && !mayTransitionsFromStateOnAction.get(anotherState).isEmpty()) {
						result.add(new DeterminisationModalityMismatchImpl<S, A>(state, anotherState, action));
					}
				}
			}
		}
		return result;
	}


	/**
	 * Gets the set of target state
	 * that goes from state s by the parameter action and by transitionType.
	 * @param m
	 * @param s
	 * @param action
	 * @param transitionType
	 * @return
	 */
	private Set<S> calculateTransitionsFromState(MTS<S, A> m, S s, A action, TransitionType transitionType) {
		Set<S> result = new HashSet<S>();
		
		// gets all transitions from s with the desired type
		BinaryRelation<A, S> transitionsByType = m.getTransitions(s, transitionType);
		
		// filters transitions that are not by the desired action
		for (Pair<A, S> pair : transitionsByType) {
			if(action.equals(pair.getFirst())) {
				result.add(pair.getSecond());
			}
		}
		return result;
	}


	private Set<S> getClosure(Set<S> initialSetOfStates, Set<A> silentActions, MTS<S, A> mts) {
		Set<S> unprocessedStates = new HashSet<S>();
		Set<S> processedStates = new HashSet<S>();
		unprocessedStates.addAll(initialSetOfStates);
		
		while (!unprocessedStates.isEmpty()) {
			S state = unprocessedStates.iterator().next();
			unprocessedStates.remove(state);
			processedStates.add(state);
			for (A silentAction : silentActions) {
				BinaryRelation<A, S> transitions = mts.getTransitions(state, TransitionType.POSSIBLE);
				Set<S> targetStates = transitions.getImage(silentAction);
				for (S targetState : targetStates) {
					if(!processedStates.contains(targetState)) {
						unprocessedStates.add(targetState);
					}
				}
			}
		}
		return processedStates;	
	}
}
