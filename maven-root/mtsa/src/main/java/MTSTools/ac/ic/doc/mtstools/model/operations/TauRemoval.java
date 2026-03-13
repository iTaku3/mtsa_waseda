package MTSTools.ac.ic.doc.mtstools.model.operations;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.*;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.MapSetBinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;

public class TauRemoval {
	
	private static class IncompleteSet<T> extends AbstractSet<T> {
		public int size() { return 0; }
		public boolean equals(Object o) { return o != null && o instanceof IncompleteSet; };
		public Iterator<T> iterator() { return emptyIterator(); }
	};
	
	private static final Set incomplete = new IncompleteSet();
	
	private static final Set sink = singleton(-1);

	public static <State, Action> 
	void apply(MTS<State, Action> mts, Action tau) {
		Set<State> conducing = getConducing(mts, tau);
		Map<State, BinaryRelation<Action, State>> walks = walkReach(conducing, mts, tau);
		Map<State, BinaryRelation<Action, State>> transitions = mts.getTransitions(TransitionType.REQUIRED);
		for (State state : mts.getStates())
			transitions.put(state, new MapSetBinaryRelation<Action, State>());
		for (State state : conducing) {
			for (Pair<Action, State> pair : walks.get(state))
				mts.addTransition(
					state, pair.getFirst(), pair.getSecond(), TransitionType.REQUIRED);
		}
	}
	
	public static <State, Action>
	Set<State> getConducing(MTS<State, Action> mts, Action tau) {
		Set<State> result = new HashSet<State>();
		for (State state : mts.getStates()) {
			BinaryRelation<Action, State> relation =
				mts.getTransitions(state, TransitionType.REQUIRED);
			if (relation.isEmpty()) {
				result.add(state);
			} else for (Pair<Action, State> pair : relation) {
				if (!pair.getFirst().equals(tau)) {
					result.add(state);
					break;
				}
			}
		}
		return result;
	}
	
	public static <State, Action>
	Map<State, BinaryRelation<Action, State>> walkReach(Set<State> conducing, MTS<State, Action> mts, Action tau) {
		Map<State, Set<State>> taus = tauReach(conducing, mts, tau);
		Map<State, BinaryRelation<Action, State>> steps =
			new HashMap<State, BinaryRelation<Action, State>>();
		for (State state : mts.getStates()) {
			BinaryRelation<Action, State> relation = new MapSetBinaryRelation<Action, State>();
			for (Pair<Action, State> pair : mts.getTransitions(state, TransitionType.REQUIRED))
				if (!pair.getFirst().equals(tau)) {
					for (State destination : taus.get(pair.getSecond()))
						relation.addPair(pair.getFirst(), destination);
				}
			steps.put(state, relation);
		}
		Map<State, BinaryRelation<Action, State>> walks =
			new HashMap<State, BinaryRelation<Action, State>>();
		for (State state : conducing) {
			BinaryRelation<Action, State> relation = new MapSetBinaryRelation<Action, State>();
			relation.addAll(steps.get(state));
			for (State tauReachable : taus.get(state))
				relation.addAll(steps.get(tauReachable));
			walks.put(state, relation);
		}
		return walks;
	}
	
	public static <State, Action>
	Map<State, Set<State>> tauReach(Set<State> conducing, MTS<State, Action> mts, Action tau) {
		Map<State, Set<State>> taus = new HashMap<State, Set<State>>();
		for (State state : mts.getStates()) {
			tauReachRecursive(conducing, mts, state, tau, taus);
		}
		return taus;
	}
	
	public static <State, Action>
	Set<State> tauReachRecursive(Set<State> conducing, MTS<State, Action> mts, State state, Action tau, Map<State, Set<State>> taus) {
		Set<State> result = taus.get(state);
		if (result != null)
			return result;
		taus.put(state, incomplete);
		result = new HashSet<State>();
		BinaryRelation<Action, State> relation = mts.getTransitions(state, TransitionType.REQUIRED);
		Set<State> tauReachables = relation.getImage(tau);
		for (State tauReachable : tauReachables) {
			Set<State> reachable = tauReachRecursive(conducing, mts, tauReachable, tau, taus);
			if (reachable == incomplete || reachable == sink) {
				result = sink;
				break;
			} else {
				result.addAll(reachable);
			}
		}
		if (result != sink && conducing.contains(state))
			result.add(state);
		taus.put(state, result);
		return result;
	}
	
	public static <State, Action>
	void muAddition(MTS<State, Action> mts, Action mu, Action tau) {
		State s0 = mts.getInitialState();
		BinaryRelation<Action, State> relation = mts.getTransitions(s0, TransitionType.REQUIRED);
		Set<State> tauReachables = relation.getImage(tau);
		if (!tauReachables.isEmpty())
			mts.addAction(mu);
		for (State tauReachble : tauReachables)
			mts.addRequired(s0, mu, tauReachble);
	}
	
	public static <State, Action>
	void muRemoval(MTS<State, Action> mts, Action mu) {
		TraceInclusionClosure.getInstance().applyMTSClosure(mts, Collections.singleton(mu));
	}
	
}
