package MTSTools.ac.ic.doc.commons.collections;

import java.util.AbstractSet;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class provides an implementation of Set, which given a base set produces the power set of that set. 
 * @author fdario
 *
 * @param <E>
 */
public class PowerSet<E> extends AbstractSet<Set<E>> implements Set<Set<E>> {

	private Set<E> baseSet;
	private E[] baseSetAsArray;
	
	 public PowerSet(Set<E> baseSet) {
		 this.setBaseSet(baseSet);
	 }
	

	
	private Set<E> getBaseSet() {
		return baseSet;
	}

	@SuppressWarnings("unchecked")
	private void setBaseSet(Set<E> baseSet) {
		this.baseSet = new HashSet<E>(baseSet);
		this.baseSetAsArray = (E[]) this.baseSet.toArray();		
	}

	@Override
	public Iterator<Set<E>> iterator() {
		return new Iterator<Set<E>>(){
			
			BitSet mask = new BitSet(PowerSet.this.getBaseSet().size()+1);
			
			
			public boolean hasNext() {
				return !this.mask.get(PowerSet.this.getBaseSet().size());
			}

			public Set<E> next() {
				Set<E> result = new HashSet<E>(this.mask.cardinality());
				for(int i = this.mask.nextSetBit(0); i>=0; i=this.mask.nextSetBit(i+1)) {
					result.add(PowerSet.this.baseSetAsArray[i]);
				}
				this.updateMask();
				return result;
			}

			private void updateMask() {
				int nextBitToSet = this.mask.nextClearBit(0);
				this.mask.clear(0, nextBitToSet);
				this.mask.set(nextBitToSet);				
			}

			public void remove() {
				throw new UnsupportedOperationException();				
			}
			
		};
	}

	@Override
	public int size() {
		return (int) Math.pow(2, this.getBaseSet().size());
	}

	@Override
	public boolean contains(Object o) {
		if (!(o instanceof Set)) {
			return false;
		} else {
			return this.getBaseSet().containsAll((Collection<?>) o);
		}
	}


}
