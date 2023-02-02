package MTSSynthesis.controller.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.collections.PowerSet;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;

import com.google.common.collect.Sets;
@Deprecated
public class ImperfectToPerfectInfoGameBuilder<State, Action> {

	private MTS<State, Action> nonDet;
	private Set<Action> controllableSet;
	private Set<Action> uncontrollableSet;
	private Action newControllable;

	public ImperfectToPerfectInfoGameBuilder(MTS<State, Action> nonDet, Set<Action> controllableSet, Action newControllable) {
		this.nonDet = nonDet;
		this.controllableSet = controllableSet;
		this.uncontrollableSet = Sets.difference(nonDet.getActions(), controllableSet);
		this.newControllable = newControllable;
	}

	public MTS<Pair<Set<State>,Set<Action>>, Action> buildPerfectInfoGame() {
		Pair<Set<State>,Set<Action>> initial = Pair.create(Collections.singleton(this.nonDet.getInitialState()),null);
		MTS<Pair<Set<State>, Set<Action>>, Action> result = new MTSImpl<Pair<Set<State>, Set<Action>>, Action>(initial);
		result.addActions(nonDet.getActions()); //notice that this may include tau. 
		result.addAction(newControllable);
		
		Queue<Set<State>> toVisit = new LinkedList<Set<State>>();
		Set<Set<State>> visited = new HashSet<Set<State>>();
		toVisit.add(initial.getFirst());

		while (!toVisit.isEmpty()) {
			Set<State> currentSet = toVisit.poll();
			if (visited.contains(currentSet)) {
				continue;
			}
			visited.add(currentSet);
			
			Pair<Set<State>, Set<Action>> currentState = Pair.create(currentSet, (Set<Action>) null);
			result.addState(currentState);

			Set<Action> enabledControllableActions = enabledControllableActions(currentSet);
			Set<Action> intersection = Sets.intersection(enabledControllableActions, this.controllableSet);
			
			PowerSet<Action> controllablePowerSet = new PowerSet<Action>(intersection);
			for (Set<Action> controllables : controllablePowerSet) {
				
				Pair<Set<State>, Set<Action>> intermediateState = Pair.create(currentSet, controllables);
				result.addState(intermediateState);
				result.addTransition(currentState, this.newControllable, intermediateState ,TransitionType.REQUIRED);
				
				// checking for dead-ends
				Set<Action> enabledActions = Sets.union(controllables, this.uncontrollableSet);
				if (isDeadlockSet(currentSet, enabledActions)) {
					continue;
				}
				
				for (Action action : enabledActions) {
					Set<State> successorSet = computeSuccessorsSet(intermediateState, action);
					if (successorSet.isEmpty()) {
						continue;
					}
					Pair<Set<State>,Set<Action>> successorState = Pair.create(successorSet, null);
					result.addState(successorState);
					toVisit.add(successorSet);
					result.addTransition(intermediateState, action, successorState, TransitionType.REQUIRED);
				}
			}
		}
		return result;
	}

	private Set<Action> enabledControllableActions(Set<State> currentSet) {
		Set<Action> setOfActions = new HashSet<Action>();
		for (State state : currentSet) {
			BinaryRelation<Action, State> transitions = this.nonDet.getTransitions(state, TransitionType.REQUIRED);
			for (Pair<Action, State> tr : transitions) {
				setOfActions.add(tr.getFirst());
			}
		}
		return setOfActions;
	}

	private Set<State> computeSuccessorsSet(Pair<Set<State>, Set<Action>> intermediateState, Action action) {
		Set<State> result = new HashSet<State>();
		for (State state : intermediateState.getFirst()) {
			result.addAll(this.nonDet.getTransitions(state, TransitionType.POSSIBLE).getImage(action));
		}
		return result;
	}

	private boolean isDeadlockSet(Set<State> current, Set<Action> enabledActions) {
		for (State state : current) {
			if (!isImageEnabled(state, enabledActions)) {
				return true;
			}
		}
		return false;
	}

	private boolean isImageEnabled(State state, Set<Action> enabledActions) {
		BinaryRelation<Action, State> trs = this.nonDet.getTransitions(state, TransitionType.POSSIBLE);
		for (Iterator<Pair<Action, State>> iterator = trs.iterator(); iterator.hasNext();) {
			Pair<Action, State> pair = iterator.next();
			if (enabledActions.contains(pair.getFirst())) {
				return true;
			}
		}
		return false;
	}

}
