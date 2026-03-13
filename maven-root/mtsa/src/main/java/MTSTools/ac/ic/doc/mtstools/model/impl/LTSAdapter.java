package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class LTSAdapter<State, Action> implements LTS<State, Action> {

	private MTS<State, Action> mts;
	private TransitionType exposedBehaviour;

	public LTSAdapter(MTS<State, Action> mts, TransitionType exposedBehaviour) {
		this.mts = mts;
		this.exposedBehaviour = exposedBehaviour;
	}
	
	public Set<State> getStates() {
		return this.mts.getStates();
	}
	public Set<Action> getActions() {
		return this.mts.getActions();
	}
	public State getInitialState() {
		return this.mts.getInitialState();
	}
	public Map<State, BinaryRelation<Action, State>> getTransitions() {
		return this.mts.getTransitions(this.exposedBehaviour);
	}
	public BinaryRelation<Action, State> getTransitions(State state) {
		return this.mts.getTransitions(state, this.exposedBehaviour);
	}
	public boolean addState(State state) {
		return this.mts.addState(state);
	}
	public boolean addStates(Collection<? extends State> states) {
		return this.mts.addStates(states);
	}
	public boolean addAction(Action action) {
		return this.mts.addAction(action);
	}
	public boolean addActions(Collection<? extends Action> actions) {
		return this.mts.addActions(actions);
	}
	public boolean addTransition(State from, Action label, State to) {
		return this.mts.addTransition(from, label, to, this.exposedBehaviour);
	}

	public boolean removeTransition(State from, Action label, State to) {
		return this.mts.removeTransition(from, label, to, this.exposedBehaviour);
	}

	public boolean removeUnreachableStates() {
		return this.mts.removeUnreachableStates();
	}

	public void removeAction(Action action) {
		mts.removeAction(action);
	}

  @Override
  public void setInitialState(State state)
  {
    mts.setInitialState(state);
  }
}
