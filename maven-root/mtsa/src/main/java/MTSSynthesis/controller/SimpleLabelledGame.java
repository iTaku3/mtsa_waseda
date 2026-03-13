package MTSSynthesis.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import MTSSynthesis.controller.model.LabelledGame;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import MTSSynthesis.controller.model.Goal;

public class SimpleLabelledGame<S,A> implements LabelledGame<S,A> {
	protected LTS<S, A> env;
	protected Set<A> controllable;
	protected SetView<A> uncontrollable;

	protected Map<S, Set<S>> predecessors;
	protected Map<S, Set<S>> controllablePredecessors;
	protected Map<S, Set<S>> uncontrollablePredecessors;
	protected Map<S, Set<S>> successors;
	protected Map<S, Set<S>> controllableSuccessors;
	protected Map<S, Set<S>> uncontrollableSuccessors;
	protected Map<S, Set<A>> enabledActions;	
	protected Set<S> initialStates;
	protected Set<S> states;
	
	protected Map<S, Map<S, A>> labels;
	
	//TODO MTS -> LTS
	public SimpleLabelledGame(LTS<S, A> env, Set<A> controllable) {
		this.env = env;
		this.controllable = controllable;
		this.uncontrollable = Sets.difference(env.getActions(), controllable);
		
		initializeRelations();
		
		initialStates = new HashSet<S>();
		initialStates.add(env.getInitialState());		
	}
	
	
	private void initializeRelations() {
		this.states						= new HashSet<S>();
					
		this.predecessors				= new HashMap<S, Set<S>>();
		this.controllablePredecessors 	=  new HashMap<S, Set<S>>();
		this.uncontrollablePredecessors =  new HashMap<S, Set<S>>();
		this.successors					= new HashMap<S, Set<S>>();
		this.controllableSuccessors 	=  new HashMap<S, Set<S>>();
		this.uncontrollableSuccessors 	=  new HashMap<S, Set<S>>();
		this.enabledActions 			=  new HashMap<S, Set<A>>();
		this.labels						= new HashMap<S, Map<S, A>>();
		
		//add keys
		for (S state : env.getStates()) {
			this.states.add(state);
			this.enabledActions.put(state, new HashSet<A>());
			this.predecessors.put(state, new HashSet<S>());
			this.controllablePredecessors.put(state, new HashSet<S>());
			this.uncontrollablePredecessors.put(state, new HashSet<S>());
			this.successors.put(state, new HashSet<S>());
			this.controllableSuccessors.put(state, new HashSet<S>());
			this.uncontrollableSuccessors.put(state, new HashSet<S>());
		}
		//process transitions
		Map<S, BinaryRelation<A, S>> trs = env.getTransitions();
		for (Map.Entry<S, BinaryRelation<A, S>> statesTrs : trs.entrySet()) {
			S pred = statesTrs.getKey();
			
			if(!states.contains(pred))
				states.add(pred);
			
			if(labels.get(pred) == null)
				labels.put(pred, new HashMap<S, A>());
			
			for (Pair<A, S> s : statesTrs.getValue()) {
				S succ	= s.getSecond();
				if(!env.getStates().contains(succ))
//					System.out.println("eror");
				this.enabledActions.get(pred).add(s.getFirst());
				
				predecessors.get(s.getSecond()).add(pred);
				successors.get(pred).add(succ);
				
				labels.get(pred).put(succ,s.getFirst());
				
				if(controllable.contains(s.getFirst())){
						controllablePredecessors.get(succ).add(pred);
						controllableSuccessors.get(pred).add(succ);
				}
				else{
						uncontrollablePredecessors.get(succ).add(pred);
						uncontrollableSuccessors.get(pred).add(succ);
				}
				if(!states.contains(succ))
					states.add(succ);
			}
		}
	}	
	
	
	public Set<S> getInitialStates(){
		return initialStates;
	}

	public Set<A> enabledActions(S state){
		return this.enabledActions(state);
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
		return this.enabledActions(smallState).containsAll(actions);
	}


	@Override
	public Set<S> getUncontrollableSuccessors(S state) {
		return this.uncontrollableSuccessors.get(state);
	}

	@Override
	public Set<S> getControllableSuccessors(S state) {
		return this.controllableSuccessors.get(state);
	}

	@Override
	public Set<S> getPredecessors(S state) {
		return predecessors.get(state);
	}

	@Override
	public Set<S> getStates() {
		return this.states;
	}

	@Override
	public boolean isUncontrollable(S state) {
		return !(this.uncontrollableSuccessors.get(state).isEmpty());
	}

	public boolean hasControllable(S state) {
		return !(this.controllableSuccessors.get(state).isEmpty());
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
		return this.successors.get(state) ;
	}


	@Override
	public A getLabel(S outgoing, S incoming) {
		return labels.get(outgoing).get(incoming);
	}


	@Override
	public void addControllableSuccessor(S state1, A label, S state2) {
		throw new UnsupportedOperationException();
	}


	@Override
	public void addUncontrollableSuccessor(S predecessor, A label, S successor) {
		throw new UnsupportedOperationException();
	}


	@Override
	public Goal getGoal() {
		// TODO Auto-generated method stub
		return null;
	}
}

