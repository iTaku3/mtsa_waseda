package MTSSynthesis.ar.dc.uba.util;


import MTSSynthesis.controller.gr.StrategyState;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by MariaBrassesco on 11/28/16.
 */
public class ConcurrentLinkedQueuePriorityBit<T> implements Queue<T> {

    private ConcurrentLinkedQueue<T> queue;
    private Boolean enable;
    private T state;

    public ConcurrentLinkedQueuePriorityBit() {
        queue = new ConcurrentLinkedQueue<>();
        enable = false;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return queue.iterator();
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return queue.toArray(a);
    }

    @Override
    public boolean add(T t) {
        if (0 == (Long) ((StrategyState) t).getState()) {
            synchronized (enable) {
                enable = true;
                state = t;
            }
            return enable;
        }
        return queue.add(t);
    }


    @Override
    public boolean remove(Object o) {
        return queue.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return queue.contains(c);
    }


    @Override
    public boolean addAll(Collection<? extends T> c) {
        return queue.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return queue.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return queue.retainAll(c);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean offer(T t) {
        return queue.offer(t);
    }


    @Override
    public T remove() {
        return queue.remove();
    }

    @Override
    public T poll() {
        if (enable) {
            synchronized (enable) {
                enable = false;
                return state;
            }
        } else {
            return queue.poll();
        }
    }

    @Override
    public T element() {
        return queue.element();
    }

    @Override
    public T peek() {
        return queue.peek();
    }
}
