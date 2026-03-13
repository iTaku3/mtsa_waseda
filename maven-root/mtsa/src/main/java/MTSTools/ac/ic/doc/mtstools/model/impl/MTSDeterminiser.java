package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.*;
import java.util.Map.Entry;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.operations.MultipleState;

public class MTSDeterminiser {
	final static int TAU = 0;

	private static final int ERROR = -1;

	private MTS<Long, String> toDeterminize;
	private List<MultipleState> multipleStates; // list of sets of old States

	private Hashtable<MultipleState, Integer> oldToNewStateMap; // maps sets of old states
													// (BitSet) -> new state
													// (Integer)

	private int newStateCode; // next new state number
	private int currentStateCode; // current state being computed
	private boolean forkSemantics;

	public MTSDeterminiser(MTS<Long, String> toDeterminize, boolean forkSemantics) {
		this.toDeterminize = toDeterminize;
		multipleStates = new ArrayList<MultipleState>();
		oldToNewStateMap = new Hashtable<MultipleState, Integer>(this.toDeterminize.getStates().size() * 2);
		newStateCode = 0;
		currentStateCode = newStateCode;
		this.forkSemantics = forkSemantics;
	}

	public MTS<Long, String> determinize() {

		MTS<Long, String> deterministic = new MTSImpl<Long, String>(Long.valueOf(addState(new MultipleState(newStateCode))));
		deterministic.addActions(this.toDeterminize.getActions());
		
		while (currentStateCode < newStateCode) {
			this.compute(currentStateCode, deterministic);
			++currentStateCode;
		}
		return deterministic;
	}

	protected void compute(int currentStateNumber, MTS<Long, String> determinisedMTS) {
		if (currentStateNumber!=ERROR) {
			MultipleState currentState = (MultipleState) multipleStates.get(currentStateNumber);
			if (!this.forkSemantics && currentState.isDeadlock()) {
				return;
			}
			
			Map<String, Map<Integer, List<Pair<Long, TransitionType>>>> transitionsByAction = groupTransitionsByAction(currentState);
			
			for (Entry<String, Map<Integer, List<Pair<Long, TransitionType>>>> transitionByAction : transitionsByAction.entrySet()) {
				String label = transitionByAction.getKey();
				Map<Integer, List<Pair<Long, TransitionType>>> transitionsByState = transitionByAction.getValue();
				
				MultipleState nextState = new MultipleState();
				boolean isRequired = buildNextState(currentState, nextState, transitionsByState);
				
				TransitionType type = TransitionType.REQUIRED;
				if (!isRequired) {
					type = TransitionType.MAYBE;
				}
				int nextStateNumber = this.addState(nextState);
				determinisedMTS.addState(Long.valueOf(nextStateNumber));
				determinisedMTS.addTransition(Long.valueOf(currentStateNumber), label, Long.valueOf(nextStateNumber), type);
			}
		}

	}

	private boolean buildNextState(MultipleState currentState, MultipleState nextState, Map<Integer, List<Pair<Long, TransitionType>>> transitionsByState) {
		boolean isRequired = false;
		for (Entry<Integer, List<Pair<Long, TransitionType>>> subStateTransition : transitionsByState.entrySet()) {
			Integer actualSubState = subStateTransition.getKey();
			for (Pair<Long, TransitionType> transition : subStateTransition.getValue()) {
				int toState = transition.getFirst().intValue();
				if (this.toDeterminize.getTransitions((long)toState, TransitionType.POSSIBLE).size()<=0) {
					nextState.setDeadlock();
				}
				nextState.addState(toState);
				TransitionType type = transition.getSecond();
				isRequired |= (TransitionType.REQUIRED.equals(type) && !currentState.hasMaybeMark(actualSubState));
				//solo agrego maybe si modifico el estado.
				if (TransitionType.MAYBE.equals(type)) {
					nextState.markAsMaybe(toState);
				}
			}
		}
		return isRequired;
	}

	private Map<String, Map<Integer, List<Pair<Long, TransitionType>>>> groupTransitionsByAction(MultipleState state) {
		MTSCompositionAdapter<String> mtsAux = new MTSCompositionAdapter<String>(toDeterminize);
		Map<String, Map<Integer, List<Pair<Long, TransitionType>>>> transitionsByAction = new HashMap<String, Map<Integer, List<Pair<Long, TransitionType>>>>();
		for (Integer subState : state) {
			Map<String, List<Pair<Long, TransitionType>>> tr = mtsAux.groupByAction(Long.valueOf(subState));
			for (Entry<String, List<Pair<Long, TransitionType>>> transitionByAction : tr.entrySet()) {
				if (!transitionsByAction.containsKey(transitionByAction.getKey())) {
					Map<Integer, List<Pair<Long, TransitionType>>> transitionsByState = new HashMap<Integer, List<Pair<Long, TransitionType>>>();
					transitionsByState.put(subState, transitionByAction.getValue());
					transitionsByAction.put(transitionByAction.getKey(), transitionsByState);
				} else {
					transitionsByAction.get(transitionByAction.getKey()).put(subState, transitionByAction.getValue());
				}
			}
		}
		return transitionsByAction;
	}

	protected int addState(MultipleState state) {
		Integer ii = (Integer) oldToNewStateMap.get(state);
		if (ii != null) {
			return ii.intValue();
		} else {
			oldToNewStateMap.put(state, new Integer(newStateCode));
			multipleStates.add(state);
			++newStateCode;
			return newStateCode - 1;
		}
	}
}
