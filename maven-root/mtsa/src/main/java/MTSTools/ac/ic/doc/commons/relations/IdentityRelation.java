package MTSTools.ac.ic.doc.commons.relations;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class IdentityRelation<E> extends AbstractSet<Pair<E, E>> implements BinaryRelation<E,E> {

	private Set<E> basetSet;
	
	public IdentityRelation(Set<E> baseSet) {
		this.basetSet = baseSet;
	}



	@Override
	public Iterator<Pair<E, E>> iterator() {
		return new Iterator<Pair<E,E>>() {
			private Iterator<E> baseIterator;
			{
				baseIterator = basetSet.iterator();
			}
			public boolean hasNext() {
				return baseIterator.hasNext();
			}
			public Pair<E, E> next() {
				E elem = baseIterator.next();
				return Pair.create(elem, elem);
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		if (!(o instanceof Pair)) {
			return false;
		}
		Pair<E, E> pair = (Pair<E, E>) o;
		return pair.getFirst().equals(pair.getSecond()) && basetSet.contains(pair.getFirst());
	}



	@Override
	public int size() {
		return basetSet.size();
	}

	public boolean addPair(Pair<E, E> pair) {
		throw new UnsupportedOperationException();
	}

	public boolean addPair(E first, E second) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public Set<E> getImage(Object first) {
		if (this.basetSet.contains(first)) {
			return Collections.singleton((E)first);
		}
		return Collections.EMPTY_SET;
	}

	public boolean removePair(Pair<E, E> pair) {
		throw new UnsupportedOperationException();
	}

	public boolean removePair(E first, E second) {
		throw new UnsupportedOperationException();
	}


}
