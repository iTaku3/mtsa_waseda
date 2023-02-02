package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

public class UnmodifiableMTS<State, Action> implements MTS<State, Action> {

	public static <State, Action> MTS<State, Action> decorate(MTS<State, Action> mts) {
		return new UnmodifiableMTS<State, Action>(mts);
	}
	
	private MTS<State, Action> wrappedMts;
	
	public UnmodifiableMTS(MTS<State, Action> mts) {
		this.wrappedMts = mts;
	}
		
	@Override
	public Set<State> getStates() {
		return this.wrappedMts.getStates();
	}

	@Override
	public Set<Action> getActions() {
		return this.wrappedMts.getActions();
	}

	@Override
	public State getInitialState() {
		return this.wrappedMts.getInitialState();
	}

	@Override
	public boolean addState(State state) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addStates(Collection<? extends State> states) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAction(Action action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addActions(Collection<? extends Action> actions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAction(Action action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeUnreachableStates() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<State, BinaryRelation<Action, State>> getTransitions(
			MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType type) {
		return Collections.unmodifiableMap(this.wrappedMts.getTransitions(type));
	}

	@Override
	public BinaryRelation<Action, State> getTransitions(State state,
		MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType type) {
		return this.wrappedMts.getTransitions(state, type);
	}

	@Override
	public boolean addTransition(State from, Action label, State to,
		MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addRequired(State from, Action label, State to) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addPossible(State from, Action label, State to) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeTransition(State from, Action label, State to,
		MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removePossible(State from, Action label, State to) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeRequired(State from, Action label, State to) {
		throw new UnsupportedOperationException();
	}

  @Override
  public void setInitialState(State state)
  {
    throw new UnsupportedOperationException();
  }
}
