package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;
//FIXME State is a generic class never used. It should be used instead of Long
public class MTSMultipleComposer<State, Action> {
	private TransitionRulesApplier compositionRulesApplier;

	private CompositionStateFactory stateFactory;

	private List<MTS<Long, Action>> originalMTSs;

	public MTSMultipleComposer(TransitionRulesApplier compositionRulesApplier) {
		this.compositionRulesApplier = compositionRulesApplier;
		stateFactory = new CompositionStateFactory();
	}

	//XXX TODO: Why does this class uses Long instead of a generic class S? Should be changed.
	//TOTO: Check all users of this class and method. Some of them had to do some hack to use this method instead of do it in a generic way.
	public MTS<Long, Action> compose(List<MTS<Long, Action>> mtss) {
		if (!compositionRulesApplier.composableModels(mtss)) {
			throw new IllegalArgumentException("Inconsistent models");
		}
		originalMTSs = mtss;
		return composeAdapter(buildMTSCompositionAdaptersFor(mtss));
	}

	public MTS<Long, Action> composeAdapter(List<MTSCompositionAdapter<Action>> mtss) {

		Set<CompositionState> ready = new HashSet<CompositionState>();
		Queue<CompositionState> toProcess = new LinkedList<CompositionState>();

		CompositionState initial = stateFactory.getNextStateFor(buildInitialStatesList(mtss));

		MTS<Long, Action> retValue = new MTSImpl<Long, Action>(initial.getCompositeState());

		addNewState(initial, ready, toProcess);

		while (!toProcess.isEmpty()) {
			CompositionState actualCompositeState = toProcess.remove();
			Long actual = actualCompositeState.getCompositeState();

			retValue.addState(actual);

			// Union Alphabet
			for (MTSCompositionAdapter<Action> compositionAdapter : mtss) {
				retValue.addActions(compositionAdapter.getActions());
			}

			Collection<Transition<Action>> composableTransitions = getComposableTransitions(mtss, actualCompositeState);
			for (Iterator<Transition<Action>> it = composableTransitions.iterator(); it.hasNext();) {
				Transition<Action> transition = it.next();
				CompositionState toState = transition.getCompositionState();
				if (compositionRulesApplier.composableStates(originalMTSs, toState)) {
					Long to = transition.getTo();
					retValue.addState(to);
					retValue.addTransition(actual, transition.getAction(), to, transition.getType());
					if (!ready.contains(toState) && !toState.isErrorState()) {
						addNewState(toState, ready, toProcess);
					}
				}
			}
		}
		
		compositionRulesApplier.cleanUp(retValue);
		return retValue;
	}

	private void addNewState(CompositionState stateToAdd, Set<CompositionState> ready, Queue<CompositionState> toProcess) {
		ready.add(stateToAdd);
		toProcess.add(stateToAdd);
	}

	/**
	 * Devuelve una lista de maps donde la key son las actions.
	 * 
	 * @param <Action>
	 * @param mtss
	 * @param states
	 * @param actions
	 * @return
	 */
	private List<Map<Action, List<Pair<Long, TransitionType>>>> groupByActionForAll(
			List<MTSCompositionAdapter<Action>> mtss, List<Long> states, Collection<Action> actions) {
		List<Map<Action, List<Pair<Long, TransitionType>>>> retValue = new ArrayList<Map<Action, List<Pair<Long, TransitionType>>>>();
		if (mtss.size() != states.size()) {
			throw new RuntimeException("mtss is not compliant with states");
		}
		int i = 0;
		for (MTSCompositionAdapter<Action> compositionAdapter : mtss) {
			Map<Action, List<Pair<Long, TransitionType>>> groupByAction = compositionAdapter.groupByAction(states.get(i));
			retValue.add(groupByAction);

			for (Iterator<Action> it = groupByAction.keySet().iterator(); it.hasNext();) {
				Action action = it.next();
				if (!actions.contains(action)) {
					actions.add(action);
				}
			}
			i++;
		}
		return retValue;
	}

