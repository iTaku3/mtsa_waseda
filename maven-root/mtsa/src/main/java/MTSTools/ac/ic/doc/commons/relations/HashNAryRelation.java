package MTSTools.ac.ic.doc.commons.relations;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.Validate;

public class HashNAryRelation<E> extends AbstractSet<List<E>> implements NAryRelation<E> {

	private int dimension;
	private ConcurrentMap<List<E>, Boolean> map;
	
	public HashNAryRelation(int dimension) {
		this.dimension = dimension;
		this.map = new ConcurrentHashMap<List<E>, Boolean>();
	}
	
	@Override
	public boolean add(List<E> e) {
		Validate.isTrue(e.size() == this.dimension);
		return !Boolean.TRUE.equals(map.put(e, Boolean.TRUE));
	}

	@Override
	public Iterator<List<E>> iterator() {
		return this.map.keySet().iterator();
	}

	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public boolean remove(Object o) {
		return Boolean.TRUE.equals(this.map.remove(o));
	}

	@Override
	public int getDimension() {
		return this.dimension;
	}

}
