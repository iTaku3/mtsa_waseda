package MTSTools.ac.ic.doc.commons.collections;

import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

public class QueueSet<E> extends AbstractQueue<E> implements Queue<E>, Set<E> {

	private Queue<E> queue;
	
	private Set<E> set;
	
	public QueueSet() {
		queue = new ArrayDeque<>();
		set = new HashSet<>();
	}
	
	@Override
	public boolean offer(E e) {
		return !set.add(e) || queue.offer(e);
	}

	@Override
	public E poll() {
		E result = queue.poll();
		set.remove(result);
		return result;
	}

	@Override
	public E peek() {
		return queue.peek();
	}
	
	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return queue.iterator();
	}

	@Override
	public int size() {
		return queue.size();
	}

}