	private void calculateIsInAlphabet(Action action, List<MTSCompositionAdapter<Action>> mtss, BitSet isInAlphabetOf) {
		for (int i = 0; i < mtss.size(); i++) {
			isInAlphabetOf.set(i, mtss.get(i).getActions().contains(action));
		}
	}

	private void calculateIsInTransitionsFrom(Action action, List<Map<Action, List<Pair<Long, TransitionType>>>> mtss,
			BitSet isInTransitionsFromOf, BitSet isInAlphabetOf) {
		for (int i = isInAlphabetOf.nextSetBit(0); i >= 0; i = isInAlphabetOf.nextSetBit(i + 1)) {
			isInTransitionsFromOf.set(i, mtss.get(i).containsKey(action));
		}
	}

	private Collection<Transition<Action>> getComposableTransitions(List<MTSCompositionAdapter<Action>> mtss,
			CompositionState x) {
		Collection<Transition<Action>> retValue = new ArrayList<Transition<Action>>();

		List<Long> state = x.getIndividualStates();

		Collection<Action> allFromActions = new HashSet<Action>();

		List<Map<Action, List<Pair<Long, TransitionType>>>> transisionesDesdeEstadoActualAgrupadasPorAction = groupByActionForAll(
				mtss, state, allFromActions);

		for (Iterator<Action> it = allFromActions.iterator(); it.hasNext();) {
			Action action = it.next();
			BitSet isInAlphabetOf = new BitSet();
			BitSet isInTransitionsFrom = new BitSet();
			calculateIsInAlphabet(action, mtss, isInAlphabetOf);
			calculateIsInTransitionsFrom(action, transisionesDesdeEstadoActualAgrupadasPorAction, isInTransitionsFrom,
					isInAlphabetOf);

			if (isInAlphabetOf.cardinality() > 1 && !MTSConstants.TAU.equalsIgnoreCase(action.toString())) {
				if (isInAlphabetOf.equals(isInTransitionsFrom)) {
					// compone

					Set<Pair<Vector<Long>, TransitionType>> estadosAlcanzablesDesdeAccionActual = buildReachableStates(
							mtss, transisionesDesdeEstadoActualAgrupadasPorAction, action, isInTransitionsFrom);

					for (Pair<Vector<Long>, TransitionType> statesToCompose : estadosAlcanzablesDesdeAccionActual) {
						for (int j = 0; j < statesToCompose.getFirst().size(); j++) {
							Long position = statesToCompose.getFirst().get(j);
							if (position == null) {
								statesToCompose.getFirst().set(j, state.get(j));
							}
						}
						CompositionState nextState = stateFactory.getNextStateFor(statesToCompose.getFirst());
						Transition<Action> transition = new Transition<Action>(nextState.getCompositeState(), action,
								statesToCompose.getSecond());
						transition.setCompositionState(nextState);
						retValue.add(transition);
					}
				}
			} else {
				// No sincroniza por taus, solo si la action es tau van a mover
				// mas de una.

				for (int mtsPosition = isInTransitionsFrom.nextSetBit(0); mtsPosition >= 0; mtsPosition = isInTransitionsFrom
						.nextSetBit(mtsPosition + 1)) {
					for (Pair<Long, TransitionType> transition : transisionesDesdeEstadoActualAgrupadasPorAction.get(
							mtsPosition).get(action)) {
						Vector<Long> nextState = new Vector<Long>(state);
						nextState.set(mtsPosition, transition.getFirst());

						CompositionState nextCompositionState = stateFactory.getNextStateFor(nextState);
						Transition<Action> newTransition = new Transition<Action>(nextCompositionState
								.getCompositeState(), action, compositionRulesApplier.applyCompositionRules(transition
								.getSecond()));
						newTransition.setCompositionState(nextCompositionState);

						retValue.add(newTransition);
					}
				}
			}
		}
		return retValue;
	}

