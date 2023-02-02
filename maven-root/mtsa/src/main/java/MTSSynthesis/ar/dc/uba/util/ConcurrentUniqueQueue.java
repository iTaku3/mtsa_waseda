package MTSSynthesis.ar.dc.uba.util;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by Ezequiel Castellano on 11/8/16.
 */
public class ConcurrentUniqueQueue<T> implements Queue<T> {

    private Set<T> internalSet;
    private ConcurrentLinkedQueue<T> internalQueue;

    /**
     * Main lock guarding all access
     */
    final ReentrantLock addLock;

    public ConcurrentUniqueQueue() {
        internalSet = new HashSet<T>();
        internalQueue = new ConcurrentLinkedQueue<T>();
        addLock = new ReentrantLock(true);
    }

    @Override
    public int size() {
        return internalSet.size();
    }

    @Override
    public boolean isEmpty() {
        return internalSet.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return internalSet.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public Object[] toArray() {
        return internalQueue.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return (T1[]) internalQueue.toArray(a);
    }

    @Override
    public boolean add(T t) {
        final ReentrantLock add = this.addLock;
        add.lock();
        try {
            //try to add in the set, if it returns true it adds in the queue. Otherwise return false.
            return internalSet.add(t) && internalQueue.add(t);
        } finally {
            add.unlock();
        }
    }


    @Override
    public boolean remove(Object o) {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return internalSet.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public void clear() {
        final ReentrantLock lock = this.addLock;
        lock.lock();
        try {
            internalQueue.clear();
            internalSet.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(T t) {
        return this.add(t);
    }

    @Override
    public T remove() {
        T next = this.poll();
        if (next == null)
            throw new NoSuchElementException();
        return next;
    }


    @Override
    public T poll() {
        final ReentrantLock lock = this.addLock;
        lock.lock();
        try {
            T o = internalQueue.poll();
            if (o != null)
                internalSet.remove(o);
            return o;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T element() {
        T next = this.peek();
        if (next == null)
            throw new NoSuchElementException();
        return next;
    }

    @Override
    public T peek() {
        return internalQueue.peek();
    }
}