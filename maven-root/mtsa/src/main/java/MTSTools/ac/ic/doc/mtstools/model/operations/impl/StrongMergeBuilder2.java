package MTSTools.ac.ic.doc.mtstools.model.operations.impl;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.POSSIBLE;
import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.REQUIRED;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.collections.PowerSet;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.IdentityRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.RefinementByRelation;
import MTSTools.ac.ic.doc.mtstools.model.impl.StrongPlusCROperator;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSemantics;
import MTSTools.ac.ic.doc.mtstools.model.operations.MergeBuilder;
import MTSTools.ac.ic.doc.mtstools.utils.MTSUtils;

/**
 * This class implements a merge algorithm based on make a common refinement more abstract applying a
 * sequence of abstraction operations, but this implementations keeps the original states structure 
 * (this means that it doesn't clone states).
 * @author fdario
 *
 */
public class StrongMergeBuilder2 implements MergeBuilder {

	private StrongPlusCROperator crOperator;
	private RefinementByRelation refinement;
	
	public StrongMergeBuilder2() {
		this.setCrOperator(new StrongPlusCROperator());
		this.setRefinement(new WeakSemantics(Collections.EMPTY_SET));
	}
	
	private StrongPlusCROperator getCrOperator() {
		return crOperator;
	}



	private void setCrOperator(StrongPlusCROperator crOperator) {
		this.crOperator = crOperator;
	}

	


	private RefinementByRelation getRefinement() {
		return refinement;
	}



	private void setRefinement(RefinementByRelation refinement) {
		this.refinement = refinement;
	}



	public <S1, S2, A> MTS<?, A> merge(MTS<S1, A> mtsA, MTS<S2, A> mtsB) {
		
		MTS<Pair<S1, S2>,A> cr = this.getCrOperator().compose(mtsA, mtsB);
		cr.removeUnreachableStates();
		
		
		BinaryRelation<S1, Pair<S1, S2>> refRelationACR = this.getRefinement().getLargestRelation(mtsA, cr);
		BinaryRelation<S2, Pair<S1, S2>> refRelationBCR = this.getRefinement().getLargestRelation(mtsB, cr);
		BinaryRelation<Pair<S1, S2>, Pair<S1, S2>> refRelationCRCR = new IdentityRelation<Pair<S1,S2>>(cr.getStates());
				
		assert(this.getRefinement().isAValidRelation(mtsA, cr, refRelationACR));
		assert(this.getRefinement().isAValidRelation(mtsB, cr, refRelationBCR));
		
		MTS<Pair<S1, S2>,A> bestCR = MTSUtils.cloneMTS(cr);
		
		boolean changed;
		do {
			changed = false;
			for (Pair<S1, S2> state : cr.getStates()) {
			for (A action : cr.getActions()) {
				Set<Pair<S1,S2>> reqReachableStates =  cr.getTransitions(state, REQUIRED).getImage(action);
				Set<Pair<S1,S2>> posReachableStates =  cr.getTransitions(state, POSSIBLE).getImage(action);
				if (reqReachableStates.isEmpty()) continue;
				
				for (Pair<S1,S2> toState : new HashSet<Pair<S1, S2>>(reqReachableStates)) {
					cr.removeRequired(state, action, toState);
					cr.addPossible(state, action, toState);
				}
				
				for (Set<Pair<S1,S2>> coverSet : new PowerSet<Pair<S1, S2>>(posReachableStates)){										
					for (Pair<S1,S2> toState : coverSet) {
						cr.addRequired(state, action, toState);
					}
					
					if (	this.getRefinement().isAValidRelation(mtsA, cr, refRelationACR) &&
							this.getRefinement().isAValidRelation(mtsB, cr, refRelationBCR) &&
							this.getRefinement().isAValidRelation(cr, bestCR, refRelationCRCR) && 
							!this.getRefinement().isAValidRelation(bestCR, cr, refRelationCRCR) ) {
						bestCR = MTSUtils.cloneMTS(cr);
						changed = true;
					}
					
					for (Pair<S1,S2> toState : coverSet) {
						cr.removeRequired(state, action, toState);
						cr.addPossible(state, action, toState);
					}
				}
				
				cr = MTSUtils.cloneMTS(bestCR);				
			}				
			}
	
		}while(changed);
							
		assert(this.getRefinement().isARefinement(mtsA, bestCR));
		assert(this.getRefinement().isARefinement(mtsB, bestCR));
		return bestCR;
	}	
	
}
