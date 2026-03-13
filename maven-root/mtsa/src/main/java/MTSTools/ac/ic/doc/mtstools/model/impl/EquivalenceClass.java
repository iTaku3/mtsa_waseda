package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public interface EquivalenceClass<State> extends Iterable<State> {

	public abstract boolean addState(State state);

	public abstract boolean hasState(State state);

	public abstract Iterator<State> iterator();

	public abstract void addAllStates(Collection<State> classToAdd);

	public abstract boolean hasAll(EquivalenceClass<State> equivalenceClassB);
	
	public abstract Set<State> getStates(); 

}