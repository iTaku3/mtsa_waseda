package MTSTools.ac.ic.doc.commons.relations;

import java.util.List;
import java.util.Set;

public interface NAryRelation<E> extends Set<List<E>> {

	int getDimension();
	
}
