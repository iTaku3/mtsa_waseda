package MTSTools.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

interface RelationConstructor {

	public <S1, S2, A> BinaryRelation<S1, S2> getLargestRelation(MTS<S1, A> mts1, MTS<S2, A> mts2);
	public <S1, S2, A> boolean isValidRelation(MTS<S1, A> mts1, MTS<S2, A> mts2, BinaryRelation<S1, S2> relation);
	
}
