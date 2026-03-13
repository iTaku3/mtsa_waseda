package MTSSynthesis.controller.oppositeSafe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import MTSSynthesis.controller.model.Game;
import MTSSynthesis.controller.model.Goal;

public class OppositeSafeGame<S,A> implements Game<S> {
	protected LTS<S, A> env;
	protected Set<A> controllable;
	protected SetView<A> uncontrollable;

	protected Map<S, Set<S>> predecessors;
	
	//TODO MTS -> LTS
	public OppositeSafeGame(LTS<S, A> env, Set<A> controllable) {
		this.env = env;
		this.controllable = controllable;
		this.uncontrollable = Sets.difference(env.getActions(), controllable);
		
		initialisePredecessors();
	}
	
	public Set<S> getInitialStates(){
		Set<S> initialStates = new HashSet<S>();
		initialStates.add(env.getInitialState());
		return initialStates;
	}

	public Set<A> enabledActions(S state){
		HashSet<A> result = new HashSet<A>();
		BinaryRelation<A, S> trs = env.getTransitions(state);
		for (Iterator<Pair<A, S>> it = trs.iterator(); it.hasNext();) {
			Pair<A, S> tr = it.next();
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
		BinaryRelation<A, S> trs = env.getTransitions(smallState);
		for (Iterator<Pair<A, S>> iterator = trs.iterator(); iterator.hasNext();) {
			Pair<A, S> pair = iterator.next();
			if (actions.contains(pair.getFirst())) {
				return true;
			}
		}
		return false;
	}
	
	private void initialisePredecessors() {
		this.predecessors =  new HashMap<S, Set<S>>();
		for (S state : env.getStates()) {
			this.predecessors.put(state, new HashSet<S>());
		}
		
		Map<S, BinaryRelation<A, S>> trs = env.getTransitions();
		for (Map.Entry<S, BinaryRelation<A, S>> statesTrs : trs.entrySet()) {
			S pred = statesTrs.getKey();
			for (Pair<A, S> s : statesTrs.getValue()) {
				predecessors.get(s.getSecond()).add(pred);
			}
		}
	}

	@Override
	public Set<S> getUncontrollableSuccessors(S state) {
		Set<S> result = new HashSet<S>();
		BinaryRelation<A, S> trs = env.getTransitions(state);
		for (Iterator<Pair<A, S>> it = trs.iterator(); it.hasNext();) {
			Pair<A, S> tr = it.next();
			if (!controllable.contains(tr.getFirst())) {
				result.add(tr.getSecond());
			}
		}
		return result;
	}

	@Override
	public Set<S> getControllableSuccessors(S state) {
		//TODO Merge with getUncontrollableSucc...
		Set<S> result = new HashSet<S>();
		BinaryRelation<A, S> trs = env.getTransitions(state);
		for (Iterator<Pair<A, S>> it = trs.iterator(); it.hasNext();) {
			Pair<A, S> tr = it.next();
			if (controllable.contains(tr.getFirst())) {
				result.add(tr.getSecond());
			}
		}
		return result;
	}

	@Override
	public Set<S> getPredecessors(S state) {
		return predecessors.get(state);
	}

	@Override
	public Set<S> getStates() {
		return env.getStates();
	}

	@Override
	public boolean isUncontrollable(S state) {
		for (A action : this.uncontrollable) {
			if (!env.getTransitions(state).getImage(action).isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	//DIPI Game class needs refactoring, methods below needs to be reestructured. 
	@Override
	public void addUncontrollableSuccessor(S predecessor, S successor) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void addControllableSuccessor(S state1, S state2) {
		throw new UnsupportedOperationException();
	}

	public LTS<S, A> getEnvironment() {
		 return this.env;
	}

	public Set<A> getUncontrollable() {
		return uncontrollable;
	}
	
	public Set<A> getControllable() {
		return controllable;
	}
	
	@Override
	public Set<S> getSuccessors(S state) {
		Set<S> result = new HashSet<S>();
		result.addAll(this.getUncontrollableSuccessors(state));
		result.addAll(this.getControllableSuccessors(state));
		return result ;
	}

	@Override
	public Goal getGoal() {
		throw new NullPointerException("Missing implementation for this method!!!!");	
	}	
}
