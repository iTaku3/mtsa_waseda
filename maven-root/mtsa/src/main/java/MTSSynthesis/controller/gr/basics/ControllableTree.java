package MTSSynthesis.controller.gr.basics;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang.Validate;

public class ControllableTree<State>{
	StateTreeDecisionPoints<State, Integer> root;
	Set<StateTreeDecisionPoints<State, Integer>> states;
	StateTreeDecisionPoints<State, Integer> actual;
	Set<StateTreeDecisionPoints<State, Integer>> discovered;
	Stack<StateTreeDecisionPoints<State, Integer>> nexts;
	public ControllableTree(StateTreeDecisionPoints<State, Integer> root, Set<StateTreeDecisionPoints<State, Integer>> states) {
		Validate.isTrue(states.contains(root));
		this.states = states;
		this.root = root;
		this.actual = root;
		this.discovered = new HashSet<StateTreeDecisionPoints<State,Integer>>();
		this.nexts = new Stack<StateTreeDecisionPoints<State, Integer>>();
		discover();
	}
	
	public boolean add(){
		boolean overflow = false;
		
		if(nexts.isEmpty()){
			overflow  =  true;
		}else{
			addTraversal();
			if(!nexts.isEmpty()){
				discover();
			}else{
				overflow  =  true;
			}
		}
		return overflow;
	}

	private void addTraversal() {
		StateTreeDecisionPoints<State, Integer> v = nexts.peek(); 
		
		while(!nexts.isEmpty() && v.add()){
			discovered.remove(nexts.pop());
			if(!nexts.isEmpty())
				v = nexts.peek();
		}
	}

	private void discover() {
		Queue<StateTreeDecisionPoints<State, Integer>> s =  new LinkedList<StateTreeDecisionPoints<State, Integer>>();
		s.add(root);
		while (!s.isEmpty()){
			StateTreeDecisionPoints<State, Integer> v = s.poll(); 
			if (!discovered.contains(v)){
				discovered.add(v);
				nexts.push(v);
				for (StateTreeDecisionPoints<State, Integer>w: v.getNextDecisionPoints()){
					s.add(w);
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return root.toString();
	}
	
	public Set<StateTreeDecisionPoints<State, Integer>> getPoints() {
		return discovered;
	}
	
}
