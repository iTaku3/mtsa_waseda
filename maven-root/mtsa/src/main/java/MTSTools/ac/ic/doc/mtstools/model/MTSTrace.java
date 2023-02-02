package MTSTools.ac.ic.doc.mtstools.model;

import java.util.ListIterator;


/**
 * A trace is a sequence of transitions. 
 * 
 * @author gsibay
 *
 * @param <A>
 * @param <S>
 */
public interface MTSTrace<A, S> extends Iterable<MTSTransition<A, S>> {

	/**
	 * Adds the transition at the end of the trace
	 * @param transition
	 */
	public abstract void add(MTSTransition<A, S> transition);

	
	/**
	 * Adds the transition at the beginning of the trace
	 * @param transition
	 */
	public abstract void addFirst(MTSTransition<A, S> transition);
	
	/**
	 * Clears the trace leaving it without any transitions. Turns the trace
	 * into an empty trace.
	 */
	public abstract void clear();

	/**
	 * Returns the trace's number of transitions
	 * @return
	 */
	public abstract int size();

	/**
	 * Removes the transition at the end of the trace
	 */
	public abstract void removeLastTransition();

	/**
	 * Gets the transition in position i of the trace.
	 * Positions are numbered as done in a List.
	 * 
	 * @param i
	 * @return
	 */
	public abstract MTSTransition<A, S> get(int i);

	/**
	 * Removes transition at position i of the trace.
	 * Positions are numbered as done in a List.
	 * @param i
	 */
	public abstract void remove(int i);
	
	/**
	 * Gets a ListIterator for the transitions starting at the indicated position
	 * @param transition
	 */
	public abstract ListIterator<MTSTransition<A, S>> listIterator(int index);

}