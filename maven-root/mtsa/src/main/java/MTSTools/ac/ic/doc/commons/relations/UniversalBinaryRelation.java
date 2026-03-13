package MTSTools.ac.ic.doc.commons.relations;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections15.iterators.EmptyIterator;

/**
 * Represents the cartesian product of the parameter collections
 *
 * @param <E1>
 * @param <E2>
 */
public class UniversalBinaryRelation<E1, E2> extends AbstractSet<Pair<E1,E2>> implements BinaryRelation<E1, E2> {

	private Set<E1> col1;
	private Set<E2> col2;
	
	public UniversalBinaryRelation(Set<? extends E1> col1, Set<? extends E2> col2) {
		this.col1 = Collections.unmodifiableSet(col1);
		this.col2 = Collections.unmodifiableSet(col2);
	}
		
	public int size() {
		return this.col1.size() * this.col2.size();
	}

	@SuppressWarnings("unchecked")
	public boolean contains(Object o) {
		if (!(o instanceof Pair)) {
			return false;
		}
		Pair pair = (Pair) o;
		return this.col1.contains(pair.getFirst()) && this.col2.contains(pair.getSecond());
	}

	public Iterator<Pair<E1,E2>> iterator() {
		return new Iterator<Pair<E1,E2>>() {

			private Iterator<E1> it1;
			private Iterator<E2> it2;
			private Pair<E1,E2> next;
			
			{
				this.it1 = UniversalBinaryRelation.this.col1.iterator();
				this.it2 = EmptyIterator.getInstance();
				this.next = this.getNext();
			}
			
			public boolean hasNext() {
				return this.next != null;
			}

			public Pair<E1, E2> next() {
				if (this.next == null) {
					throw new NoSuchElementException();
				}
				Pair<E1,E2> result = this.next;
				this.next = this.getNext();
				return result;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
			private Pair<E1,E2> getNext() {
				E1 first;
				if (!this.it2.hasNext()) {
					if (!this.it1.hasNext()) {
						return null;
					} else {
						first = this.it1.next();
					}
					this.it2 = UniversalBinaryRelation.this.col2.iterator();					
				} else {
					first = this.next.getFirst();
				}
				return Pair.create(first,this.it2.next());
			}
			
		};
	}

	public boolean addPair(Pair<E1, E2> pair) {
		throw new UnsupportedOperationException();
	}

	public boolean addPair(E1 first, E2 second) {
		throw new UnsupportedOperationException();
	}

	public boolean removePair(Pair<E1, E2> pair) {
		throw new UnsupportedOperationException();
	}

	public boolean removePair(E1 first, E2 second) {
		throw new UnsupportedOperationException();
	}


	@SuppressWarnings("unchecked")
	public Set<E2> getImage(Object first) {
		if (this.col1.contains(first)) {
			return this.col2;
		} else {
			return Collections.EMPTY_SET;
		}
	}

	public boolean add(Pair<E1, E2> o) {
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public void clear() {
		this.col1 = Collections.EMPTY_SET;
		this.col2 = Collections.EMPTY_SET;
	}
}
