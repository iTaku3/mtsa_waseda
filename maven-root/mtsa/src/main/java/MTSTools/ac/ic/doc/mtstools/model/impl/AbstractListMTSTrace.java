package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import MTSTools.ac.ic.doc.mtstools.model.MTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.MTSTransition;

/**
 * Abstract implementation of MTSTrace as a list
 * 
 * @author gsibay
 *
 * @param <A>
 * @param <S>
 */
public abstract class AbstractListMTSTrace<A, S> implements MTSTrace<A, S> {

	 
	protected abstract void setTransitionsList(List<MTSTransition<A, S>> trace);
	protected abstract List<MTSTransition<A, S>> getTransitionsList();
	
	/* (non-Javadoc)
	 * @see ac.ic.doc.mtstools.model.impl.MTSTrace#add(ac.ic.doc.mtstools.model.impl.MTSTransition)
	 */
	@Override
	public void add(MTSTransition<A, S> transition){
		this.getTransitionsList().add(transition);
	}

	/* (non-Javadoc)
	 * @see ac.ic.doc.mtstools.model.impl.MTSTrace#addFirst(ac.ic.doc.mtstools.model.impl.MTSTransition)
	 */
	@Override
	public void addFirst(MTSTransition<A, S> transition) {
		this.getTransitionsList().add(0, transition);
	}

	public ListIterator<MTSTransition<A, S>> listIterator() {
		return this.getTransitionsList().listIterator();
	}

	/* (non-Javadoc)
	 * @see ac.ic.doc.mtstools.model.impl.MTSTrace#clear()
	 */
	@Override
	public void clear() {
		this.getTransitionsList().clear();
	}

	/* (non-Javadoc)
	 * @see ac.ic.doc.mtstools.model.impl.MTSTrace#size()
	 */
	@Override
	public int size() {
		return this.getTransitionsList().size();
	}

	/* (non-Javadoc)
	 * @see ac.ic.doc.mtstools.model.impl.MTSTrace#remove(int)
	 */
	@Override
	public void remove(int i) {
		this.getTransitionsList().remove(i);
	}
	
	/* (non-Javadoc)
	 * @see ac.ic.doc.mtstools.model.impl.MTSTrace#removeLastTransition()
	 */
	@Override
	public void removeLastTransition() {
		this.remove(this.size()-1);
	}

	/*
	public ListIterator<MTSTransition<A, S>> listIterator(int index) {
		return this.getTransitionsList().listIterator(index);
	}*/

	/* (non-Javadoc)
	 * @see ac.ic.doc.mtstools.model.impl.MTSTrace#get(int)
	 */
	@Override
	public MTSTransition<A, S> get(int i) {
		return this.getTransitionsList().get(i);
	}

	@Override
	public ListIterator<MTSTransition<A, S>> listIterator(int index) {
		return this.getTransitionsList().listIterator(index);
	}
	
	@Override
	public Iterator<MTSTransition<A, S>> iterator() {
		return this.getTransitionsList().iterator();
	}

	public String toString() {
		return this.getTransitionsList().toString();
	}
	
	public boolean equals(Object obj) {
		if (this==obj){
			return true;
		}
		if (obj instanceof AbstractListMTSTrace) {
			@SuppressWarnings("unchecked")
			AbstractListMTSTrace<A, S> aTrace = (AbstractListMTSTrace<A, S>) obj;
			return new EqualsBuilder().append(this.getTransitionsList(), aTrace.getTransitionsList()).isEquals();
		}			
		return false;
	}
	
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.getTransitionsList()).toHashCode();
	}
}
