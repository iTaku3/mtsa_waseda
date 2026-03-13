package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.*;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class MTSComposer {

	public <Action> MTS<Long, Action> compose(Collection<MTS<Long, Action>> mtss) {
		if (mtss == null || mtss.isEmpty()) {
			throw new IllegalArgumentException("Size zero or null argument.");
		}
		Iterator<MTS<Long, Action>> iterator = mtss.iterator();

		MTS<Long, Action> retValue = iterator.next();
		while (iterator.hasNext()) {
			retValue = compose(retValue, iterator.next());
		}
		return retValue;
	}

	public <Action> MTS<Long, Action> compose(MTS<Long, Action> mtsA, MTS<Long, Action> mtsB) {
		Set<Long> ready = new HashSet<Long>();
		Queue<Long> toProcess = new LinkedList<Long>();
		Long initial = code(mtsA, mtsB, mtsA.getInitialState(), mtsB.getInitialState());
		MTS<Long, Action> retValue = new MTSImpl<Long, Action>(initial);
		retValue.addActions(mtsA.getActions());
		retValue.addActions(mtsB.getActions());

		ready.add(initial);
		toProcess.add(initial);

		while (!toProcess.isEmpty()) {
			Long actual = toProcess.remove();
			retValue.addState(actual);
			Collection<Transition<Action>> composableTransitions = getComposableTransitions(mtsA, mtsB, actual);
			for (Iterator<Transition<Action>> it = composableTransitions.iterator(); it.hasNext();) {
				Transition<Action> transition = it.next();
//				retValue.addAction(transition.getAction());
				retValue.addState(transition.getTo());
				retValue.addTransition(actual, transition.getAction(), transition.getTo(), transition.getType());
				if (!ready.contains(transition.getTo())) {
					ready.add(transition.getTo());
					toProcess.add(transition.getTo());
				}
			}
		}
		
		return retValue;
	}

	private static <Action> Collection<Transition<Action>> getComposableTransitions(MTS<Long, Action> mtsA,
			MTS<Long, Action> mtsB, Long x) {
		Collection<Transition<Action>> retValue = new ArrayList<Transition<Action>>();
		Pair<Long, Long> state = decode(mtsA, mtsB, x);
		Map<Action, List<Pair<Long, TransitionType>>> fromA = groupByAction(getTransitions(mtsA, state.getFirst()));
		Map<Action, List<Pair<Long, TransitionType>>> fromB = groupByAction(getTransitions(mtsB, state.getSecond()));
		Collection<Action> allActions = new HashSet<Action>();
		allActions.addAll(fromA.keySet());
		allActions.addAll(fromB.keySet());
		for (Iterator<Action> it = allActions.iterator(); it.hasNext();) {
			Action action = it.next();
			boolean isInA = mtsA.getActions().contains(action);
			boolean isInB = mtsB.getActions().contains(action);
			boolean isInFromA = fromA.containsKey(action);
			boolean isInFromB = fromB.containsKey(action);
			if (isInA && isInB) {
				if (isInFromA && isInFromB) {
					for (Iterator<Pair<Long, TransitionType>> itA = fromA.get(action).iterator(); itA.hasNext();) {
						Pair<Long, TransitionType> toA = itA.next();
						for (Iterator<Pair<Long, TransitionType>> itB = fromB.get(action).iterator(); itB.hasNext();) {
							Pair<Long, TransitionType> toB = itB.next();
							Long nextState = code(mtsA, mtsB, toA.getFirst(), toB.getFirst());
							TransitionType type;
							if (toA.getSecond() == TransitionType.REQUIRED
									&& toB.getSecond() == TransitionType.REQUIRED) {
								type = TransitionType.REQUIRED;
							} else {
								type = TransitionType.MAYBE;
							}
							retValue.add(new Transition<Action>(nextState, action, type));
						}
					}
				}
			} else {
				if (fromA.get(action) != null) {
					for (Iterator<Pair<Long, TransitionType>> it2 = fromA.get(action).iterator(); it2.hasNext();) {
						Pair<Long, TransitionType> transition = it2.next();
						Long nextState = code(mtsA, mtsB, transition.getFirst(), state.getSecond());
						retValue.add(new Transition<Action>(nextState, action, transition.getSecond()));
					}
				} else {
					for (Iterator<Pair<Long, TransitionType>> it2 = fromB.get(action).iterator(); it2.hasNext();) {
						Pair<Long, TransitionType> transition = it2.next();
						Long nextState = code(mtsA, mtsB, state.getFirst(), transition.getFirst());
						retValue.add(new Transition<Action>(nextState, action, transition.getSecond()));
					}
				}
			}
		}
		return retValue;
	}

	private static <Action> Map<Action, List<Pair<Long, TransitionType>>> groupByAction(
			Collection<Transition<Action>> transitions) {
		Map<Action, List<Pair<Long, TransitionType>>> retValue = new HashMap<Action, List<Pair<Long, TransitionType>>>();
		for (Iterator<Transition<Action>> it = transitions.iterator(); it.hasNext();) {
			Transition<Action> transition = it.next();
			if (!retValue.containsKey(transition.getAction())) {
				retValue.put(transition.getAction(), new ArrayList<Pair<Long, TransitionType>>());
			}
			retValue.get(transition.getAction()).add(
					Pair.create(transition.getTo(), transition.getType()));
		}
		return retValue;
	}

	private static <Action> Collection<Transition<Action>> getTransitions(MTS<Long, Action> mts, Long state) {
		Collection<Transition<Action>> retValue = new ArrayList<Transition<Action>>();
		retValue.addAll(getTransitions(mts, state, TransitionType.MAYBE));
		retValue.addAll(getTransitions(mts, state, TransitionType.REQUIRED));
		return retValue;
	}

	private static <Action> Collection<Transition<Action>> getTransitions(MTS<Long, Action> mts, Long state,
			TransitionType type) {
		Collection<Transition<Action>> retValue = new ArrayList<Transition<Action>>();
		for (Iterator<Pair<Action, Long>> iter = mts.getTransitions(state, type).iterator(); iter.hasNext();) {
			Pair<Action, Long> transition = iter.next();
			retValue.add(new Transition<Action>(transition.getSecond(), transition.getFirst(), type));
		}
		return retValue;
	}

	private static <Action> Long code(MTS<Long, Action> mtsA, MTS<Long, Action> mtsB, Long a, Long b) {
		return a * mtsB.getStates().size() + b;
	}

	private static <Action> Pair<Long, Long> decode(MTS<Long, Action> mtsA, MTS<Long, Action> mtsB, Long x) {
		return Pair.create(x / mtsB.getStates().size(), x % mtsB.getStates().size());
	}

	private static class Transition<Action> {
		private Long to;

		private Action action;

		private TransitionType type;

		public Transition(Long to, Action action, TransitionType type) {
			super();
			this.to = to;
			this.action = action;
			this.type = type;
		}

		/**
		 * @return the action
		 */
		protected Action getAction() {
			return action;
		}

		/**
		 * @param action
		 *            the action to set
		 */
		protected void setAction(Action action) {
			this.action = action;
		}

		/**
		 * @return the to
		 */
		protected Long getTo() {
			return to;
		}

		/**
		 * @param to
		 *            the to to set
		 */
		protected void setTo(Long to) {
			this.to = to;
		}

		/**
		 * @return the type
		 */
		protected TransitionType getType() {
			return type;
		}

		/**
		 * @param type
		 *            the type to set
		 */
		protected void setType(TransitionType type) {
			this.type = type;
		}

	}
}
