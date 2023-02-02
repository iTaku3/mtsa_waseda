package MTSTools.ac.ic.doc.mtstools.model.operations;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.MAYBE;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTSTransition;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.MTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.impl.LinkedListMTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSTransitionImpl;
import MTSTools.ac.ic.doc.mtstools.utils.MTSUtils;

public  class MTSDeadLockManipulatorImpl<State, Action> implements MTSDeadLockManipulator<State, Action> {

	private static final int MIX_DEADLOCK = 3;
	private static final int NONE_DEADLOCK = 2;
	private static final int ALL_DEADLOCK = 1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.ic.doc.mtstools.model.operations.MTSDeadLockManipulator#getTransitionsToDeadlock(ac.ic.doc.mtstools.model.MTS,
	 *      java.util.List)
	 */
	public boolean getTransitionsToDeadlock(MTS<State, Action> mts, MTSTrace<Action, State> traceToDeadlock) {
		traceToDeadlock.clear();
		HashSet<State> visited = new HashSet<State>();
		visited.add(mts.getInitialState());
		return buildTraceToDeadlock(mts.getInitialState(), mts, traceToDeadlock, visited);
	}

	public boolean buildTraceToDeadlock(State actualState, MTS<State, Action> mts,
			MTSTrace<Action, State> traceToDeadlock, Set<State> visited) {

		if (deadlockCandidate(actualState, mts)) {
			return true;
		}

		Iterator<Pair<Action, State>> it = mts.getTransitions(actualState, TransitionType.POSSIBLE).iterator();
		while (it.hasNext()) {
			Pair<Action, State> nextState = it.next();
			if (!visited.contains(nextState.getSecond())) {
				MTSTransition<Action, State> newTransition = MTSTransitionImpl.createMTSEventState(actualState, nextState
						.getFirst(), nextState.getSecond());
				traceToDeadlock.add(newTransition);
				visited.add(actualState);
				if (buildTraceToDeadlock(nextState.getSecond(), mts, traceToDeadlock, visited)) {
					return true;
				} else {
					traceToDeadlock.removeLastTransition();
				}
			}
		}
		return false;
	}

	private boolean deadlockCandidate(State state, MTS<State, Action> mts) {
		return mts.getTransitions(state, TransitionType.POSSIBLE).size() == 0;
		//&& state != MTSConstants.ERROR_STATE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.ic.doc.mtstools.model.operations.MTSDeadLockManipulator#deleteTransitionsToDeadlock(ac.ic.doc.mtstools.model.MTS,
	 *      java.util.List)
	 */
	public void deleteTransitionsToDeadlock(MTS<State, Action> mts) {
		MTSTrace<Action, State> trace = new LinkedListMTSTrace<Action, State>();

		while (this.getTransitionsToDeadlock(mts, trace) && isNotEmptyMTS(mts)) {
			deleteTrace(mts, trace);
		}
		mts.removeUnreachableStates();
	}

	//FIXME: If this method deletes a trace it could be useful to add it to MTSUtils. 
	//FIXME: Change name of method if does not always delete the trace or add a comment. It deletes the trace partially sometimes. Add a comment with the criteria. Could this be added to MTSUtils? 
	private void deleteTrace(MTS<State, Action> mts, MTSTrace<Action, State> trace) {
		for (ListIterator<MTSTransition<Action, State>> it = (ListIterator<MTSTransition<Action, State>>) trace
				.listIterator(trace.size()); it.hasPrevious();) {
			MTSTransition<Action, State> transition = (MTSTransition<Action, State>) it.previous();
			if (mts.getTransitions(transition.getStateTo(), TransitionType.POSSIBLE).isEmpty()) {
				mts.removePossible(transition.getStateFrom(), transition.getEvent(), transition.getStateTo());
			}
		}
	}

	private boolean isNotEmptyMTS(MTS<State, Action> mts) {
		return !mts.getTransitions(mts.getInitialState(), TransitionType.POSSIBLE).isEmpty();
	}
	/**
	 * return 1 if All impls has deadlock, 2 if all impls hasn't, and 3 otherwise 
	 */
	public int getDeadlockStatus(MTS<State, Action> mts) {
		if (checkAllImplementationsWithDeadlock(mts)) {
			return ALL_DEADLOCK;
		} else if (checkAllImplementationsWithoutDeadlock(mts)) {
			return NONE_DEADLOCK;
		} else {
			return MIX_DEADLOCK;
		}
	}

	private boolean checkAllImplementationsWithDeadlock(MTS<State, Action> mts) {
		Set<State> reachableByRequired = buildReachableByRequired(mts);
		for (MTSTrace<Action, State> trace : getAlltracesToDeadlock(mts)) {
			if (unavoidableDeadlock(mts, trace, reachableByRequired)) {
				return true;
			}
		}
		return false;
	}

	private boolean unavoidableDeadlock(MTS<State, Action> mts, MTSTrace<Action, State> trace, Set<State> reachableByRequired) {
		MTSTransition<Action, State> lastTransition = trace.get(trace.size()-1);
		if (reachableByRequired.contains(lastTransition.getStateTo())) {
			return true;
		}
		for (ListIterator<MTSTransition<Action, State>> it = trace.listIterator(trace.size()); it.hasPrevious();) {
			MTSTransition<Action, State> transition = it.previous();
			if (MTSUtils.stateWithoutOutgoingChoices(mts, transition.getStateFrom(), TransitionType.POSSIBLE)) {
				if (reachableByRequired.contains(transition.getStateFrom())) {
					return true;					
				}
			} else {
				return false;
			}
		}
		return false;
	}

	private boolean checkAllImplementationsWithoutDeadlock(MTS<State, Action> mts) {
		for (State state : mts.getStates()) {
			if (mts.getTransitions(state, TransitionType.REQUIRED).size()<1) {
				return false;
			}
		}
		return true;
	}

	//FIXME: check if there is a bug here. I think not all traces to deadlock are returned. Is this what has to be done? What is the result. Add a comment if it is not returning all of them.
	protected Set<MTSTrace<Action, State>> getAlltracesToDeadlock(MTS<State, Action> mts) {
		Set<MTSTrace<Action, State>> tracesToDeadlock = new HashSet<MTSTrace<Action, State>>();
		MTSTrace<Action, State> trace = new LinkedListMTSTrace<Action, State>();
		
		MTS<State, Action> clonedMTS = this.cloneMTS(mts);
		
		while (this.getTransitionsToDeadlock(clonedMTS, trace) && isNotEmptyMTS(clonedMTS)) {
			tracesToDeadlock.add(trace);
			this.deleteTrace(clonedMTS, trace);
			trace = new LinkedListMTSTrace<Action, State>();
		}

		return tracesToDeadlock;
	}
