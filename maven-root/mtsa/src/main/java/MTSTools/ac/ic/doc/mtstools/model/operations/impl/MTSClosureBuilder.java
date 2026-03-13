package MTSTools.ac.ic.doc.mtstools.model.operations.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.operations.MTSClosure;
import MTSTools.ac.ic.doc.mtstools.model.operations.ProjectionBuilder;

public class MTSClosureBuilder implements MTSClosure {

	public <State, Action> void applyMTSClosure(MTS<State, Action> mts, Set<Action> silentActions) {
		if (silentActions.isEmpty()) {
			return ;
		}
		for (State state : mts.getStates()) {
			for (Action action : mts.getActions()) {

				Set<Action> sa = getSilentActions(silentActions, action);
				addTransitions(mts, state, action, TransitionType.MAYBE, sa);
				addTransitions(mts, state, action, TransitionType.REQUIRED, sa);
			}
		}
	}

	private <Action> Set<Action> getSilentActions(Set<Action> silentActions, Action action) {
		if (!silentActions.contains(action)) {
			return silentActions;
		} else {
			return Collections.emptySet();
		}
	}

	private <Action, State> void addTransitions(MTS<State, Action> mts, State state, Action action,
			TransitionType transitionType, Set<Action> silentActions) {
		Iterator<List<Pair<Action, State>>> iterator = getIterator(mts, state, action, transitionType, silentActions);
		while (iterator.hasNext()) {
			List<Pair<Action, State>> transitions = iterator.next();
			Pair<Action, State> transition = transitions.get(1);
			State stateTo = transition.getSecond();
			transitionType.addTransition(mts, state, action, stateTo);
		}
	}

	protected <State, Action> Iterator<List<Pair<Action, State>>>  getIterator(MTS<State, Action> mts,
			State state, Action action, TransitionType transitionType, Set<Action> emptySilentActions) {
		return getClosurePathBuilder().getProjectionIterator(mts, state, action, transitionType, emptySilentActions);
		
	}

	protected ProjectionBuilder getClosurePathBuilder() {
		return ProjectionBuilder.getInstance();
	}
}
