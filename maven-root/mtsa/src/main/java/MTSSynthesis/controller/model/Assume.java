package MTSSynthesis.controller.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections15.SetUtils;

//TODO rename to StateSetFormula --> remove guarantee as well
public class Assume<State> {
	private Set<State> states;
	
	public Assume() {
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
	/**
	 * Returns the number of elements in the assume that are not in the guarantee.
	 * @param guarantee
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int getDifferenceSize(Set<State> guarantee) {
		Collection<State> intersection = CollectionUtils.intersection(guarantee, this.states);
		return this.states.size()-intersection.size();
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
