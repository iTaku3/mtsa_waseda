package MTSTools.ac.ic.doc.mtstools.model.operations;

import java.util.BitSet;
import java.util.Iterator;

public class MultipleState implements Iterable<Integer> {
	private static final int ERROR = -1;
	private BitSet internalState;
	private BitSet internalOutgoingMaybes;
	private boolean isDeadlock;
		
	public boolean isDeadlock() {
		return isDeadlock;
	}
	public void setDeadlock() {
		this.isDeadlock = true;
	}
	public MultipleState() {
		internalState = new BitSet();
		internalOutgoingMaybes = new BitSet();
	}
	public MultipleState(int initialState) {
		this();
		this.addState(initialState);
	}
	public void addState(Integer stateId) {
		if (stateId==ERROR) {
			this.isDeadlock = true;
		} else {
			internalState.set(stateId);
		}
	}
	
	public void markAsMaybe(Integer stateId) {
		if (stateId==ERROR) {
			this.isDeadlock = true;
		} else {
			internalOutgoingMaybes.set(stateId);
		}
	}
	
	public boolean hasMaybeMark(Integer stateId) {
		if (stateId==ERROR) {
			return this.isDeadlock;
		} else {
			return internalOutgoingMaybes.get(stateId);
		}
	}
	
	public boolean hasState(Integer state) {
		if (state==ERROR) {
			return this.isDeadlock;
		} else {
			return internalState.get(state);
		}
	}
	
	public int width(){
		return internalState.size() + (this.isDeadlock?1:0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this==obj) {
			return true;
		}
		try {
			MultipleState ms = (MultipleState) obj;
			return ms.internalState.equals(this.internalState);
//					&& ms.getInternalOutgoingMaybes().equals(this.getInternalOutgoingMaybes()); 
		} catch (RuntimeException e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return internalState.hashCode();
	}
	
	//saque el state por el problema del long
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>(){
			private int currentIndex = 0;
			
			public boolean hasNext() {
				currentIndex = internalState.nextSetBit(currentIndex);
				return ERROR!=currentIndex;
			}

			public Integer next() {
				return Integer.valueOf(currentIndex++);
			}

			public void remove() {
				throw new UnsupportedOperationException("Can't remove items");
			}
		};
	}
}
