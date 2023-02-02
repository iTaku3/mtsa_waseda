package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.ImplementationNotion;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.RefinementByRelation;

public class BaseSemanticsByRelation implements ImplementationNotion, RefinementByRelation {

	private RelationConstructor relationConstructor;
	private Set<?> silentActions;

	public BaseSemanticsByRelation(RelationConstructor relationConstructor, Set<?> silentActions) {
		this.setRelationConstructor(relationConstructor);
		this.setSilentActions(silentActions);
	}
	
	protected Set<?> getSilentActions() {
		return silentActions;
	}

	protected void setSilentActions(Set<?> silentActions) {
		this.silentActions = silentActions;
	}

	public <S1, S2, A> boolean isAnImplementation(MTS<S1, A> mts, LTS<S2, A> lts) {
		return this.isARefinement(mts, new MTSAdapter<S2, A>(lts));
	}

	public <S1, S2, A> boolean isARefinement(MTS<S1, A> mts1, MTS<S2, A> mts2) {
		return (getRefinement(mts1, mts2, mts1.getInitialState(), mts2.getInitialState()) != null);
	}

	public <S1, S2, A> BinaryRelation<S1, S2> getRefinement(MTS<S1, A> mts1, MTS<S2, A> mts2) {
		return this.getRefinement(mts1, mts2, mts1.getInitialState(), mts2.getInitialState());
	}


	protected void setRelationConstructor(RelationConstructor relationConstructor) {
		this.relationConstructor = relationConstructor;
	}

	protected RelationConstructor getRelationConstructor() {
		return relationConstructor;
	}

	public <S1, S2, A> boolean isARefinement(MTS<S1, A> mts1, MTS<S2, A> mts2, S1 state1, S2 state2) {
		return (getRefinement(mts1,mts2, state1, state2) != null);
	}


	public <S1, S2, A> BinaryRelation<S1, S2>  getRefinement(MTS<S1, A> mts1, MTS<S2, A> mts2, S1 state1, S2 state2) {
		Validate.isTrue(areComparable(mts1, mts2), "The models do not have the same alphabet");
		BinaryRelation<S1, S2> largestRelation = this.getRelationConstructor()
				.getLargestRelation(mts1, mts2);
		if (largestRelation.contains(Pair.create(state1, state2)))
			return largestRelation;
		else
			return null;
	}

	public <S1, S2, A> BinaryRelation<S1, S2> getLargestRelation(MTS<S1, A> m, MTS<S2, A> n) {
		return this.getRelationConstructor().getLargestRelation(m, n);
	}

	public <S1, S2, A> boolean isAValidRelation(MTS<S1, A> m, MTS<S2, A> n, BinaryRelation<S1, S2> relation) {
		return this.getRelationConstructor().isValidRelation(m, n, relation);		
	}

	protected <S1, S2, A> boolean areComparable(MTS<S1, A> m, MTS<S2, A> n) {
		return 	(n.getActions().containsAll(CollectionUtils.subtract(m.getActions(), this.getSilentActions()))) 
				&&
				(m.getActions().containsAll(CollectionUtils.subtract(n.getActions(), this.getSilentActions())));
	}
}
