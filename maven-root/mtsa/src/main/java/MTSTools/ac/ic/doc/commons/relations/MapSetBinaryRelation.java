package MTSTools.ac.ic.doc.commons.relations;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.collections15.iterators.EmptyIterator;

public class MapSetBinaryRelation<E1, E2> extends AbstractSet<Pair<E1,E2>> implements BinaryRelation<E1, E2> {

	private int size;
	private Class<? extends Set<E2>> setClass;
	
	private ConcurrentMap<E1, Set<E2>> internalRelation;

	@SuppressWarnings("unchecked")
	public MapSetBinaryRelation() {
//		this((Class<? extends ConcurrentMap<E1,Set<E2>>>)ConcurrentHashMap.class, (Class<? extends Set<E2>>)HashSet.class);
//	}
	
//	private MapSetBinaryRelation(Class<? extends ConcurrentMap<E1,Set<E2>>> mapClass, Class<? extends Set<E2>> setClass) {		
        Object tmp1 = ConcurrentHashMap.class;
        Class<? extends ConcurrentMap<E1,Set<E2>>> mapClass = (Class<? extends ConcurrentMap<E1,Set<E2>>>)tmp1;
        Object tmp2 = HashSet.class;
        Class<? extends Set<E2>> setClass = (Class<? extends Set<E2>>) tmp2;
        
		try {
			this.size = 0;
			this.setClass = setClass;
			this.setClass.newInstance();
			this.internalRelation = mapClass.newInstance();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.ic.doc.common.relations.BinaryRelation#addPair(ac.ic.doc.common.relations.BinaryRelationImpl.Pair)
	 */
	public boolean addPair(Pair<E1, E2> pair) {
		return this.addPair(pair.getFirst(), pair.getSecond());
	}

	public boolean addPair(E1 first, E2 second) {
		return this.getInternalImage(first).add(second);
	}

	private Set<E2> getInternalImage(E1 first) {
		Set<E2> result = this.internalRelation.get(first);
		if (result == null) {
			this.internalRelation.putIfAbsent(first, this.createSet());
			result = this.internalRelation.get(first);
		}
		return result;
	}

	protected Set<E2> createSet() {
		try {
			final Set<E2> set = this.setClass.newInstance();
			return new AbstractSet<E2>() {
				Set<E2> wrappedSet = set;
				
				@Override
				public void clear() {
					this.wrappedSet.clear();
				}

				@Override
				public boolean isEmpty() {
					return this.wrappedSet.isEmpty();
				}
				
				@Override
				public Iterator<E2> iterator() {
					return new Iterator<E2>() {
						Iterator<E2> it = wrappedSet.iterator();	
						public boolean hasNext() {
							return it.hasNext();
						}

						public E2 next() {
							return it.next();
						}

						public void remove() {
							it.remove();
							MapSetBinaryRelation.this.size--;
						}						
					};
				}

				@Override
				public int size() {
					return this.wrappedSet.size();
				}

				@Override
				public boolean contains(Object o) {
					return this.wrappedSet.contains(o);
				}

				@Override
				public boolean remove(Object o) {
					if (this.wrappedSet.remove(o)) {
						MapSetBinaryRelation.this.size--;
						return true;
					} else {
						return false;
					}
				}

				@Override
				public boolean add(E2 o) {
					boolean added = this.wrappedSet.add(o);
					if (added) {
						MapSetBinaryRelation.this.size++;
					}
					return added;
				}
				
			};
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public boolean removePair(Pair<E1, E2> pair) {
		return this.removePair(pair.getFirst(), pair.getSecond());
	}

	public boolean removePair(E1 first, E2 second) {
		return this.getInternalImage(first).remove(second);
	}

	public Set<E2> getImage(E1 first) {
		return this.getInternalImage(first);
	}

	public int size() {
		return this.size;
	}

	@SuppressWarnings("unchecked")
	public boolean contains(Object o) {
		if (o instanceof Pair) {
			Pair<E1,E2> pair = (Pair<E1,E2>) o;
			Set<E2> image = this.internalRelation.get((E1) pair.getFirst());
			return image!=null && image.contains(pair.getSecond());
		}
		return false;
	}

	public boolean add(Pair<E1, E2> o) {
		return this.addPair(o.getFirst(), o.getSecond());
	}

	@SuppressWarnings("unchecked")
	public boolean remove(Object o) {
		if (o instanceof Pair) {
			return this.removePair((Pair<E1,E2>) o);
		}
		return false;
	}

	public void clear() {
		this.size = 0;
		this.internalRelation.clear();
	}

	public Iterator<Pair<E1, E2>> iterator() {
		return new Iterator<Pair<E1,E2>>() {
			private Iterator<Map.Entry<E1,Set<E2>>> firstIt;
			private Map.Entry<E1,Set<E2>> nextNotEmptyEntry;
			private E1 actualFirst;
			private Iterator<E2> actualSetIt;
			
			{
				this.firstIt = MapSetBinaryRelation.this.internalRelation.entrySet().iterator();
				this.updateNextNotEmptyEntry();
				this.actualFirst = null;
				this.actualSetIt = EmptyIterator.getInstance();
			}
			
			public boolean hasNext() {
				return this.actualSetIt.hasNext() || this.nextNotEmptyEntry!=null;
			}
			
			public Pair<E1, E2> next() {
				if (!this.actualSetIt.hasNext() && this.nextNotEmptyEntry != null) {
					this.actualSetIt = this.nextNotEmptyEntry.getValue().iterator();
					this.actualFirst = this.nextNotEmptyEntry.getKey();
					this.updateNextNotEmptyEntry();
				}
				return Pair.create(this.actualFirst,this.actualSetIt.next());
			}
			public void remove() {
				this.actualSetIt.remove();
			}
			
			private void updateNextNotEmptyEntry() {
				Map.Entry<E1,Set<E2>> entry = null;
				while( this.firstIt.hasNext() && (entry=this.firstIt.next()).getValue().isEmpty()); 
				if (entry!=null && !entry.getValue().isEmpty()) {
					this.nextNotEmptyEntry = entry;
				} else {
					this.nextNotEmptyEntry = null;
				}
			}			

		};
	}

}
