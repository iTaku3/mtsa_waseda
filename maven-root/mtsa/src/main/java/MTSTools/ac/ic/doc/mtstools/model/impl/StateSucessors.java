package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class StateSucessors<Action> {
	
	private Long stateFrom;
	private Map<Action, BitSet> requiredSuccessorsByAction;
	private Map<Action, BitSet> possibleSuccessorsByAction;
	
	public StateSucessors(Long stateFrom){
		this.stateFrom = stateFrom;
		this.possibleSuccessorsByAction = new HashMap<Action, BitSet>();
		this.requiredSuccessorsByAction = new HashMap<Action, BitSet>();
	}
	
	public void addSuccessor(Long stateTo, Action label, TransitionType type) {
		if (!possibleSuccessorsByAction.containsKey(label)) {
			possibleSuccessorsByAction.put(label, new BitSet());
		}
		possibleSuccessorsByAction.get(label).set(stateTo.intValue());
		
		//TODO ISSUE design
		if (TransitionType.REQUIRED.equals(type)) {
			if (!requiredSuccessorsByAction.containsKey(label)) {
				requiredSuccessorsByAction.put(label, new BitSet());
			}
			requiredSuccessorsByAction.get(label).set(stateTo.intValue());
		}
	}
	
	public BitSet successorsByAction(Action action, TransitionType type) {
		//TODO ISSUE Design
		if (TransitionType.REQUIRED.equals(type)) {
			return requiredSuccessorsByAction.get(action);
		} else {
			return possibleSuccessorsByAction.get(action);
		}
	}
	
	/**
	 * @return the stateFrom
	 */
	protected Long getStateFrom() {
		return stateFrom;
	}

	/**
	 * @return the requiredSuccessorsByAction
	 */
	protected Map<Action, BitSet> getRequiredSuccessorsByAction() {
		return requiredSuccessorsByAction;
	}

	/**
	 * @return the possibleSuccessorsByAction
	 */
	protected Map<Action, BitSet> getPossibleSuccessorsByAction() {
		return possibleSuccessorsByAction;
	}
	
	private Collection<Entry<Action, BitSet>> getMaybeTransitions() {
		return CollectionUtils.subtract(possibleSuccessorsByAction.entrySet(), requiredSuccessorsByAction.entrySet());
	}
	
	public Set<Pair<Action, TransitionType>> getTransitionsTo(Long toState) {
		HashSet<Pair<Action, TransitionType>> result = new HashSet<Pair<Action,TransitionType>>();
		for (Entry<Action, BitSet> requiredSuccessor : requiredSuccessorsByAction.entrySet()) {
			if (requiredSuccessor.getValue().get(toState.intValue())) {
				result.add(new Pair<Action, TransitionType>(requiredSuccessor.getKey(), TransitionType.REQUIRED));				
			}
		}
		for (Entry<Action, BitSet> maybeSuccessor : getMaybeTransitions()) {
			if (maybeSuccessor.getValue().get(toState.intValue())) {
				result.add(new Pair<Action, TransitionType>(maybeSuccessor.getKey(), TransitionType.MAYBE));
			}
		}
		return result;
	}

	public BitSet getSuccessors() {
		BitSet result = new BitSet();
		for (Entry<Action, BitSet> successorByAction : possibleSuccessorsByAction.entrySet()) {
			result.or(successorByAction.getValue());
		}
		return result;
	}

}
