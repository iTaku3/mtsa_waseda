package MTSTools.ac.ic.doc.commons.relations;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


public class UniversalNAryRelation<E> extends AbstractSet<List<E>> implements NAryRelation<E> {

	private List<Set<E>> singleSets;
	private int			 size;
	
	public UniversalNAryRelation(List<? extends Set<? extends E>> singleSets) {
		this.size = 1;
		this.singleSets = new ArrayList<Set<E>>(singleSets.size());
		for (Set<? extends E> set : singleSets) {
			this.singleSets.add(new HashSet<E>(set));
			this.size*=set.size();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		if (!(o instanceof List)) {
			return false;
		}
		List tuple = (List) o;
		if (tuple.size() != this.singleSets.size()) {
			return false;
		}
		Iterator<Set<E>> setIt = singleSets.iterator();
		Iterator tupleIt	   = tuple.iterator();
		while(setIt.hasNext()) {
			if (!setIt.next().contains(tupleIt.next())) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<List<E>> iterator() {
		return new Iterator<List<E>>() {

			private List<Iterator<E>> its;
			private List<E> next;
			
			{
				this.its  = new ArrayList<Iterator<E>>( UniversalNAryRelation.this.singleSets.size() );
				this.next = new ArrayList<E>(	UniversalNAryRelation.this.singleSets.size() );
				for (Set<E> set : UniversalNAryRelation.this.singleSets) {
					Iterator<E> it = set.iterator();
					if ( !it.hasNext() ) {
						this.next = null;
						break;
					}
					this.next.add(it.next());
					this.its.add(it);
				}
			}
			
			@Override
			public boolean hasNext() {
				return this.next != null;
			}

			@Override
			public List<E> next() {
				if (this.next == null) {
					throw new NoSuchElementException();
				}
				List<E> result = new ArrayList<E>(this.next);
				updateNext();
				return result;
			}

			private void updateNext() {
				int i;
				for ( i = 0; i < this.its.size(); i++) {
					if ( !this.its.get(i).hasNext()) {
						Iterator<E> it = UniversalNAryRelation.this.singleSets.get(i).iterator();
						this.its.set(i, it);
						this.next.set(i, it.next());
					} else {
						break;
					}
				}
				
				if ( i < this.its.size() ) {
					this.next.set(i, this.its.get(i).next());
				} else {
					this.next = null;
				}					
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(List<E> e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends List<E>> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getDimension() {
		return this.singleSets.size();
	}
}
