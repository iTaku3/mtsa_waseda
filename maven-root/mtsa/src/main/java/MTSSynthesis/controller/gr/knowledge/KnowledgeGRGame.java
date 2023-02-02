package MTSSynthesis.controller.gr.knowledge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import MTSSynthesis.controller.model.Game;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import MTSSynthesis.controller.model.gr.GRGoal;

public class KnowledgeGRGame<S,A> implements Game<Set<S>> {
	private MTS<Set<S>, A> det;
	private MTS<S, A> nondet;
	private Set<A> controllable;
	private Map<Set<S>, Set<Set<S>>> predecessors;
	private GRGoal<Set<S>> goal;
	private SetView<A> uncontrollable;
	private Set<Set<S>> initialStates;

	//TODO MTS -> LTS
	public KnowledgeGRGame(Set<Set<S>> initialStates, MTS<S, A> nondetMts, MTS<Set<S>, A> detMts, Set<A> controllable, GRGoal<Set<S>> goal) {
		this.initialStates = initialStates;
		this.det = detMts;
		this.nondet = nondetMts;
		this.controllable = controllable;
		this.uncontrollable = Sets.difference(det.getActions(), controllable);
		this.goal = goal;
		initialisePredecessors();
	}

	public Set<Set<S>> getInitialStates(){
		return initialStates;
	}
	
	public Set<A> enabledActions(Set<S> state){
		HashSet<A> result = new HashSet<A>();
		BinaryRelation<A, Set<S>> trs = det.getTransitions(state, TransitionType.POSSIBLE);
		for (Iterator<Pair<A, Set<S>>> it = trs.iterator(); it.hasNext();) {
			Pair<A, Set<S>> tr = it.next();
			result.add(tr.getFirst());
		}
		return result;
	}
	
	/**
	 * Every smallState in <i>state</i> has an enabled action in <i>actions</i>.
	 * 
	 * @param state
	 * @param actions
	 * @return
	 */
	public boolean isEnabled(Set<S> state, Set<A> actions) {
		for (S smallState : state) {
			if (!isEnabled(smallState,actions)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isEnabled(S smallState, Set<A> actions) {
		BinaryRelation<A, S> trs = nondet.getTransitions(smallState, TransitionType.POSSIBLE);
		for (Iterator<Pair<A, S>> iterator = trs.iterator(); iterator.hasNext();) {
			Pair<A, S> pair = iterator.next();
			if (actions.contains(pair.getFirst())) {
				return true;
			}
		}
		return false;
	}
	
	private void initialisePredecessors() {
		this.predecessors =  new HashMap<Set<S>, Set<Set<S>>>();
		for (Set<S> state : det.getStates()) {
			this.predecessors.put(state, new HashSet<Set<S>>());
		}
		
		Map<Set<S>, BinaryRelation<A, Set<S>>> trs = det.getTransitions(TransitionType.POSSIBLE);
		for (Map.Entry<Set<S>, BinaryRelation<A, Set<S>>> statesTrs : trs.entrySet()) {
			Set<S> pred = statesTrs.getKey();
			for (Pair<A, Set<S>> s : statesTrs.getValue()) {
				predecessors.get(s.getSecond()).add(pred);
			}
		}
	}

	@Override
	public Set<Set<S>> getUncontrollableSuccessors(Set<S> state) {
		Set<Set<S>> result = new HashSet<Set<S>>();
		BinaryRelation<A, Set<S>> trs = det.getTransitions(state, TransitionType.POSSIBLE);
		for (Iterator<Pair<A, Set<S>>> it = trs.iterator(); it.hasNext();) {
			Pair<A, Set<S>> tr = it.next();
			if (!controllable.contains(tr.getFirst())) {
				result.add(tr.getSecond());
			}
		}
		return result;
	}

	@Override
	public Set<Set<S>> getControllableSuccessors(Set<S> state) {
		//TODO Merge with getUncontrollableSucc...
		Set<Set<S>> result = new HashSet<Set<S>>();
		BinaryRelation<A, Set<S>> trs = det.getTransitions(state, TransitionType.POSSIBLE);
		for (Iterator<Pair<A, Set<S>>> it = trs.iterator(); it.hasNext();) {
			Pair<A, Set<S>> tr = it.next();
			if (controllable.contains(tr.getFirst())) {
				result.add(tr.getSecond());
			}
		}
		return result;
	}

	@Override
	public Set<Set<S>> getPredecessors(Set<S> state) {
		return predecessors.get(state);
	}

	@Override
	public Set<Set<S>> getStates() {
		return det.getStates();
	}

	@Override
	public boolean isUncontrollable(Set<S> state) {
		for (A action : this.uncontrollable) {
			if (!det.getTransitions(state, TransitionType.POSSIBLE).getImage(action).isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	//DIPI Game class needs refactoring, methods below needs to be reestructured. 
	@Override
	public void addUncontrollableSuccessor(Set<S> predecessor, Set<S> successor) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void addControllableSuccessor(Set<S> state1, Set<S> state2) {
		throw new UnsupportedOperationException();
	}

	public GRGoal<Set<S>> getGoal() {
		return goal;
	}

	public MTS<Set<S>, A> getDetMTS() {
		 return this.det;
	}

	public Set<A> getUncontrollable() {
		return uncontrollable;
	}
	
	public Set<A> getControllable() {
		return controllable;
	}
	
	@Override
	public Set<Set<S>> getSuccessors(Set<S> state) {
		Set<Set<S>> result = new HashSet<Set<S>>();
		result.addAll(this.getUncontrollableSuccessors(state));
		result.addAll(this.getControllableSuccessors(state));
		return result ;
	}
}
