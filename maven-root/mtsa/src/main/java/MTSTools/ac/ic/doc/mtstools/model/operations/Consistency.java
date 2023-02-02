package MTSTools.ac.ic.doc.mtstools.model.operations;

import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.NAryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

public interface Consistency {
	public <S1, S2, A> boolean areConsistent(MTS<S1,A> mtsA, MTS<S2,A> mtsB);
	public <S1, S2, A> Set<Pair<S1, S2>> getConsistencyRelation(MTS<S1,A> mtsA, MTS<S2,A> mtsB);
	
	public <S, A> boolean 		  areConsisten(List<? extends MTS<S,A>> mtss);
	public <S, A> NAryRelation<S> getConsistencyRelation(List< ? extends MTS<S,A> > mtss);
}
