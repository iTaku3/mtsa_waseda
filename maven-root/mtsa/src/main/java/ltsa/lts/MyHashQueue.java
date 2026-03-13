package ltsa.lts;

import java.lang.UnsupportedOperationException;
import java.util.Iterator;
import java.util.LinkedList;

/* MyHash is a speciallized Hashtable/Queue for the reachable analyser
 * it includes a queue structure through the hash table entries
 *  -- assumes no attempt to input duplicate key
 *  
 */

class MyHashQueueEntry extends StateMapEntry {
	int action;
	int level = 0;
	MyHashQueueEntry next; // for linking buckets in hash table
	MyHashQueueEntry link; // for queue linked list
	MyHashQueueEntry parent; // pointer to node above in BFS

	MyHashQueueEntry(byte[] l) {
		stateNumber= -1;
		key = l;
		// action= 0;
		action = -1;
		next = null;
		link = null;
		marked = false;
	}

	MyHashQueueEntry(byte[] l, int a, MyHashQueueEntry p) {
		stateNumber= -1;
		key = l;
		action = a;
		next = null;
		link = null;
		parent = p;
		marked = false;
	}

}

public class MyHashQueue implements StackCheck, Iterable, StateMap {

	class MyHashQueueIterator implements Iterator {
		int tableEntry;
		MyHashQueueEntry curEntry;

		public MyHashQueueIterator(MyHashQueue hh) {
			tableEntry = 0;
			curEntry = null;
		}

		public boolean hasNext() {
			MyHashQueueEntry iter = curEntry;
			int tableIter = tableEntry;
			if (iter != null) {
				if (iter.next != null)
					return true;
				else {
					iter = null;
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
			MyHashQueueEntry iter = curEntry;
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

			return null; // ?
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private MyHashQueueEntry[] table;
	private int count = 0;
	private MyHashQueueEntry head = null;
	private MyHashQueueEntry tail = null;

	public MyHashQueue(int size) {
		table = new MyHashQueueEntry[size];
	}

	// @Override
	public int get(byte[] key) {
		int hash = StateCodec.hash(key) % table.length;
		MyHashQueueEntry entry = table[hash];
		while (entry != null) {
			if (StateCodec.equals(entry.key, key))
				return entry.stateNumber;
			entry = entry.next;
		}
		return LTSConstants.NO_SEQUENCE_FOUND;
	}

	private MyHashQueueEntry getEntry(byte[] key)
			throws IllegalArgumentException {
		int hash = StateCodec.hash(key) % table.length;
		MyHashQueueEntry tableEntry = table[hash];
		while (tableEntry != null && StateCodec.equals(key, tableEntry.key)) {
			tableEntry = tableEntry.next;
		}

		if (tableEntry == null)
			throw new IllegalArgumentException(
					"key passed to getEntry does not exist");

		return tableEntry;
	}

	public void addPut(MyHashQueueEntry qe) {
		if (qe.parent != null)
			qe.level = qe.parent.level + 1;
		// insert in hash table
		int hash = StateCodec.hash(qe.key) % table.length;
		qe.next = table[hash];
		table[hash] = qe;
		++count;
		// insert in queue
		if (head == null)
			head = tail = qe;
		else {
			tail.link = qe;
			tail = qe;
		}
	}

	public void addPut(byte[] key, int action, MyHashQueueEntry parent) {
		MyHashQueueEntry entry = new MyHashQueueEntry(key, action, parent);
		addPut(entry);
	}

	// @Override
	public void add(byte[] key) {
		MyHashQueueEntry entry= new MyHashQueueEntry(key);
		addPut(entry);
	}
	
	// @Override
	public void add(byte[] key, int depth) {
		MyHashQueueEntry entry= new MyHashQueueEntry(key);
		addPut(entry);
		entry.depth= depth;
	}

	// @Override
	public void add(byte[] key, int action, byte[] parent) {
		MyHashQueueEntry parentEntry = null;
		if (parent != null) {
			parentEntry = getEntry(parent);
		}

		addPut(key, action, parentEntry);
	}

	// @Override
	public boolean nextStateIsMarked() {
		return head.marked;
	}

	// @Override
	public byte[] getNextState() {
		return peek().key;
	}

	public MyHashQueueEntry peek() {
		return head;
	}

	// @Override
	public int getNextStateNumber() {
		return head.stateNumber;
	}
	
	// @Override
	public void markNextState(int stateNumber) {
		head.marked = true;
		head.stateNumber= stateNumber;
	}

	// @Override
	public void removeNextState() {
		pop();
	}

	public void pop() {
		// remove from head of queue
		head.marked = true;
		head = head.link;
		if (head == null)
			tail = head;
	}

	public boolean empty() {
		return head == null;
	}

	// @Override
	public boolean contains(byte[] key) {
		return containsKey(key);
	}

	public boolean containsKey(byte[] key) {
		int hash = StateCodec.hash(key) % table.length;
		MyHashQueueEntry entry = table[hash];
		while (entry != null) {
			if (StateCodec.equals(entry.key, key))
				return true;
			entry = entry.next;
		}
		return false;
	}

	// for breadth first search we can only check that we have already visited
	// the state
	public boolean onStack(byte[] key) {
		int hash = StateCodec.hash(key) % table.length;
		MyHashQueueEntry entry = table[hash];
		while (entry != null) {
			if (StateCodec.equals(entry.key, key)) // return
													// entry.level<=head.level;
				return entry.marked;
			entry = entry.next;
		}
		return false;
	}

	public int size() {
		return count;
	}

	public LinkedList<String> getPath(MyHashQueueEntry end, String[] actionNames) {
		LinkedList<String> trace = new LinkedList<String>();
		while (end != null) {
			if (end.parent != null)
				trace.addFirst(actionNames[end.action]);
			end = end.parent;
		}
		return trace;
	}

	public int depth(MyHashQueueEntry e) {
		int d = 0;
		while (e != null) {
			++d;
			e = e.parent;
		}
		return d;
	}

	public Iterator<?> iterator() {
		return new MyHashQueueIterator(this);
	}

}
