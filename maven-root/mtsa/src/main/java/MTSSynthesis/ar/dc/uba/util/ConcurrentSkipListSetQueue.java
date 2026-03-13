package MTSSynthesis.ar.dc.uba.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by MariaBrassesco on 11/29/16.
 */
public class ConcurrentSkipListSetQueue<T> implements Queue<T> {
    private ConcurrentSkipListSet<T> internalSet;

    public ConcurrentSkipListSetQueue() {
        internalSet = new ConcurrentSkipListSet<T>();
    }

    @Override
    public boolean add(T t) {
        return internalSet.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return internalSet.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return internalSet.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return internalSet.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return internalSet.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return internalSet.retainAll(c);
    }

    @Override
    public void clear() {
        internalSet.clear();
    }

    @Override
    public boolean offer(T t) {
        return internalSet.add(t);
    }

    @Override
    public T remove() {
        throw new NoSuchMethodError("This method is not implemented");
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
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public Iterator<T> iterator() {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public Object[] toArray() {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public T poll() {
        return internalSet.pollLast();
        //returns null if set is empty
    }

    @Override
    public T element() {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public T peek() {
        throw new NoSuchMethodError("This method is not implemented");
    }
}
