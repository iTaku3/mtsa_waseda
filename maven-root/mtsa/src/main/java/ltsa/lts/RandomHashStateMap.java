package ltsa.lts;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;

public class RandomHashStateMap implements StateMap, StackCheck, Iterable {

	private class RandomHashEntry extends StateMapEntry implements Comparable {
		RandomHashEntry next;
		RandomHashEntry prev; // for linking within bucket
		int action;
		long priority;
		
		public RandomHashEntry(byte[] l) {
			stateNumber= -1;
			key = l;
			action = -1;
			next = null;
			prev= null;
			marked = false;
			priority= 0;
		}
		
		public RandomHashEntry(byte[] l, int a) {
			this(l);
			action= a;
		}
		
		public RandomHashEntry(byte[] l, long priority) {
			this(l);
			this.priority= priority;
		}
		
		public RandomHashEntry(byte[] l, int a, long priority) {
			this(l, a);
			this.priority= priority;
		}

		// @Override
		public int compareTo(Object arg0) {
			if (arg0 instanceof RandomHashEntry) {
				RandomHashEntry other= (RandomHashEntry) arg0;
				if (depth < other.depth)
					return -1;
				else if (depth > other.depth)
					return 1;
				else {
					if (priority < other.priority)
						return -1;
					else if (priority > other.priority)
						return 1;
					else
						return compareKeys(key, other.key);
				}
			} else
				throw new ClassCastException("Cannot compare RandomHashEntry objects with " + arg0.getClass().getName() + " objects.");
		}
		
		int compareKeys(byte[] k1, byte[] k2) {
			if (k1.length < k2.length)
				return -1;
			else if (k1.length > k2.length)
				return 1;
			else {
				for (int i= 0; i < k1.length; i++)
					if (k1[i] < k2[i])
						return -1;
					else if (k1[i] > k2[i])
						return 1;
			}
			return 0;
		}
	}
	
	class RandomHashStateMapIterator implements Iterator {
		int tableEntry;
		RandomHashEntry curEntry;
		RandomHashEntry nextCache;

		public RandomHashStateMapIterator(RandomHashStateMap rand) {
			tableEntry = 0;
			curEntry = null;
			nextCache= null;
		}

		public boolean hasNext() {
			RandomHashEntry iter = curEntry;
			int tableIter = tableEntry;
			if (iter != null) {
				if (iter.next != null) {
					nextCache= iter.next;
					return true;
				} else {
					iter = null;
					tableIter++;
				}
			}

			while (iter == null && tableIter < table.length) {
				if (table[tableIter] != null) {
					nextCache= table[tableIter];
					return true;
				}
				else
					tableIter++;
			}

			return false;
		}

		public Object next() {
			RandomHashEntry retEntry= null;
			if (nextCache != null) {
				retEntry= nextCache;
				nextCache= null;
				return retEntry;
			}

			RandomHashEntry iter = curEntry;
			int tableIter = tableEntry;
			if (iter != null) {
				if (iter.next != null) {
					curEntry = iter.next;
					return curEntry;
				} else {
					iter = null;
					tableIter++;
				}
			}

			while (iter == null && tableIter < table.length) {
				if (table[tableIter] != null) {
					curEntry = table[tableIter];
					tableEntry = tableIter;
					return curEntry;
				} else
					tableIter++;
			}

			return null;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private PriorityQueue queue;
	private RandomHashEntry[] table;
	private Random rand;
	private int count;
	private long seed;
	
	public RandomHashStateMap(int size) {
		this(size, System.currentTimeMillis());
	}
	
	public RandomHashStateMap(int size, long seed) {
		this.seed= seed;
		this.rand= new Random(seed);
		table= new RandomHashEntry[size];
		queue= new PriorityQueue();
	}
	
	public long getSeed() {
		return seed;
	}
	
	// @Override
	public void add(byte[] state) throws UnsupportedOperationException {
		// inserted via hash so we can lookup
		// next() is via random
        RandomHashEntry entry = new RandomHashEntry(state);
        entry.priority= rand.nextLong() % (count+1);
        int hash = StateCodec.hash(state) % table.length;
        entry.next=table[hash];
        if (table[hash] != null)
        	table[hash].prev= entry;
        table[hash]=entry;
        count++;

        queue.add(entry);
	}
	
	// @Override
	public void add(byte[] state, int depth) throws UnsupportedOperationException {
		// inserted via hash so we can lookup
		// next() is via random
        RandomHashEntry entry = new RandomHashEntry(state);
        entry.depth= depth;
        entry.priority= rand.nextLong() % (count+1);
        int hash = StateCodec.hash(state) % table.length;
        entry.next=table[hash];
        if (table[hash] != null)
        	table[hash].prev= entry;
        table[hash]=entry;
        count++;

        queue.add(entry);
	}

	// @Override
	public void add(byte[] state, int action, byte[] parent)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	// @Override
	public boolean contains(byte[] state) {
		int hash = StateCodec.hash(state) % table.length;
		RandomHashEntry entry = table[hash];
		while (entry!=null) {
			if (StateCodec.equals(entry.key, state)) return true;
				entry = entry.next;
			}
		return false;
	}

	// @Override
	public boolean empty() {
		return count == 0;
	}

	// @Override
	public int get(byte[] key) throws UnsupportedOperationException {
		int hash = StateCodec.hash(key) % table.length;
		RandomHashEntry entry = table[hash];
		while (entry!=null) {
			if (StateCodec.equals(entry.key,key)) return entry.stateNumber;
				entry = entry.next;
		}
		
		return LTSConstants.NO_SEQUENCE_FOUND;
	}

	// @Override
	public byte[] getNextState() {
		return ((RandomHashEntry) queue.peek()).key;
	}
	
	public int getNextStateDepth() {
		return ((RandomHashEntry) queue.peek()).depth;
	}

	// @Override
	public int getNextStateNumber() {
		return ((RandomHashEntry) queue.peek()).stateNumber;
	}

	// @Override
	public void markNextState(int stateNumber) {
		((RandomHashEntry) queue.peek()).stateNumber= stateNumber;
		((RandomHashEntry) queue.peek()).marked= true;
	}
	
	public void unmarkNextState() {
		((RandomHashEntry) queue.peek()).marked= false;
	}

	// @Override
	public boolean nextStateIsMarked() {
		return ((RandomHashEntry) queue.peek()).marked;
	}

	// @Override
	public void removeNextState() {
		queue.remove();
		count--;
	}
	
	public Object popNextState() {
		count--;
		return queue.remove();
	}

	// @Override
	public boolean onStack(byte[] key) {
		int hash = StateCodec.hash(key) % table.length;
		RandomHashEntry entry = table[hash];
		while (entry!=null) {
			if (StateCodec.equals(entry.key, key))
				return entry.marked;
			
			entry = entry.next;
		}
		return false;
	}

	// @Override
	public Iterator iterator() {
		return new RandomHashStateMapIterator(this);
	}

}
