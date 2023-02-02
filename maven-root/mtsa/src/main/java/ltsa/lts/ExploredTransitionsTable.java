package ltsa.lts;

import java.util.Iterator;

class ExploredTransitionsEntry {
	ExploredTransitionsEntry next;
	ExploredTransitionsEntry prev;
	byte[] key;	// parent state, hash by this
	int action;
	byte[] destKey;	// dest state, hashed key
	
	public ExploredTransitionsEntry(byte[] key, int action, byte[] parent) {
		this.key= parent == null ? null : myclone(parent);
		this.action= action;
		this.destKey= myclone(key);
	}
	
	private byte[] myclone(byte[] x) {
		byte[] tmp = new byte[x.length];
		for (byte i = 0; i < x.length; i++)
			tmp[i] = x[i];
		return tmp;
	}
}

public class ExploredTransitionsTable implements Iterable {
	class ExploredTransitionsTableIterator implements Iterator {
		int curTable;
		ExploredTransitionsEntry curEntry;
		
		public ExploredTransitionsTableIterator(ExploredTransitionsTable table) {
		}
		
		public boolean hasNext() {
			ExploredTransitionsEntry iter= curEntry;
			int tableIter= curTable;
			if (iter != null) {
				if (iter.next != null)
					return true;
				else {
					iter= null;
					tableIter++;
				}
			}
			
			while (iter == null && tableIter < table.length) {
				if (table[tableIter] != null)
					return true;
				else
					tableIter++;
			}

			return false;
		}

		public Object next() {
			ExploredTransitionsEntry iter= curEntry;
			int tableIter= curTable;
			if (iter != null) {
				if (iter.next != null) {
					curEntry= iter.next;
					return curEntry;
				} else {
					iter= null;
					tableIter++;
				}
			}

			while (iter == null && tableIter < table.length) {
				if (table[tableIter] != null) {
					curEntry= table[tableIter];
					curTable= tableIter;
					return curEntry;
				}
				else
					tableIter++;
			}
			
			return null; // ?
		}

		public void remove() {
			if (curEntry.prev != null) {
				curEntry.prev.next= curEntry.next;
			} else {
				table[curTable]= curEntry.next;
			}
			
			if (curEntry.next != null) {
				curEntry.next.prev= curEntry.prev;
			}

			count--;
		}
	}
	
	// to save different paths to a given state
	// (save here when a found hash is already in the Queue/Stack table)
	private ExploredTransitionsEntry[] table;
	private int count= 0;
	
	// must be same size as HashQueue / HashStack
	public ExploredTransitionsTable(int size) {
		table= new ExploredTransitionsEntry[size];
	}
	
	public int size() {
		return count;
	}
	
	private void addPut(ExploredTransitionsEntry entry) {
        //insert in hash table
        int hash = StateCodec.hash(entry.key) % table.length;
        entry.next=table[hash];
        if (table[hash] != null)
        	table[hash].prev= entry;
        table[hash]=entry;
        count++;
	}
	
    public void addPut(byte[] key, int action, MyHashQueueEntry parent) {
    	ExploredTransitionsEntry entry = new ExploredTransitionsEntry(key, action, parent.key);
        addPut(entry);
	}

	public Iterator iterator() {
		return new ExploredTransitionsTableIterator(this);
	}
}
