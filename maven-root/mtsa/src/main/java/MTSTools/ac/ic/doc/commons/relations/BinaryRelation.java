package MTSTools.ac.ic.doc.commons.relations;

import java.util.Set;

public interface BinaryRelation<E1, E2> extends Set<Pair<E1,E2>>{

	public abstract boolean addPair(Pair<E1, E2> pair);

	public abstract boolean addPair(E1 first, E2 second);
	
	public abstract boolean removePair(Pair<E1, E2> pair);
	
	public abstract boolean removePair(E1 first, E2 second);

	public abstract Set<E2> getImage(E1 first);
	
}

