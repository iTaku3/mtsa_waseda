package MTSTools.ac.ic.doc.commons.relations;

import java.util.HashSet;
import java.util.Set;

public class BinaryRelationUtils {

	public static final <E1,E2> BinaryRelation<E2, E1> getInverseRelation(BinaryRelation<E1, E2> relation) {
		BinaryRelation<E2, E1> result = new MapSetBinaryRelation<E2, E1>();
		for (Pair<E1, E2> pair : relation) {
			result.add(Pair.create(pair.getSecond(), pair.getFirst()));
		}
		return result;
	}

	public static final <E1,E2> Set<E1> getDomain(BinaryRelation<E1, E2> relation) {
		Set<E1> result = new HashSet<E1>();
		for (Pair<E1, E2> pair : relation) {
			result.add(pair.getFirst());
		}
		return result;
	}
}