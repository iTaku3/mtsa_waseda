package MTSTools.ac.ic.doc.mtstools.model;

import java.util.Map;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;

public interface MTS<State, Action> extends TransitionSystem<State, Action>{

	public abstract Map<State, BinaryRelation<Action, State>> getTransitions(TransitionType type);

	public abstract BinaryRelation<Action, State> getTransitions(State state, TransitionType type);

	public boolean addTransition(State from, Action label, State to, TransitionType type);
	
	public boolean addRequired(State from, Action label, State to);
	public boolean addPossible(State from, Action label, State to);
	
	public boolean removeTransition(State from, Action label, State to, TransitionType type);
	public boolean removePossible(State from, Action label, State to);
	public boolean removeRequired(State from, Action label, State to);
	
//	public Set<State> getReachableStatesBy(State state, TransitionType transitionType);
	
	public enum TransitionType {
		REQUIRED {
			public <State, Action> boolean addTransition(MTS<State, Action> mts, State from, Action label, State to ) {
				return mts.addRequired(from, label, to);
			}
			public <State, Action> boolean removeTransition(MTS<State, Action> mts, State from, Action label, State to ){
				return mts.removeRequired(from, label, to);
			}
		},
		POSSIBLE {
			public <State, Action> boolean addTransition(MTS<State, Action> mts, State from, Action label, State to ) {
				return mts.addPossible(from, label, to);
			}
			public <State, Action> boolean removeTransition(MTS<State, Action> mts, State from, Action label, State to ){
				return mts.removePossible(from, label, to);
			}
		},  
		MAYBE {
			public <State, Action> boolean addTransition(MTS<State, Action> mts, State from, Action label, State to ) {
				return mts.addPossible(from, label, to);
			}
			public <State, Action> boolean removeTransition(MTS<State, Action> mts, State from, Action label, State to ){
				return mts.removePossible(from, label, to);
			}
		};

		public abstract <State, Action> boolean addTransition(MTS<State, Action> mts, State from, Action label, State to );
		public abstract <State, Action> boolean removeTransition(MTS<State, Action> mts, State from, Action label, State to );
		
	}
}
