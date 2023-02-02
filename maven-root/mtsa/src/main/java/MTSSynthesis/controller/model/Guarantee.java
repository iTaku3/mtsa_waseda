package MTSSynthesis.controller.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.SetUtils;

//TODO class to remove
public class Guarantee<State> {
	private Set<State> states;
	
	public Guarantee() {
		this.states = new HashSet<State>();
	}

	public boolean addState(State state) {
		return this.states.add(state);
	}

	public boolean addStates(Set<State> states) {
		return this.states.addAll(states);
	}

	public boolean contains(State state) {
		return this.states.contains(state); 
	}

	public Set<State> getStateSet() {
		return SetUtils.unmodifiableSet(this.states);
	}
	
	public boolean isEmpty() {
		return this.states.isEmpty();
	}
	
	@Override
	public String toString() {
		return this.states.toString();
	}
}
