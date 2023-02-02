package MTSTools.ac.ic.doc.commons.collections;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Hierarchical Map.
 * 
 * @author dfischbein
 */
public class HierarchicalMap<K,V> extends AbstractMap<K,V> implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3079809237399731340L;
	private Map<K,V> localMap;
    private Map<K, V> parent;

    /**
     * Creates a HierarchicalMap with an empty localMap.
     */
    public HierarchicalMap() {
        this.setLocalMap(new HashMap<K,V>());
    }

    /**
     * Removes all mappings from the local map.
     * 
     * @see java.util.Map#clear()
     */
	@Override
    public void clear() {
        this.getLocalMap().clear();
    }

    /**
     * Establishes if a key is contained either in the local map or the parent map.
     * 
     * @see Map#containsKey(java.lang.Object)
     */
	@Override
    public boolean containsKey(Object key) {
        return this.getLocalMap().containsKey(key) || this.getParentOrEmptyMap().containsKey(key);
    }

    /**
     * Establishes if a value is contained in the local map, or if it not, checks if the value is in the parent map
     * related with an inherited key.
     * 
     * @see Map#containsValue(java.lang.Object)
     */
	@Override
    public boolean containsValue(Object value) {
        if (this.getLocalMap().containsValue(value)) {
            return true;
        } else {
            if (this.getParentOrEmptyMap().containsValue(value)) {
                Iterator<Entry<K,V>> parentEntryIt = this.getParentOrEmptyMap().entrySet().iterator();
                while (parentEntryIt.hasNext()) {
                    Entry<K,V> parentEntry = parentEntryIt.next();
                    if (((value != null && value.equals(parentEntry.getValue())) || (value == null && parentEntry
                        .getValue() == null))
                        && this.isKeyInherited(parentEntry.getKey())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * Returns a <b>unmodifiable </b> set view of the mappings contained in this map. Each element in this set is a
     * Map.Entry.
     * 
     * @see java.util.Map#entrySet()
     */
	@Override
    public Set<Entry<K,V>> entrySet() {
        Set<Entry<K,V>> entrySet = new HashSet<Entry<K,V>>(this.getLocalMap().entrySet());

        Iterator<Entry<K,V>> iter = this.getParentOrEmptyMap().entrySet().iterator();
        while (iter.hasNext()) {
            Entry<K,V> parentEntry = iter.next();
            if (!this.getLocalMap().containsKey(parentEntry.getKey())) {
                entrySet.add(parentEntry);
            }
        }
        return Collections.unmodifiableSet(entrySet);
    }

    /**
     * Establishes if a Hierarchical Map is equals to the received object based on the comparisson of both the Local Map
     * and the Parent Map. Both Local and Parents Maps must be equal for two Hierarchical Maps to be equals, for
     * example, two Hierarchical Maps can have equal entrySets yet some particular values of one of them can be
     * inherited while not being the case in the other.
     */
	@Override
    public boolean equals(Object o) {
        if (o instanceof HierarchicalMap) {
            HierarchicalMap o2 = (HierarchicalMap)o;
            return this.getLocalMap().equals(o2.getLocalMap())
                && this.getParentOrEmptyMap().equals(o2.getParentOrEmptyMap());
        }
        return false;
    }

    /**
     * Retrieves a map entry for a given key from the local map first and if not found from the parent map.
     * 
     * @see Map#get(java.lang.Object)
     */
	@Override
    public V get(Object key) {
        if (this.getLocalMap().containsKey(key)) {
            return this.getLocalMap().get(key);
        }
        return this.getParentOrEmptyMap().get(key);
    }

    /**
     * Retrieves the localMap
     * 
     * @return Map with the localMap
     */
    public Map<K,V> getLocalMap() {
        return this.localMap;
    }

    /**
     * Retrieves the parent
     * 
     * @return Map with the parent
     */
    public Map<K, V> getParent() {
        return this.parent;
    }

    /**
     * Establishes if a key exists in the parent map.
     */
    public boolean isKeyInheritanceAllowed(K key) {
        return this.getParentOrEmptyMap().containsKey(key);
    }

    /**
     * Establishes if a key exists in the parent map but not in the local map.
     */
    public boolean isKeyInherited(K key) {
        return this.getParentOrEmptyMap().containsKey(key) && !this.getLocalMap().containsKey(key);
    }

    /**
     * Establishes if a local key is overriding a parent key.
     */
    public boolean isKeyOverridden(K key) {
        return this.getParentOrEmptyMap().containsKey(key) && this.getLocalMap().containsKey(key);
    }

    /**
     * Associates the specified value with the specified key in the local map . If the hierarchy previously contained a
     * mapping for this key, the old value is replaced.
     * 
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
	@Override
    public V put(K key, V value) {
        V oldValue = this.get(key);
        this.getLocalMap().put(key, value);
        return oldValue;
    }

    /**
     * Copies all of the mappings from the specified map to the local map. These mappings will replace any mappings that
     * this map had for any of the keys currently in the local map.
     * 
     * @see java.util.Map#putAll(java.util.Map)
     */
	@Override
	public void putAll(Map<? extends K, ? extends V>  t) {
        this.getLocalMap().putAll(t);
    }

    /**
     * Removes the mapping for this key from the local map if it is present.
     * 
     * @see java.util.Map#remove(java.lang.Object)
     */
	@Override
    public V remove(Object key) {
        V oldValue = this.get(key);
        this.getLocalMap().remove(key);
        return oldValue;
    }

    /**
     * Sets the localMap
     * 
     * @param localMap
     *            Map with the localMap
     */
    public void setLocalMap(Map<K,V> localMap) {
        this.localMap = localMap;
    }

    /**
     * Sets the parent
     * 
     * @param parent
     *            Map with the parent
     */
    public void setParent(Map<K, V> parent) {
        this.parent = parent;
    }

    /**
     * Returns an <b>unmodifiable </b> collection view of the values contained in this map.
     * 
     * @see java.util.Map#values()
     */
	@Override
    public Collection<V> values() {
        return super.values();
    }

    /**
     * Retrieves the parent or an empty map if the parent is null
     * 
     * @return the parent or an empty map if the parent is null
     */
    protected Map<K, V> getParentOrEmptyMap() {
        if (this.getParent() != null) {
            return this.getParent();
        }
        return new HashMap<K,V>();
    }

}