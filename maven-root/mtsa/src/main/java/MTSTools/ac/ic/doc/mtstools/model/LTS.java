package MTSTools.ac.ic.doc.mtstools.model;

import java.util.Map;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;

public interface LTS<State, Action> extends TransitionSystem<State, Action> {

	public abstract Map<State, BinaryRelation<Action, State> > getTransitions();
	public abstract BinaryRelation<Action, State> getTransitions(State state);
	public boolean addTransition(State from, Action label, State to);
	public boolean removeTransition(State from, Action label, State to);
}
