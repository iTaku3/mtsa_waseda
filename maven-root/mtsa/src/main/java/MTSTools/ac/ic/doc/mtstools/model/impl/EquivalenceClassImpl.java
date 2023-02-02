package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.*;

public class EquivalenceClassImpl<State> implements EquivalenceClass<State>{
	
	private Set<State> states;
	private Long id;
	
	public EquivalenceClassImpl(){
		this.states = new HashSet<State>();
	}

	/* (non-Javadoc)
	 * @see ac.ic.doc.mtstools.model.impl.EquivalenceClass#addState(State)
	 */
	public boolean addState(State state) {
		return states.add(state);
	}
	
	/* (non-Javadoc)
	 * @see ac.ic.doc.mtstools.model.impl.EquivalenceClass#hasState(State)
	 */
	public boolean hasState(State state) {
		return states.contains(state);
	}
	
	/**
	 * @return the id
	 */
	protected Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see ac.ic.doc.mtstools.model.impl.EquivalenceClass#iterator()
	 */
	public Iterator<State> iterator() {
		return states.iterator();
	}
	
	/* (non-Javadoc)
	 * @see ac.ic.doc.mtstools.model.impl.EquivalenceClass#addAllStates(java.util.Set)
	 */
	public void addAllStates(Collection<State> classToAdd) {
		states.addAll(classToAdd);
	}

	public boolean hasAll(EquivalenceClass<State> equivalenceClassB) {
		for(State state : equivalenceClassB)
		    if(!hasState(state)){
		    	return false;
		    }
		return true;
	}
	
	@Override
	public String toString() {
		return states.toString();
	}
	@Override
	public boolean equals(Object obj) {
		if (obj==this){
			return true;
		}
		try {
			EquivalenceClass<State> equivalenceClass = (EquivalenceClass<State>) obj;
			return states.equals(equivalenceClass.getStates());
			
		} catch (RuntimeException e) {
			return false;
		}
		
	}
	@Override
	public int hashCode() {
		return states.hashCode();
	}

	public Set<State> getStates() {
		return states;
	}
}
