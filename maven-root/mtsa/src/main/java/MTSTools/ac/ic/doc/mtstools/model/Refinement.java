package MTSTools.ac.ic.doc.mtstools.model;


import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;

public interface Refinement {
	public <S1, S2, A> boolean isARefinement(MTS<S1,A> m, MTS<S2,A> n);
	public <S1, S2, A> BinaryRelation<S1, S2> getRefinement(MTS<S1,A> m, MTS<S2,A> n);

}
 