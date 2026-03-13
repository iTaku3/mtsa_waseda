package MTSTools.ac.ic.doc.mtstools.model.operations.impl;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.MAYBE;
import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.REQUIRED;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.collections.PowerSet;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.RefinementByRelation;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.StrongPlusCROperator;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSemantics;
import MTSTools.ac.ic.doc.mtstools.model.operations.MergeBuilder;

public class StrongMergeBuilder implements MergeBuilder {

	private StrongPlusCROperator crOperator;
	private RefinementByRelation refinement;
	
	public StrongMergeBuilder() {
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



	public <S1, S2, A> MTS<Object, A> merge(MTS<S1, A> mtsA, MTS<S2, A> mtsB) {
		
		MTS<Object,A> cr = this.cloneMTS(this.getCrOperator().compose(mtsA, mtsB));
		cr.removeUnreachableStates();
		
		
		BinaryRelation<S1, Object> refRelationACR = this.getRefinement().getLargestRelation(mtsA, cr);
		BinaryRelation<S2, Object> refRelationBCR = this.getRefinement().getLargestRelation(mtsB, cr);
		BinaryRelation<Object, Object> refRelationCRCR = this.getRefinement().getLargestRelation(cr, cr); //new IdentityRelation<Object>(cr.getStates());
				
		assert(this.getRefinement().isAValidRelation(mtsA, cr, refRelationACR));
		assert(this.getRefinement().isAValidRelation(mtsB, cr, refRelationBCR));
		
		MTS<Object,A> bestCR = this.cloneMTS(cr);
		
		boolean changed;
		do {
			changed = false;
			for (A action : cr.getActions())
			for (Object state : cr.getStates()) {
				boolean doMerge = false;
				Collection<MTS<Object,A>> mcrs = new LinkedList<MTS<Object,A>>();
				mcrs.add(bestCR);
				
				Set<Object> reqReachableStates =  cr.getTransitions(state, REQUIRED).getImage(action);
								
				if (reqReachableStates.isEmpty()) continue;
				
				reqReachableStates = new HashSet<Object>(reqReachableStates); // clone the set in order to be able to modify cr without impacting reqReachableStates
				
				
				this.setAsPossibleTransitions(cr, state, action, reqReachableStates);
				
				for (Set<Object> coverSet : new PowerSet<Object>(reqReachableStates)){										
					this.setAsRequiredTransitions(cr, state, action, coverSet);
					
					if (	this.getRefinement().isAValidRelation(mtsA, cr, refRelationACR) &&
							this.getRefinement().isAValidRelation(mtsB, cr, refRelationBCR)) {
						
						doMerge |= this.updateMCRsSet(mcrs, cr, refRelationCRCR);						
					}
					
					this.setAsPossibleTransitions(cr, state, action, coverSet);
				}
				
				if (doMerge) {
					changed = true;
					if (mcrs.size() > 1) {
						bestCR = this.mergeMCRs(mcrs, state, action);
						refRelationACR = this.getRefinement().getLargestRelation(mtsA, bestCR);
						refRelationBCR = this.getRefinement().getLargestRelation(mtsB, bestCR);
						refRelationCRCR = this.getRefinement().getLargestRelation(bestCR, bestCR); // TODO prove that this relation can be keep and use it in order to validate refinement between cr and bestCR.
						cr = this.cloneMTS(bestCR);
						break;
					} else {
						bestCR = mcrs.iterator().next();
					}
					
				} 
				cr = this.cloneMTS(bestCR);								
			}
	
		}while(changed);
							
		assert(this.getRefinement().isARefinement(mtsA, bestCR));
		assert(this.getRefinement().isARefinement(mtsB, bestCR));
		return bestCR;
	}

	private <A> boolean updateMCRsSet(Collection<MTS<Object, A>> mcrs, MTS<Object, A> mcrCandidate, BinaryRelation<Object, Object> refRelationCRCR) {
		boolean addCr = true;
		for (Iterator<MTS<Object, A>> iter = mcrs.iterator(); iter.hasNext();) {
			MTS<Object,A> mcr = iter.next();
			if (this.getRefinement().isAValidRelation(mcr, mcrCandidate, refRelationCRCR)) {
				addCr = false;
				break;
			}
			
			if (this.getRefinement().isAValidRelation(mcrCandidate, mcr, refRelationCRCR) ) {
				iter.remove();
			}
		}
		
		if (addCr) {
			mcrs.add(this.cloneMTS(mcrCandidate));
			return true;
		} else {
			return false;
		}
	}

	private <A> void setAsRequiredTransitions(MTS<Object, A> cr, Object sourceSate, A action, Set<Object> targetStates) {
		for (Object toState : targetStates) {
			cr.addRequired(sourceSate, action, toState);
		}
	}

	private <A> void setAsPossibleTransitions(MTS<Object, A> cr, Object sourceState, A action, Set<Object> targetStates) {
		for (Object toState : targetStates) {
			cr.removeRequired(sourceState, action, toState);
			cr.addPossible(sourceState, action, toState);
		}
	}

	
	/**
	 * 
	 * @param <A>
	 * @param mcrs
	 * @param stateToClone
	 * @param action
	 * @return
	 */
	private <A> MTS<Object,A> mergeMCRs(Collection<MTS<Object,A>> mcrs, Object stateToClone, A action) {
				
		// takes one mcr in order to take from it the common structure of the mcrs. 
		MTS<Object,A> mcr0 = mcrs.iterator().next();
		
		Map<Object,Collection<Object>> state2newStates = this.createNewStatesMapping(mcr0, mcrs.size(),stateToClone);
		
		Object initialState = state2newStates.get(mcr0.getInitialState()).iterator().next();
		
		// creates a new MTS setting the initial state, the actions, and the states.
		MTS<Object,A> result = new MTSImpl<Object, A>(initialState);			
		result.addActions(mcr0.getActions());		
		for (Collection<Object> newStates: state2newStates.values()) {
			result.addStates(newStates);
		}
		
		// Clones shared transition between all mcrs
		for(TransitionType type: TransitionType.values()) {
			if (MAYBE.equals(type)) continue;
			for(Object source: mcr0.getStates()) 
			for(Pair<A,Object> transition: mcr0.getTransitions(source, type)) {
				Object target = transition.getSecond();
				A transitionAction = transition.getFirst();
				
				// if the transition is one of the transitions over which the abstraction is taking place it is ignored.
				if (source.equals(stateToClone) && transitionAction.equals(action)) continue;
				
				for (Object newSource: state2newStates.get(source))
				for (Object newTarget: state2newStates.get(target)){
					result.addTransition(
							newSource,
							transitionAction,
							newTarget,
							type);
				}			
			}
		}
		
		//Clones actions over newState by the specific action.
		Iterator<MTS<Object,A>> mcrIt = mcrs.iterator();
		Iterator<Object> newStateIt = state2newStates.get(stateToClone).iterator();
		while(mcrIt.hasNext()) {
			MTS<Object,A> mcr = mcrIt.next();
			Object newSource = newStateIt.next();
			
			for(TransitionType type: TransitionType.values()) {
				if (MAYBE.equals(type)) continue;
				for(Pair<A,Object> transition: mcr.getTransitions(stateToClone, type)) {
					A transitionAction = transition.getFirst();
					
					if (!transitionAction.equals(action)) continue;
					Object target = transition.getSecond();
					for (Object newTarget: state2newStates.get(target)){
						result.addTransition(
								newSource,
								transitionAction,
								newTarget,
								type);
					}
				}
			}
			
		}
		
		return result;
	}
	
	
	/**
	 * Returns a map, which maps every mts' state to a singleton with itself as a unique element except the stateToClone. 
	 * The mapping for the stateToClone is a set of new object instance, the cardinality of that set if amountNewState.
	 * @param mts
	 * @param amountNewState
	 * @param stateToClone
	 * @return
	 */
	private Map<Object,Collection<Object>> createNewStatesMapping(MTS<Object,?> mts, int amountNewState, Object stateToClone) {
		Map<Object,Collection<Object>> result = new HashMap<Object, Collection<Object>>((int) (mts.getStates().size() / 0.75 +1));
		
		// maps every state with itself
		for (Object state : mts.getStates()) {
			result.put(state, Collections.singleton(state));
		}
		
		// maps stateToClone with a set of amountNewState new Objects.
		Collection<Object> newStates = new LinkedList<Object>(); 
		for(int i=0; i<amountNewState; i++) {
			newStates.add(new Object());
		}		
		result.put(stateToClone, newStates);
				
		return result;
		
	}
	
	/**
	 * Returns a new MTS, which is a clone of passed mts. 
	 * @param <S>
	 * @param <A>
	 * @param mts
	 * @return
	 */
	private <S,A> MTS<Object,A> cloneMTS(MTS<S,A> mts) {
		MTS<Object,A> result = new MTSImpl<Object, A>(mts.getInitialState());
		result.addActions(mts.getActions());
		result.addStates(mts.getStates());
		
		for(TransitionType type: TransitionType.values()) {
			if (MAYBE.equals(type)) continue;
			for(S source: mts.getStates()) {
				for(Pair<A,S> transition: mts.getTransitions(source, type)) {
					result.addTransition(
							source,
							transition.getFirst(),
							transition.getSecond(),
							type);					
				}
			}
				
		}
		return result;
	}
	
		
}
