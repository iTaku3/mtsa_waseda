package MTSTools.ac.ic.doc.mtstools.model.impl;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.MAYBE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections15.iterators.EmptyIterator;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class PathBuilder {

	private static PathBuilder singleton = new PathBuilder();

	static public PathBuilder getInstance() {
		return singleton;
	}

	public <S, A> Iterator<List<Pair<A, S>>> getPathsIterator
			(final MTS<S, A> mts,final S state,final A label,final TransitionType type, final Set<A> silentActions) {
		
		return new Iterator<List<Pair<A, S>>>() {

			private LinkedList<Pair<A, S>> actualPath;	
			private Set<S> actualStates;
			private LinkedList<Pair<A, S>> next;
			private Map<Integer,Queue<Pair<A, S>>> queue;
			private Iterator<S> endStateIt;
			
			 
			 {
				
				this.actualPath = new LinkedList<Pair<A, S>>();
				this.actualStates = new HashSet<S>();
				this.next = null;
				
				this.queue = new HashMap<Integer,Queue<Pair<A, S>>>();
				this.queue.put(0,new LinkedList<Pair<A, S>>());				
				this.queue.get(0).add((Pair<A, S>) Pair.create(null,state));
				
				this.endStateIt = EmptyIterator.getInstance();
				this.calculateNext();
			}
			
			public boolean hasNext() {
				return this.next != null;
				
			}

			public List<Pair<A, S>> next() {
				if (!this.hasNext()) {
					throw new NoSuchElementException();
				}
				List<Pair<A, S>> result = this.next;
				this.next = null;
				this.calculateNext();
				return result;
			}

			@SuppressWarnings("unchecked")
			private void calculateNext() {
				if (endStateIt.hasNext()) {
					this.next = (LinkedList<Pair<A, S>>) this.actualPath.clone();
					this.next.addLast(Pair.create(label,endStateIt.next()));
					return;
				}

				Pair<A,S> nextTransition = this.queue.get(this.actualPath.size()).poll();
				if (nextTransition == null) {
					if (this.actualPath.isEmpty()) {
						return;
					} else {
						Pair<A,S> removed = this.actualPath.removeLast();
						this.actualStates.remove(removed.getSecond());
						this.calculateNext();
					}
				} else {	
					if (this.actualStates.contains(nextTransition.getSecond())) {
						return;
					}
					this.actualPath.addLast(nextTransition);
					this.actualStates.add(nextTransition.getSecond());
					
					S newState = nextTransition.getSecond();
					BinaryRelation<A, S> transitions = mts.getTransitions(newState,type);
					
					this.endStateIt = this.getEndStateIt(newState,transitions);
					
					
					Queue<Pair<A,S>> silentTransitions = new LinkedList<Pair<A,S>>(); 
					for(A silentAction: silentActions ) {
						for(S toState: transitions.getImage(silentAction)) {
							if (!this.actualStates.contains(toState)) {
								silentTransitions.add((Pair<A, S>) Pair.create(silentAction,toState));
							}
						}
					}
					
					this.queue.put(this.actualPath.size(),silentTransitions);
					this.calculateNext();
				}				

			}

			private Iterator<S> getEndStateIt(S from, BinaryRelation<A, S> transitions ) {
				if (type!=MAYBE && silentActions.contains(label) && !transitions.getImage(label).contains(from)) {						
					// XXX improve performance
					Set<S> endStates = new HashSet<S>(transitions.getImage(label));
					endStates.add(from);
					return endStates.iterator();
				} else {
					return transitions.getImage(label).iterator();
				}				
			}
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}

}
