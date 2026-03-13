package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.*;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class MTSCompositionAdapter<Action> {

	private MTS<Long, Action> adapteeMTS;
	
	/*
	 * Cachea las transiciones de un mts desde un estado
	 */
	private Map<Long, Collection<Transition<Action>>> cacheTransitionsFromState;

	/*
	 * Cachea las transiciones de un mts desde un estado, agrupadas por action
	 */
	private Map<Long, Map<Action, List<Pair<Long, TransitionType>>>> cacheTransitionsFromStateGroupedByAction;
	

	public MTSCompositionAdapter(MTS<Long, Action> adapteeMTS) {
		this.adapteeMTS = adapteeMTS;
		cacheTransitionsFromState = new HashMap();
		cacheTransitionsFromStateGroupedByAction = new HashMap();
	}
	
	public Map<Action, List<Pair<Long, TransitionType>>> groupByAction(Long state) {
		if (!cacheTransitionsFromStateGroupedByAction.containsKey(state)) {
			Map<Action, List<Pair<Long, TransitionType>>> retValue = getTransitionsByLabel(state);
			cacheTransitionsFromStateGroupedByAction.put(state, retValue);
		}
		return (Map<Action, List<Pair<Long, TransitionType>>>) cacheTransitionsFromStateGroupedByAction.get(state);
	}

	private Map<Action, List<Pair<Long, TransitionType>>> getTransitionsByLabel(Long state) {
		Map<Action, List<Pair<Long, TransitionType>>> retValue = new HashMap<Action, List<Pair<Long, TransitionType>>>();
		for (Iterator it = getTransitions(state).iterator(); it.hasNext();) {
			Transition<Action> transition = (Transition<Action>) it.next();
			if (!retValue.containsKey(transition.getAction())) {
				retValue.put(transition.getAction(), new ArrayList<Pair<Long, TransitionType>>());
			}
			retValue.get(transition.getAction()).add(
					Pair.create(transition.getTo(), transition.getType()));
		}
		return retValue;
	}

	
	public Collection<Transition<Action>> getTransitions(Long state) {
		if (!cacheTransitionsFromState.containsKey(state)) {
			Collection<Transition<Action>> retValue = new ArrayList<Transition<Action>>();
			//Aca tal vez se pueda armar mas cosas
			//FIXME problemas con los generic s que pasa con esto
			retValue.addAll(getTransitions(state, TransitionType.REQUIRED));
			retValue.addAll(getTransitions(state, TransitionType.MAYBE));
			cacheTransitionsFromState.put(state, retValue);
		}
		return (Collection<Transition<Action>>) cacheTransitionsFromState.get(state);
	}
	
	public Collection<Transition<Action>> getTransitions(Long state, TransitionType type) {
		Map<TransitionType, Map<Long, Collection<Transition<Action>>>> cacheTransitionsByTypeAndByState = new HashMap<TransitionType, Map<Long, Collection<Transition<Action>>>>();
		//Me fijo si ya agregue el type al cache
		if (!cacheTransitionsByTypeAndByState.containsKey(type)) {
			cacheTransitionsByTypeAndByState.put(type, new HashMap<Long, Collection<Transition<Action>>>());
		}
		//me fijo si ya agregue las transiciones al map para ese type
		Map<Long, Collection<Transition<Action>>> localCacheTransitionByType = cacheTransitionsByTypeAndByState.get(type);
		if (!localCacheTransitionByType.containsKey(state)) {
			
			Collection<Transition<Action>> retValue = new ArrayList<Transition<Action>>();
			for (Pair<Action, Long> transition : adapteeMTS.getTransitions(state, type) ) {
				retValue.add(new Transition<Action>(transition.getSecond(), transition.getFirst(), type));
			}
			localCacheTransitionByType.put(state, retValue);
		}
		return localCacheTransitionByType.get(state);
	}

	public Long getInitialState() {
		return this.adapteeMTS.getInitialState();
	}

	public Set<Long> getStates() {
		return this.adapteeMTS.getStates();
	}

	/**
	 * @param action
	 * @return
	 * @see ac.ic.doc.mtstools.model.TransitionSystem#addAction(java.lang.Object)
	 */
	public boolean addAction(Action action) {
		return adapteeMTS.addAction(action);
	}

	/**
	 * @param actions
	 * @return
	 * @see ac.ic.doc.mtstools.model.TransitionSystem#addActions(java.util.Collection)
	 */
	public boolean addActions(Collection<? extends Action> actions) {
		return adapteeMTS.addActions(actions);
	}

	/**
	 * @param from
	 * @param label
	 * @param to
	 * @return
	 * @see ac.ic.doc.mtstools.model.MTS#addPossible(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public boolean addPossible(Long from, Action label, Long to) {
		return adapteeMTS.addPossible(from, label, to);
	}

	/**
	 * @param from
	 * @param label
	 * @param to
	 * @return
	 * @see ac.ic.doc.mtstools.model.MTS#addRequired(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public boolean addRequired(Long from, Action label, Long to) {
		return adapteeMTS.addRequired(from, label, to);
	}

	/**
	 * @param state
	 * @return
	 * @see ac.ic.doc.mtstools.model.TransitionSystem#addState(java.lang.Object)
	 */
	public boolean addState(Long state) {
		return adapteeMTS.addState(state);
	}

	/**
	 * @param states
	 * @return
	 * @see ac.ic.doc.mtstools.model.TransitionSystem#addStates(java.util.Collection)
	 */
	public boolean addStates(Collection<? extends Long> states) {
		return adapteeMTS.addStates(states);
	}

	/**
	 * @param from
	 * @param label
	 * @param to
	 * @param type
	 * @return
	 * @see ac.ic.doc.mtstools.model.MTS#addTransition(java.lang.Object, java.lang.Object, java.lang.Object, ac.ic.doc.mtstools.model.MTS.TransitionType)
	 */
	public boolean addTransition(Long from, Action label, Long to, TransitionType type) {
		return adapteeMTS.addTransition(from, label, to, type);
	}

	/**
	 * @return
	 * @see ac.ic.doc.mtstools.model.TransitionSystem#getActions()
	 */
	public Set<Action> getActions() {
		return adapteeMTS.getActions();
	}
	@Override
	public String toString() {
		return adapteeMTS.toString();
	}
	
}
