package MTSTools.ac.ic.doc.commons.relations;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BinaryRelationImpl<E1, E2> extends AbstractSet<Pair<E1,E2>>  implements BinaryRelation<E1, E2> {

	private Map<E1, Set<E2>> map;
	private int size;
	
	public BinaryRelationImpl() {
		map = new HashMap<>();
		size = 0;
	}
	
	@Override
	public boolean addPair(Pair<E1, E2> pair) {
		return addPair(pair.getFirst(), pair.getSecond());
	}

	@Override
	public boolean addPair(E1 first, E2 second) {
		Set<E2> set = map.get(first);
		if (set == null) {
			set = new HashSet<>();
			map.put(first, set);
		}
		boolean result = set.add(second);
		if (result) size++;
		return result;
	}

	@Override
	public boolean removePair(Pair<E1, E2> pair) {
		removePair(pair.getFirst(), pair.getSecond());
		return false;
	}

	@Override
	public boolean removePair(E1 first, E2 second) {
		Set<E2> set = map.get(first);
		boolean result = set == null || set.remove(second);
		if (result) size--;
		return result;
	}

	@Override
	public Set<E2> getImage(E1 first) {
		return map.get(first);
	}

	@Override
	public int size() {
		return size;
	}
	
	@Override
	public void clear() {
		map.clear();
		size = 0;
	}
	
	@Override
	public Iterator<Pair<E1, E2>> iterator() {
		return new Relit();
	}

	private class Relit implements Iterator<Pair<E1, E2>> {
		
		private Iterator<Entry<E1, Set<E2>>> mapit;
		private E1 e1;
		private Set<E2> set;
		private Iterator<E2> setit = Collections.emptyIterator();
		private RelPair<E1,E2> pair = new RelPair<E1,E2>();
		
		public Relit() {
			mapit = map.entrySet().iterator();			
		}

		@Override
		public boolean hasNext() {
			boolean setNext = setit.hasNext();
			while (!setNext && mapit.hasNext()) {
				Entry<E1, Set<E2>> entry = mapit.next();
				e1 = entry.getKey();
				set = entry.getValue();
				setit = set.iterator();
				setNext = !set.isEmpty();
			}
			return setNext;
		}

		@Override
		public Pair<E1, E2> next() {
			if (!setit.hasNext()) {
				Entry<E1, Set<E2>> entry = mapit.next();
				e1 = entry.getKey();
				set = entry.getValue();
				setit = set.iterator();
			}
			pair.setFirst(e1);
			pair.setSecond(setit.next());
			return pair; // returns the pair with aliasing!
			//return new Pair<>(e1, setit.next());
		}

		@Override
		public void remove() {
			setit.remove();
			if (set.isEmpty())
				mapit.remove();
		}
		
	}
	
	public static class RelPair<E1,E2> extends Pair<E1, E2> {

		public RelPair(E1 first, E2 second) {
			super(first, second);
		}
		
		public RelPair() {
			this(null,null);
		}
		
		public void setFirst(E1 e1) {
			first = e1;
		}
		
		public void setSecond(E2 e2) {
			second = e2;
		}
		
		public void copy(Pair<E1,E2> pair) {
			setFirst(pair.getFirst());
			setSecond(pair.getSecond());
		}
		
	}

}

