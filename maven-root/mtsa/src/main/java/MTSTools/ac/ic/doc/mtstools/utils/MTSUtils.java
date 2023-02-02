package MTSTools.ac.ic.doc.mtstools.utils;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.MAYBE;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.Predicate;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;
import MTSTools.ac.ic.doc.mtstools.model.MTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.MTSTransition;
import MTSTools.ac.ic.doc.mtstools.model.impl.LinkedListMTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSTransitionImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSTransitionWithModalityInfo;

/**
* This class consists exclusively of static methods that operate on or return
* MTS and LTS.
*/
public class MTSUtils<S, A>  {

	/**
	 * Returns true if <code>mts</code> has tau transitions
	 * TODO: REFACTOR encapsulate this in a Predicate 
	 */
	public static <State, Action> boolean hasTauTransitions(MTS<State, Action> mts) {
		for (State state : mts.getStates()) {
			for (Pair<Action, State> transition : mts.getTransitions(state, TransitionType.POSSIBLE)) {
				if (transition.getFirst().equals(MTSConstants.TAU)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 *  TODO: REFACTOR encapsulate this in a predicate
	 *  
	 *	Returns true if <code>mts</code> has no outgoing 
	 *	transitions from state <code>stateFrom</code> with <code>transitionType</code> 
	 *	type.  
	 */
	public static <State, Action> boolean stateWithoutOutgoingChoices(
				MTS<State, Action> mts, State stateFrom, TransitionType transitionType) {
		return mts.getTransitions(stateFrom, transitionType).size()<=1;
	}
	
	/**
	 */
	public static <State, Action> void hide(MTS<State, Action> mts, Set<Action> silentActions, Action internalAction) {
		for (Action action : silentActions) {
			for (State from : mts.getStates()) {
				for (State to : mts.getTransitions(from, TransitionType.REQUIRED).getImage(action)) {
					mts.removeRequired(from, action, to);
					mts.addRequired(from, internalAction, to);
				}
			}
		}
	}
	
	public static <State, Action> void removeSilentTransitions(MTS<State, Action> mts, Set<Action> toHide) {
		for (Action action : toHide) {
			removeSilentTransitions(mts, action);
		}
	}

	public static <State, Action> void removeSilentTransitions(MTS<State, Action> mts, Action toHide) {
		Set<MTSTransition<Action, State>> toDelete = new HashSet<MTSTransition<Action, State>>();
		
		for (State from : mts.getStates()) {
			Set<State> statesTo = mts.getTransitions(from, TransitionType.POSSIBLE).getImage(toHide);
			for (State stateTo : statesTo) {
				toDelete.add(MTSTransitionImpl.createMTSEventState(from, toHide, stateTo));
			}
		}
		for (MTSTransition<Action, State> transition : toDelete) {
			mts.removePossible(transition.getStateFrom(), transition.getEvent(), transition.getStateTo());
		}
		mts.removeUnreachableStates();
	}

	/**
	 * Returns a new MTS, which is a clone (shallow copy) of the given mts. 
	 * @param <S>
	 * @param <A>
	 * @param mts
	 * @return
	 */
	public static <S,A> MTS<S,A> cloneMTS(MTS<S,A> mts) {
		MTS<S,A> result = new MTSImpl<S, A>(mts.getInitialState());
		result.addActions(mts.getActions());
		result.addStates(mts.getStates());
		
		for(TransitionType type: TransitionType.values()) {
			if (MAYBE.equals(type)) continue;
			for(S state: mts.getStates()) {
				for(Pair<A,S> transition: mts.getTransitions(state, type)) {
					result.addTransition(
							state,
							transition.getFirst(),
							transition.getSecond(),
							type);					
				}
			}
				
		}
		return result;
	}

	/**
	 * TODO: remove from here and create a Predicate
	 * Returns true if the MTS has no transitions
	 * @param mts
	 * @return
	 */
	public static <S, A> boolean isEmpty(MTS<S, A> mts) {
		return mts.getTransitions(mts.getInitialState(), TransitionType.POSSIBLE).isEmpty();
	}
	
	/**
	 * For each state satisfying the predicate there is exactly one
	 * trace leading to such state in the result. The trace is minimal in the sense that there are no repeated states (i.e. no loops).
	 * Note that there could be other traces leading to the same states that are not part of the result. 
	 *   
	 * @param mts
	 * @param predicateOnState
	 * @return
	 */
	public static <S, A> Set<MTSTrace<A, S>> getAlltracesToStatesSatisfyingPredicate(MTS<S, A> mts, Predicate<S> predicateOnState, TransitionType transitionType) {
		Set<S> visited = new HashSet<S>(); 
		return buildTracesToStateSatisfyingPredicateAux(mts.getInitialState(), mts, visited, predicateOnState, transitionType);
	}	

	/**
	 * 
	 * @param currentState
	 * @param mts
	 * @param traces
	 * @param visited
	 * @param predicateOnState
	 * @return
	 */
	private static <S, A> Set<MTSTrace<A, S>> buildTracesToStateSatisfyingPredicateAux(S currentState, MTS<S, A> mts, Set<S> visited, Predicate<S> predicateOnState, 
			TransitionType transitionType) {
		
		Set<MTSTrace<A, S>> result = new HashSet<MTSTrace<A, S>>();
		visited.add(currentState);
		
		// For each transition from crurrentState build traces to states satisfying predicate
		Iterator<Pair<A, S>> transitionsIt = mts.getTransitions(currentState, transitionType).iterator();
		while (transitionsIt.hasNext()) {
			Pair<A, S> transition = transitionsIt.next();
			S nextState = transition.getSecond();

			// only build traces to next states that have not been visited
			if ( !visited.contains(nextState) ) {
				
				boolean isMaybeTransition;
				if(mts.getTransitions(currentState, TransitionType.MAYBE).contains(transition)) {
					isMaybeTransition = true;
				} else {
					isMaybeTransition = false;
				}
				MTSTransition<A, S> transitionFromCurrentStateToNextState = new MTSTransitionWithModalityInfo<A,S>(currentState, transition.getFirst(), nextState, isMaybeTransition);
				// Get the traces to states satisfying the predicate starting at the next state
				Set<MTSTrace<A, S>> tracesToStateSatisfyingPredicateFromNextState = 
						buildTracesToStateSatisfyingPredicateAux(nextState, mts, visited, predicateOnState, transitionType);
				
				// add the transition to the first position of each of the tracesToStateSatisfyingPredicateFromNextState and add it to the final result
				appendTransitionToTracesAndAddToResult(transitionFromCurrentStateToNextState, tracesToStateSatisfyingPredicateFromNextState, result);
			}
		}
		
		if (predicateOnState.evaluate(currentState)) {
			// Current state satisfies the predicate so the empty trace is added to the result.
			result.add(new LinkedListMTSTrace<A, S>()); // LinkedList implementation is used to optimise transition appending at the first position of the trace
		}
		
		return result;
	}

	private static <S, A> void appendTransitionToTracesAndAddToResult(MTSTransition<A, S> transitionCurrentStateNextState, 
			Set<MTSTrace<A, S>> tracesToStateSatisfyingPredicateFromNextState, Set<MTSTrace<A, S>> result) {
		
		for (MTSTrace<A,S> traceToStateSatisfyingPredicateFromNextState : tracesToStateSatisfyingPredicateFromNextState) {
			traceToStateSatisfyingPredicateFromNextState.addFirst(transitionCurrentStateNextState);
			result.add(traceToStateSatisfyingPredicateFromNextState);
		}
	}

}