//TODO refactorizar tomando de abstractTRansitionSystem
	private Set<State> buildReachableByRequired(MTS<State, Action> mts) {
		Set<State> reachableStates = new HashSet<State>((int)(mts.getStates().size()/.75f + 1),0.75f);
		Queue<State> toProcess = new LinkedList<State>();
		toProcess.offer(mts.getInitialState());
		reachableStates.add(mts.getInitialState());
		while(!toProcess.isEmpty()) {
			for (Pair<Action, State> transition : mts.getTransitions(toProcess.poll(), TransitionType.REQUIRED)) {
				if (!reachableStates.contains(transition.getSecond())) {
					toProcess.offer(transition.getSecond());
					reachableStates.add(transition.getSecond());
				}
			}
		}
		return reachableStates;
	}
	
	// FIXME: This seems to be the same as MTSUtils. Check if there is a difference. If not use MTSUtils.cloneMTS
	private MTS<State,Action> cloneMTS(MTS<State,Action> mts) {
		MTS<State,Action> result = new MTSImpl<State, Action>(mts.getInitialState());
		result.addActions(mts.getActions());
		result.addStates(mts.getStates());
		
		for(TransitionType type: TransitionType.values()) {
			if (MAYBE.equals(type)) continue;
			for(State source: mts.getStates()) {
				for(Pair<Action,State> transition: mts.getTransitions(source, type)) {
					result.addTransition(
							source,
							transition.getFirst(),
							transition.getSecond(),
							type);					
				}
			}
				
		}
		return result;
	}
}