	private Set<Pair<Vector<Long>, TransitionType>> buildReachableStates(List<MTSCompositionAdapter<Action>> mtss,
			List<Map<Action, List<Pair<Long, TransitionType>>>> transisionesDesdeEstadoActualAgrupadasPorAction,
			Action action, BitSet isInTransitionsFrom) {
		Set<Pair<Vector<Long>, TransitionType>> estadosAlcanzablesDesdeAccionActual = new HashSet<Pair<Vector<Long>, TransitionType>>();
		int i = isInTransitionsFrom.nextSetBit(0);

		/*
		 * Este codigo inserta el primer estado de la composicion
		 */
		for (Pair<Long, TransitionType> transition : transisionesDesdeEstadoActualAgrupadasPorAction.get(i).get(action)) {
			Vector<Long> firstComponentOfNextState = buildEmptyCompositionState(mtss.size());
			firstComponentOfNextState.set(i, transition.getFirst());
			estadosAlcanzablesDesdeAccionActual.add(Pair.create(firstComponentOfNextState, transition.getSecond()));
		}

		for (i = isInTransitionsFrom.nextSetBit(i + 1); i >= 0; i = isInTransitionsFrom.nextSetBit(i + 1)) {
			estadosAlcanzablesDesdeAccionActual = armoConjuntoEstados(i,
					transisionesDesdeEstadoActualAgrupadasPorAction.get(i).get(action),
					estadosAlcanzablesDesdeAccionActual);
		}
		return estadosAlcanzablesDesdeAccionActual;
	}

	private Vector<Long> buildEmptyCompositionState(int i) {
		// TODO sacar esto solo lo uso para que no me pinche a medidad que
		// agrego estados en la funcion armar estados
		Vector<Long> retValue = new Vector<Long>();
		int iterator = 0;
		while (iterator < i) {
			retValue.add(null);
			iterator++;
		}
		return retValue;
	}

	/*
	 * Esta funcion va armando el estado compuesto de la maquina agregando un
	 * elemento por vez hasta tener un set de pares donde la primera componente
	 * es una lista de states representando el estado compuesto y un tipo que
	 * seria el tipo de la transicion que te lleva a ese estado.
	 */
	private Set<Pair<Vector<Long>, TransitionType>> armoConjuntoEstados(int mtsPosition,
			List<Pair<Long, TransitionType>> transitionsOnActualAction,
			Set<Pair<Vector<Long>, TransitionType>> acumulatedCompositeStatesSet) {
		Set<Pair<Vector<Long>, TransitionType>> retValue = new HashSet<Pair<Vector<Long>, TransitionType>>();
		for (Pair<Long, TransitionType> transitionOnActualAction : transitionsOnActualAction) {

			for (Pair<Vector<Long>, TransitionType> acumulatedState : acumulatedCompositeStatesSet) {
				List<Long> compositeState = new Vector(acumulatedState.getFirst());
				compositeState.set(mtsPosition, transitionOnActualAction.getFirst());

				TransitionType compositeTransitionType = compositionRulesApplier.applyCompositionRules(
						transitionOnActualAction, acumulatedState);

				retValue.add(new Pair(compositeState, compositeTransitionType));
			}

		}
		return retValue;
	}

	/*
	 * Construye la coleccion de adapters para componer
	 * 
	 * @param <Action>
	 * @param mtss
	 * @return
	 */
	private List<MTSCompositionAdapter<Action>> buildMTSCompositionAdaptersFor(List<MTS<Long, Action>> mtss) {
		List<MTSCompositionAdapter<Action>> retValue = new ArrayList<MTSCompositionAdapter<Action>>();
		for (Iterator<MTS<Long, Action>> it = mtss.iterator(); it.hasNext();) {
			MTS<Long, Action> mts = it.next();
			retValue.add(new MTSCompositionAdapter<Action>(mts));
		}
		return retValue;
	}

	public Vector<Long> buildInitialStatesList(List<MTSCompositionAdapter<Action>> mtss) {
		Vector<Long> retValue = new Vector<Long>();
		for (MTSCompositionAdapter<Action> compositionAdapter : mtss) {
			retValue.add(compositionAdapter.getInitialState());
		}
		return retValue;
	}
}
