package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MarkedLTS;

public class MarkedLTSAdapter<State, Action> implements MarkedLTS<State, Action> {

	private LTS<State, Action> lts;
	private Set<State> marked;
	private State error;
	
	public MarkedLTSAdapter(LTS<State,Action> lts) {
		this.lts = lts;
		this.marked = new HashSet<>(lts.getStates());
	}
	
	public MarkedLTSAdapter(LTS<State,Action> lts, State error) {
		this(lts);
		this.error = error;
		unmark(error);
	}
	
	@Override
	public Map<State, BinaryRelation<Action, State>> getTransitions() {
		return lts.getTransitions();
	}

	@Override
	public BinaryRelation<Action, State> getTransitions(State state) {
		return lts.getTransitions(state);
	}

	@Override
	public boolean addTransition(State from, Action label, State to) {
		return lts.addTransition(from, label, to);
	}

	@Override
	public boolean removeTransition(State from, Action label, State to) {
		return lts.removeTransition(from, label, to);
	}

	@Override
	public Set<State> getStates() {
		return lts.getStates();
	}

	@Override
	public Set<Action> getActions() {
		return lts.getActions();
	}

	@Override
	public State getInitialState() {
		return lts.getInitialState();
	}

	@Override
	public boolean addState(State state) {
		return lts.addState(state);
	}

	@Override
	public boolean addStates(Collection<? extends State> states) {
		return lts.addStates(states);
	}

	@Override
	public boolean addAction(Action action) {
		return lts.addAction(action);
	}

	@Override
	public boolean addActions(Collection<? extends Action> actions) {
		return lts.addActions(actions);
	}

	@Override
	public void removeAction(Action action) {
		lts.removeAction(action);
		
	}

	@Override
	public boolean removeUnreachableStates() {
		boolean result = lts.removeUnreachableStates();
		marked.clear();
		marked.addAll(lts.getStates());
		if (error != null)
			marked.remove(error);
		return result;
	}

	@Override
	public void setInitialState(State state) {
		lts.setInitialState(state);
	}

	@Override
	public Set<State> getMarkedStates() {
		return marked;
	}

	@Override
	public boolean mark(State state) {
		return marked.add(state);
	}

	@Override
	public boolean unmark(State state) {
		return marked.remove(state);
	}

	@Override
	public boolean isMarked(State state) {
		return marked.contains(state);
	}

}
