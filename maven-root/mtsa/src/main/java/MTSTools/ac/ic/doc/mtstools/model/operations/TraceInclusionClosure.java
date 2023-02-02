package MTSTools.ac.ic.doc.mtstools.model.operations;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.utils.MTSUtils;

public class TraceInclusionClosure implements MTSClosure {
	
	public static TraceInclusionClosure getInstance() {
		if (instance == null) {
			instance = new TraceInclusionClosure();
		}
		return instance;
	}
	
	private static TraceInclusionClosure instance;
	
	@Override
	public <State, Action> void applyMTSClosure(MTS<State, Action> mts, Set<Action> silentActions) {
		for (State state : mts.getStates()) {
			this.closure(state, silentActions, mts);
		}
		MTSUtils.removeSilentTransitions(mts, silentActions);
	}
	
	private <State, Action> void closure(State state, Set<Action> silentActions, MTS<State, Action> mts) {
		Set<State> visited = new HashSet<State>();
		Queue<State> toVisitMust = new LinkedList<State>();
		Queue<State> toVisitMay = new LinkedList<State>();
		
		visited.add(state);
		
		//add silent must successors to be processed.
		addSilentSuccessorsToVisit(state, silentActions, mts, toVisitMust, TransitionType.REQUIRED);
		//add silent may successors to be processed.
		addSilentSuccessorsToVisit(state, silentActions, mts, toVisitMay, TransitionType.MAYBE);
		

		//process all must silent closure of state
		while(!toVisitMust.isEmpty()) {
			State succ = toVisitMust.poll();
			if (visited.contains(succ)) {
				continue;
			}
			visited.add(succ);
			
			addSilentSuccessorsToVisit(succ, silentActions, mts, toVisitMust, TransitionType.REQUIRED);
			addSilentSuccessorsToVisit(succ, silentActions, mts, toVisitMay, TransitionType.MAYBE);

			addRequiredSuccessors(state, succ, silentActions, mts);
			addPossibleSuccessors(state, succ, silentActions, TransitionType.MAYBE, mts);
		}
		
		//process all maybe silent closure of state.
		while(!toVisitMay.isEmpty()) {
			State succ = toVisitMay.poll();
			if (visited.contains(succ)) {
				continue;
			}
			visited.add(succ);
			
			//add all silent successors to be processed.
			addSilentSuccessorsToVisit(succ, silentActions, mts, toVisitMay, TransitionType.POSSIBLE);
			
			addPossibleSuccessors(state, succ, silentActions, TransitionType.POSSIBLE, mts);
		}
	}

	private <State, Action> void addPossibleSuccessors(State state, State succ,
			Set<Action> silentActions, TransitionType trType, MTS<State, Action> mts) {
		
		for (Pair<Action, State> tr : mts.getTransitions(succ, trType)) {
			if (silentActions.contains(tr.getFirst())) {
				continue;
			}
			if (!mts.getTransitions(state, TransitionType.POSSIBLE).getImage(tr.getFirst()).contains(tr.getSecond())) {
				mts.addPossible(state, tr.getFirst(), tr.getSecond());
			}
		}
	}

	private <State, Action> void addRequiredSuccessors(State state, State succ, Set<Action> silentActions, MTS<State, Action> mts) {
		BinaryRelation<Action, State> transitions = mts.getTransitions(succ, TransitionType.REQUIRED);
		for (Pair<Action, State> tr : transitions) {
			if (silentActions.contains(tr.getFirst())) {
				continue;
			}
			mts.addRequired(state, tr.getFirst(), tr.getSecond());
		}
	}

	private <Action, State> void addSilentSuccessorsToVisit(State state, Set<Action> silentActions, MTS<State, Action> mts,
			Queue<State> toVisitMay, TransitionType type) {
		BinaryRelation<Action, State> mayTransitions = mts.getTransitions(state, type);
		for (Action action : silentActions) {
			Set<State> image = mayTransitions.getImage(action);
			toVisitMay.addAll(image);
		}
	}

}
