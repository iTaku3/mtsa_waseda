package MTSTools.ac.ic.doc.commons.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


/** This class implements a Bidirectional Map, that is, keys map to sets of
 *  values and values map to sets of keys. */
public class BidirectionalMap<K, V> {

	/** Internal key to set of values map. */
	private Map<K, Set<V>> map;
	
	/** Internal value to set of keys (inverse) map. */
	private Map<V, Set<K>> imap;
	
	
	/** Consturctor for a BidirectionalMap. */
	public BidirectionalMap() {
		map = new HashMap<K, Set<V>>();
		imap = new HashMap<V, Set<K>>();
	}
	
	
	/** Returns the set of values associated with the given key. */
	public Set<V> getV(K key) {
		Set<V> result = map.get(key);
		if (result == null)
			result = Collections.emptySet();
		return Collections.unmodifiableSet(result);
	}
	
	
	/** Returns the set of keys associated with the given value. */
	public Set<K> getK(V value) {
		Set<K> result = imap.get(value);
		if (result == null)
			result = Collections.emptySet();
		return Collections.unmodifiableSet(result);
	}
	
	
	/** Adds the mapping of a key to a value. */
	public boolean put(K key, V value) {
		Set<V> values = map.get(key);
		if (values == null)
			map.put(key, values = new HashSet<>());
		boolean result = values.add(value);
		Set<K> keys = imap.get(value);
		if (keys == null)
			imap.put(value, keys = new HashSet<>());
		keys.add(key);
		return result;
	}

	
	/** Adds mappings between a key and a collection of values. */
	public void putAll(K key, Collection<V> values) {
		for (V value : values)
			put(key, value);
	}
	
	
	/** Adds all the mappings in another BidirectionalMap. */
	public void putAll(BidirectionalMap<K, V> other) {
		for (K key : other.keySet())
			putAll(key, other.getV(key));
	}
	
	
	/** Removes a given key (removing all its mappings). */
	public void removeK(K key) {
		Set<V> values = getV(key);
		while (!values.isEmpty()) {
			V value = values.iterator().next();
			remove(key, value);
		}
	}
	
	
	/** Removes a given value (removing all its inverse mappings). */
	public void removeV(V value) {
		Set<K> keys = getK(value);
		while (!keys.isEmpty()) {
			K key = keys.iterator().next();
			remove(key, value);
		}
	}
	
	
	/** Removes the association between the given key and value. */
	public void remove(K key, V value) {
		Set<V> values = map.get(key);
		if (values != null) {
			values.remove(value);
			if (values.isEmpty())
				map.remove(key);
		}
		Set<K> keys = imap.get(value);
		if (keys != null) {
			keys.remove(key);
			if (keys.isEmpty())
				imap.remove(value);
		}
	}
	
	
	/** Removes all the mappings for a collection of keys. */
	public void removeKAll(Collection<K> keys) {
		for (K key : keys)
			removeK(key);
	}
	
	
	/** Removes all the mappings between a key and a collection of values. */
	public void removeAll(K key, Collection<V> values) {
		if (values != null)
			for (V value : values)
				remove(key, value);
	}
	
	
	/** Removes all the mappings between a collection of keys and a value. */
	public void removeAll(Collection<K> keys, V value) {
		if (keys != null)
			for (K key : keys)
				remove(key, value);
	}
	
	
	/** Removes all the mappings between two collections of keys and values. */
	public void removeAll(Collection<K> keys, Collection<V> values) {
		if (keys != null) {
			for (K key : keys)
				removeAll(key, values);
		}
	}
	
	
	/** Returns the key set of this map. */
	public Set<K> keySet() {
		return map.keySet();
	}
	
	
	/** Returns the set of values of this map. */
	public Set<V> values() {
		return imap.keySet();
	}
	
	
	/** Clears this map removing all mappings. */
	public void clear() {
		map.clear();
		imap.clear();
	}
	
	
	/** Returns the size of this map, that is the number of keys with associated values. */
	public int size() {
		return map.size();
	}
	
	
	/** Returns whether this map is empty or not. */
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	
	/** Returns a String representation of this map. */
	@Override
	public String toString() {
        Iterator<Entry<K,Set<V>>> i = map.entrySet().iterator();
        if (!i.hasNext())
            return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            Entry<K,Set<V>> e = i.next();
            K key = e.getKey();
            Set<V> value = e.getValue();
            sb.append(key   == this ? "(this Map)" : key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (!i.hasNext())
                return sb.append(']').toString();
            sb.append(';').append(' ');
        }
	}

}
