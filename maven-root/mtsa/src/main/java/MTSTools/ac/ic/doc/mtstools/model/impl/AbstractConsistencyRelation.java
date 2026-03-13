package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.NAryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.Consistency;

public abstract class AbstractConsistencyRelation implements Consistency {

	public AbstractConsistencyRelation() {
		super();
	}

	protected abstract <S1, S2, A> FixedPointRelationConstructor getRelationContructor(MTS<S1, A> mtsA, MTS<S2, A> mtsB);

	public <S1, S2, A> boolean areConsistent(MTS<S1, A> mtsA, MTS<S2, A> mtsB) {
		return getConsistencyRelation(mtsA, mtsB).contains(Pair.create(mtsA.getInitialState(), mtsB.getInitialState()));
	}

	public <S1, S2, A> Set<Pair<S1, S2>> getConsistencyRelation(MTS<S1, A> mtsA, MTS<S2, A> mtsB) {
		return getRelationContructor(mtsA, mtsB).getLargestRelation(mtsA, mtsB);
	}
	
	@Override
	public <S, A> boolean areConsisten(List<? extends MTS<S,A>> mtss) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S, A> NAryRelation<S> getConsistencyRelation(
			List<? extends MTS<S, A>> mtss) {
		throw new UnsupportedOperationException();
	}

}