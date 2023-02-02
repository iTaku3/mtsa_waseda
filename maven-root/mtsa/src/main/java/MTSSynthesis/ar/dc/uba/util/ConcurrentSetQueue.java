package MTSSynthesis.ar.dc.uba.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by MariaBrassesco on 11/30/16.
 */
public class ConcurrentSetQueue<T> implements Queue<T> {
    private ConcurrentHashMap.KeySetView<T, Boolean> internalSet;

    public ConcurrentSetQueue(int initialSize, int workers) {
        internalSet = new ConcurrentHashMap<T, Boolean>(initialSize, (float) 0.75,workers).newKeySet();
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
    public Iterator iterator() {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public Object[] toArray() {
        return internalSet.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public boolean add(T o) {
        return internalSet.add(o);
    }

    @Override
    public boolean remove(Object o) {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public boolean addAll(Collection c) {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public void clear() {
        internalSet.clear();
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public boolean containsAll(Collection c) {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public boolean offer(Object o) {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public T remove() {
        throw new NoSuchMethodError("This method is not implemented");
    }

    @Override
    public T poll() {
            Iterator<T> next = internalSet.iterator();
            if (!next.hasNext()) {
                return null;
            }
            T t = next.next();
            internalSet.remove(t);
            return t;
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
