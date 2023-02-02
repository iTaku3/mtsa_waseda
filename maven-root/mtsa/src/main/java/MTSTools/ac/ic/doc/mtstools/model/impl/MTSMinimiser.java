package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class MTSMinimiser<Action> {

	private StateSucessors<Action>[] stateSucessors;
	
	public MTS<Long, Action> minimise(MTS<Long, Action> mts) {
		initalizeMinimiser(mts);
		Collection<Pair<EquivalenceClass<Long>, EquivalenceClass<Long>>> simulationRelation = computeMaximalSimulationRelation(mts);
		MTS<Long, Action> minimisationResult = buildMTS(mts, simulationRelation);
		minimisationResult.removeUnreachableStates();
		return minimisationResult;
	}

	/**
	 * Initializes the outgoing transitions from every state of <code>mts</code>
	 * 
	 * @param mts
	 */
	private void initalizeMinimiser(MTS<Long, Action> mts) {
		stateSucessors = new StateSucessors[mts.getStates().size()];
		
		for (Long state : mts.getStates()) {
			StateSucessors<Action> actualSuccessors = new StateSucessors<Action>(state);

			for (Pair<Action, Long> transition : mts.getTransitions(state, TransitionType.REQUIRED)) {
				actualSuccessors.addSuccessor(transition.getSecond(), transition.getFirst(), TransitionType.REQUIRED);
			}
			for (Pair<Action, Long> transition : mts.getTransitions(state, TransitionType.POSSIBLE)) {
				actualSuccessors.addSuccessor(transition.getSecond(), transition.getFirst(), TransitionType.POSSIBLE);
			}

			stateSucessors[state.intValue()] = actualSuccessors;
		}
		simulationRelation = new HashSet<Pair<EquivalenceClass<Long>, EquivalenceClass<Long>>>();
		equivalenceClasses = new HashSet<EquivalenceClass<Long>>();

		EquivalenceClassImpl<Long> initialEquivalenceClass;
		initialEquivalenceClass = new EquivalenceClassImpl<Long>();
		initialEquivalenceClass.addAllStates(mts.getStates());
		equivalenceClasses.add(initialEquivalenceClass);
		simulationRelation.add(new Pair<EquivalenceClass<Long>, EquivalenceClass<Long>>(initialEquivalenceClass,
				initialEquivalenceClass));

	}

	/**
	 * Crea el MTS resultado de la minimizacion
	 * 
	 * @param mts
	 * @param simulationRelation
	 * @return
	 */
	private MTS<Long, Action> buildMTS(MTS<Long, Action> mts,
			Collection<Pair<EquivalenceClass<Long>, EquivalenceClass<Long>>> simulationRelation) {
		MTS<Long, Action> result = new MTSImpl<Long, Action>(code(getEquivalenceClassOf(mts.getInitialState())));
		result.addActions(mts.getActions());
		
		for (EquivalenceClass<Long> equivalenceClass : equivalenceClasses) {

			BitSet nextStates = new BitSet();
			nextStates.set(0, mts.getStates().size());
			for (Long state : equivalenceClass) {
				BitSet stateSuccessors = stateSucessors[state.intValue()].getSuccessors();
				BitSet successorsClasses = getSuccessorsClasses(stateSuccessors);
				nextStates.and(successorsClasses);
			}

			// para un estado de la clase agrego las transisiones
			Long state = equivalenceClass.iterator().next();
			TransitionType required = TransitionType.REQUIRED;
			TransitionType maybe = TransitionType.MAYBE;

			// TODO Transitions by toState?
			BinaryRelation<Action, Long> requiredTransitionsFrom = mts.getTransitions(state, required);
			for (Pair<Action, Long> transition : requiredTransitionsFrom) {
				Long from = code(equivalenceClass);
				EquivalenceClass<Long> equivalenceClassOf = getEquivalenceClassOf(transition.getSecond());
				Long to = code(equivalenceClassOf);
				if (nextStates.get(to.intValue())) {
					result.addState(from);
					result.addState(to);
					result.addAction(transition.getFirst());
					result.addTransition(from, transition.getFirst(), to, required);
				}
			}
			BinaryRelation<Action, Long> maybeTransitionsFrom = mts.getTransitions(state, maybe);
			for (Pair<Action, Long> transition : maybeTransitionsFrom) {
				Long from = code(equivalenceClass);
				EquivalenceClass<Long> equivalenceClassOf = getEquivalenceClassOf(transition.getSecond());
				Long to = code(equivalenceClassOf);
				if (nextStates.get(to.intValue())) {
					result.addState(from);
					result.addState(to);
					result.addAction(transition.getFirst());
					result.addTransition(from, transition.getFirst(), to, maybe);
				}
			}
		}
		return result;
	}

	private BitSet getSuccessorsClasses(BitSet stateSuccessors) {
		BitSet result = new BitSet();
		for (int i = stateSuccessors.nextSetBit(0); i >= 0; i = stateSuccessors.nextSetBit(i + 1)) {
			result.set(code(getEquivalenceClassOf(Long.valueOf(i))).intValue());
		}
		return result;
	}

	private Collection<Pair<EquivalenceClass<Long>, EquivalenceClass<Long>>> computeMaximalSimulationRelation(
			MTS<Long, Action> mts) {
		boolean change = true;
		while (change) {
			change = false;
			Set<EquivalenceClass<Long>> iterationEquivalenceClasses = new HashSet<EquivalenceClass<Long>>();
			Collection<Pair<EquivalenceClass<Long>, EquivalenceClass<Long>>> iterationSimulationRelation = new HashSet<Pair<EquivalenceClass<Long>, EquivalenceClass<Long>>>();
			// refine
			change = refine(iterationEquivalenceClasses);

			// update solo si la relacion de eq de la iteracion es distinta que
			// la anterior
			change |= update(mts, iterationEquivalenceClasses, iterationSimulationRelation);
			simulationRelation = iterationSimulationRelation;
			equivalenceClasses = iterationEquivalenceClasses;
		}

		return simulationRelation;
	}
	private boolean update(MTS<Long, Action> mts, Set<EquivalenceClass<Long>> iterationEquivalenceClasses,
			Collection<Pair<EquivalenceClass<Long>, EquivalenceClass<Long>>> iterationSimulationRelation) {
		boolean change = false;
		for (Pair<EquivalenceClass<Long>, EquivalenceClass<Long>> relationItem : simulationRelation) {
			for (EquivalenceClass<Long> equivalenceClassA : iterationEquivalenceClasses) {
				for (EquivalenceClass<Long> equivalenceClassB : iterationEquivalenceClasses) {
					if (relationItem.getFirst().hasAll(equivalenceClassA)
							&& relationItem.getSecond().hasAll(equivalenceClassB)) {
						Set<EquivalenceClass<Long>> fi = buildFi(equivalenceClassB, mts);
						if (fi.containsAll(buildPi(equivalenceClassA, mts))
								&& classBRefinesClassA(equivalenceClassA, equivalenceClassB)) {
							iterationSimulationRelation.add(new Pair<EquivalenceClass<Long>, EquivalenceClass<Long>>(
									equivalenceClassA, equivalenceClassB));
						} else {
							change = true;
						}
					}
				}
			}
		}
		return change;
	}

	private boolean classBRefinesClassA(EquivalenceClass<Long> equivalenceClassA,
			EquivalenceClass<Long> equivalenceClassB) {
		return stateBRefinesStateA(equivalenceClassA.iterator().next(), equivalenceClassB.iterator().next());
	}

	private Set<EquivalenceClass<Long>> buildPi(EquivalenceClass<Long> equivalenceClass, MTS<Long, Action> mts) {
		Set<EquivalenceClass<Long>> result = new HashSet<EquivalenceClass<Long>>();
		for (Long state : equivalenceClass) {
			// tomo las posibles
			for (Pair<Action, Long> transition : mts.getTransitions(state, TransitionType.POSSIBLE)) {
				result.add(getEquivalenceClassOf(transition.getSecond()));
			}
		}
		return result;
	}

	private Set<EquivalenceClass<Long>> buildFi(EquivalenceClass<Long> equivalenceClass, MTS<Long, Action> mts) {
		Set<EquivalenceClass<Long>> result = new HashSet<EquivalenceClass<Long>>();
		Set<EquivalenceClass<Long>> piEquivalenceClass = buildPi(equivalenceClass, mts);
		for (EquivalenceClass<Long> clazz : piEquivalenceClass) {
			for (Pair<EquivalenceClass<Long>, EquivalenceClass<Long>> simulationItem : simulationRelation) {
				if (simulationItem.getSecond().equals(clazz)) {
					result.add(simulationItem.getFirst());
				}
			}
		}

		return result;
	}

	private boolean refine(Set<EquivalenceClass<Long>> iterationEquivalenceClasses) {
		boolean change = false;
		for (EquivalenceClass<Long> clazz : equivalenceClasses) {
			for (Long state : clazz) {
				EquivalenceClass<Long> newEquivalenceClass = computeNewEquivalenceClass(state, clazz);

				if (!newEquivalenceClass.equals(clazz)) {
					change = true;
				}
				iterationEquivalenceClasses.add(newEquivalenceClass);
			}
		}
		return change;
	}

	private EquivalenceClass<Long> computeNewEquivalenceClass(Long state, EquivalenceClass<Long> equivalenceClass) {
		Set<Long> gt = new HashSet<Long>();
		Set<Long> lt = new HashSet<Long>();
		for (Long stateBis : equivalenceClass) {
			// building GT
			if (stateBRefinesStateA(state, stateBis)) {
				gt.add(stateBis);
			}
			// building LT
			if (stateBRefinesStateA(stateBis, state)) {
				lt.add(stateBis);
			}
		}
		EquivalenceClass<Long> result = new EquivalenceClassImpl<Long>();
		result.addAllStates(CollectionUtils.intersection(gt, lt));
		return result;
	}

	private boolean stateBRefinesStateA(Long stateA, Long stateB) {
		
//		BaseSemanticsByRelation weak = new WeakSemantics(Collections.EMPTY_SET);
//		BaseSemanticsByRelation weak = new WeakSemantics(Collections.singleton(MTSExamples.TAU));
//		return weak.isARefinement(mtsToMinimise, mtsToMinimise, stateA, stateB);
		
		/*
		 * para todo requerido de A, B tiene un requerido por la misma action y
		 * los estados a los que llegan estan en H y para todo posible de B, A
		 * tiene un posible por la misma action y los estados a los que llegan
		 * estan en H
		 * 
		 */
		StateSucessors<Action> aSucessors = stateSucessors[stateA.intValue()];
		StateSucessors<Action> bSucessors = stateSucessors[stateB.intValue()];
		// TODO ISSUE Design

		// para todo sucesor requerido de A
		for (Entry<Action, BitSet> successorFromAByAction : aSucessors.getRequiredSuccessorsByAction().entrySet()) {
			BitSet successorsFromA = successorFromAByAction.getValue();
			BitSet successorsFromB = bSucessors.getRequiredSuccessorsByAction().get(successorFromAByAction.getKey());

			if (!successorsOfBRefinesSuccessorsOfA(successorsFromA, successorsFromB)) {
				return false;
			}
		}

		// para todo sucesor posible de B
		for (Entry<Action, BitSet> successorFromBByAction : bSucessors.getPossibleSuccessorsByAction().entrySet()) {
			BitSet successorsFromB = successorFromBByAction.getValue();
			BitSet successorsFromA = aSucessors.getPossibleSuccessorsByAction().get(successorFromBByAction.getKey());

			if (!successorsOfBRefinesSuccessorsOfA(successorsFromB, successorsFromA)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Para todo sucesor de A s1 tengo que tener un sucesor de B s2 tal que
	 * ([s1], [s2]) pertenece a H
	 * 
	 * @param successorsFromA
	 * @param successorsFromB
	 */
	private boolean successorsOfBRefinesSuccessorsOfA(BitSet successorsFromA, BitSet successorsFromB) {
		if (successorsFromB == null) {
			return false;
		}
		for (int succA = successorsFromA.nextSetBit(0); succA >= 0; succA = successorsFromA.nextSetBit(succA + 1)) {
			if (!hasRefinesSuccesor(successorsFromB, succA)) {
				return false;
			}
		}
		return true;
	}

	private boolean hasRefinesSuccesor(BitSet successorsFromB, Integer succA) {
		for (int succB = successorsFromB.nextSetBit(0); succB >= 0; succB = successorsFromB.nextSetBit(succB + 1)) {
			// TODO ISSUE Desing, no quiero construir pares
			if (simulationRelation.contains(new Pair<EquivalenceClass<Long>, EquivalenceClass<Long>>(
					getEquivalenceClassOf(Long.valueOf(succB)), getEquivalenceClassOf(Long.valueOf(succA))))) {
				return true;
			}
		}
		return false;
	}

	private EquivalenceClass<Long> getEquivalenceClassOf(Long state) {
		for (EquivalenceClass equivalenceClass : equivalenceClasses) {
			if (equivalenceClass.hasState(state)) {
				return equivalenceClass;
			}
		}
		throw new RuntimeException("There is no equivalence class for state: [" + state + "]");
	}

	private Map<EquivalenceClass<Long>, Long> equivalenceClassToLong = new HashMap<EquivalenceClass<Long>, Long>();
	private Long nextStateNumber = 0L;
	private Set<EquivalenceClass<Long>> equivalenceClasses;
	private Collection<Pair<EquivalenceClass<Long>, EquivalenceClass<Long>>> simulationRelation;

	protected Long code(EquivalenceClass<Long> states) {
		if (!equivalenceClassToLong.containsKey(states)) {
			equivalenceClassToLong.put(states, nextStateNumber++);
		}
		return equivalenceClassToLong.get(states);
	}
}
