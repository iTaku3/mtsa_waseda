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
import java.util.Queue;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.lang.Validate;

import MTSTools.ac.ic.doc.commons.collections.PowerSet;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.MapSetBinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.RefinementByRelation;
import MTSTools.ac.ic.doc.mtstools.model.SemanticType;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSQuickClone;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakAlphabetPlusCROperator;
import MTSTools.ac.ic.doc.mtstools.model.operations.MergeBuilder;

public class WeakAlphabetMergeBuilder implements MergeBuilder {

	private Set<?> silentActions;
	
	private WeakAlphabetPlusCROperator crOperator;
		
	public WeakAlphabetMergeBuilder(Set<?> silentActions) {
		this.setCrOperator(new WeakAlphabetPlusCROperator(silentActions));
		this.silentActions = silentActions;
	}
	
	private WeakAlphabetPlusCROperator getCrOperator() {
		return crOperator;
	}



	private void setCrOperator(WeakAlphabetPlusCROperator crOperator) {
		this.crOperator = crOperator;
	}


	@SuppressWarnings("unchecked")
	public <S1, S2, A> MTS<Object, A> merge(MTS<S1, A> mtsA, MTS<S2, A> mtsB) {

		Set<A> alphabetAminusB = new HashSet<A>(CollectionUtils.subtract(mtsA.getActions(), mtsB.getActions()));
		Set<A> alphabetBminusA = new HashSet<A>(CollectionUtils.subtract(mtsB.getActions(), mtsA.getActions()));
		
		alphabetAminusB.addAll((Collection<? extends A>) this.silentActions);
		alphabetBminusA.addAll((Collection<? extends A>) this.silentActions);
		
		RefinementByRelation refA = SemanticType.WEAK.getRefinement(alphabetBminusA);
		RefinementByRelation refB = SemanticType.WEAK.getRefinement(alphabetAminusB);
		RefinementByRelation refCR = SemanticType.WEAK.getRefinement(this.silentActions);
		
		MTS<Object,A> bestCR = this.cloneMTS(this.getCrOperator().compose(mtsA, mtsB));
		bestCR.removeUnreachableStates();
		int maxStates = bestCR.getStates().size() * 4;
		
		Validate.isTrue(refA.isARefinement(mtsA, bestCR),"CR doesn't refine model A");
		Validate.isTrue(refB.isARefinement(mtsB, bestCR),"CR doesn't refine model B");
	
		
		Queue<Object> stateToProcess = new LinkedList<Object>();
		
		
		Set<Set<A>> actionsPartition = this.createActionsPartition(mtsA.getActions(), mtsB.getActions());
		
		int clones=0;
		int improvements=0;
		
		boolean changed;
		do {
			changed = false;
			Object state;
			stateToProcess.addAll(bestCR.getStates());	
			while ((state = stateToProcess.poll())!=null && bestCR.getStates().size() < maxStates  && !Thread.currentThread().isInterrupted() ) 				
			for (Set<A> actions : actionsPartition) {
				
				
				// Collects all transitions by action. If action if unobservable by one of the models,
				// then it also collects all the transitions by all unobservable actions to that model.
				BinaryRelation<A, Object> reqTransitions =  new MapSetBinaryRelation<A, Object>();
				for (A reqAction : actions) {
					reqTransitions.getImage(reqAction).addAll(
							bestCR.getTransitions(state, REQUIRED).getImage(reqAction));								
				}
				
				if (reqTransitions.isEmpty()) continue;
				
				
				boolean doMerge = false;
				Collection<MTS<Object,A>> mcrs = new LinkedList<MTS<Object,A>>();
				mcrs.add(bestCR);
				
				
				MTS<Object,A> cr = this.quickCloneMTS(bestCR, state);
				
				this.setAsPossibleTransitions(cr, state, reqTransitions);
				
				for (Set<Pair<A,Object>> coverSet : new PowerSet<Pair<A,Object>>(reqTransitions)){										
					if (Thread.currentThread().isInterrupted()) {
						break;
					}
					
					this.setAsRequiredTransitions(cr, state, coverSet);
					
					if (	refA.isARefinement(mtsA, cr) &&
							refB.isARefinement(mtsB, cr)) {
						
						doMerge |= this.updateMCRsSet(mcrs, cr, refCR, state);						
					}
					
					this.setAsPossibleTransitions(cr, state, coverSet);
				}				
				
				if (doMerge) {
//					System.out.println(actions.toString());
					changed = true;
					if (mcrs.size() > 1) {
						clones++;
//						System.out.println("Cloning states");
						bestCR = this.mergeMCRs(mcrs, state, actions, stateToProcess);
						break;
					} else {
						improvements++;						
						bestCR = this.cloneMTS(mcrs.iterator().next());
					}
				} 
			}
	
		}while(changed && bestCR.getStates().size() < maxStates && !Thread.currentThread().isInterrupted());

		if (bestCR.getStates().size() == maxStates ) {
//			System.out.println("Merge algorithm stopped after reaching maximum amount of clones");
		}
//		System.out.println("Improvements: " + improvements);
//		System.out.println("Clones: " + clones  );
		assert(refA.isARefinement(mtsA, bestCR));
		assert(refB.isARefinement(mtsB, bestCR));
		return bestCR;
	}
	
	
	@SuppressWarnings("unchecked")
	private <A> Set<Set<A>> createActionsPartition(Set<A> setA, Set<A> setB) {
		Set<Set<A>> result = new HashSet<Set<A>>();
		
		Set<A> diff = new HashSet<A>(CollectionUtils.subtract(setA, setB));
		if (!diff.isEmpty() ) {
			diff.addAll((Collection<? extends A>) this.silentActions);
			result.add(diff);
		}
		
		diff = new HashSet<A>(CollectionUtils.subtract(setB, setA));		
		if (!diff.isEmpty() ) {
			diff.addAll((Collection<? extends A>) this.silentActions);
			result.add(diff);
		}
		
		if (result.isEmpty()) {
			result.add((Set<A>) this.silentActions);
		}
		
		Collection<A> intersection = CollectionUtils.intersection(setA, setB);
		intersection.removeAll(this.silentActions);
		for (A elem : intersection) {
			result.add(Collections.singleton(elem));
		}
		return result;
	}

	private <A> boolean updateMCRsSet(Collection<MTS<Object, A>> mcrs, MTS<Object, A> mcrCandidate, RefinementByRelation refCR, Object state) {
		boolean addCr = true;
		for (Iterator<MTS<Object, A>> iter = mcrs.iterator(); iter.hasNext();) {
			MTS<Object,A> mcr = iter.next();
			if (refCR.isARefinement(mcr, mcrCandidate)) {
				addCr = false;
				break;
			}
			
			if (refCR.isARefinement(mcrCandidate, mcr) ) {
				iter.remove();
			}
		}
		
		if (addCr) {
			mcrs.add(this.quickCloneMTS(mcrCandidate,state));
			return true;
		} else {
			return false;
		}
	}

	private <A> void setAsRequiredTransitions(MTS<Object, A> cr, Object sourceSate, Set<Pair<A,Object>> targetTransitions) {
		for (Pair<A,Object> reqTransition: targetTransitions) {
			cr.addRequired(sourceSate, reqTransition.getFirst(), reqTransition.getSecond());
		}
	}

	private <A> void setAsPossibleTransitions(MTS<Object, A> cr, Object sourceState, Set<Pair<A,Object>> targetTransitions) {
		for (Pair<A,Object> reqTransition: targetTransitions) {
			cr.removeRequired(sourceState, reqTransition.getFirst(), reqTransition.getSecond());
			cr.addPossible(sourceState, reqTransition.getFirst(), reqTransition.getSecond());
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
	private <A> MTS<Object,A> mergeMCRs(Collection<MTS<Object,A>> mcrs, Object stateToClone, Set<A> actions, Collection<Object> newStates) {
				
		// takes one mcr in order to take from it the common structure of the mcrs. 
		MTS<Object,A> mcr0 = mcrs.iterator().next();
		
		Map<Object,Collection<Object>> state2newStates = this.createNewStatesMapping(mcr0, mcrs.size(),stateToClone, newStates);
		
		Object initialState = state2newStates.get(mcr0.getInitialState()).iterator().next();
		
		// creates a new MTS setting the initial state, the actions, and the states.
		MTS<Object,A> result = new MTSImpl<Object, A>(initialState);			
		result.addActions(mcr0.getActions());		
		for (Collection<Object> states: state2newStates.values()) {
			result.addStates(states);
		}
		
		// Clones shared transition between all mcrs
		for(TransitionType type: TransitionType.values()) {
			if (MAYBE.equals(type)) continue;
			for(Object source: mcr0.getStates()) 
			for(Pair<A,Object> transition: mcr0.getTransitions(source, type)) {
				Object target = transition.getSecond();
				A transitionAction = transition.getFirst();
				
				// if the transition is one of the transitions over which the abstraction is taking place it is ignored.
				if (source.equals(stateToClone) && actions.contains(transitionAction)) continue;
				
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
					
					if (!actions.contains(transitionAction)) continue;
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
	private Map<Object,Collection<Object>> createNewStatesMapping(MTS<Object,?> mts, int amountNewState, Object stateToClone, Collection<Object> newStates) {
		Map<Object,Collection<Object>> result = new HashMap<Object, Collection<Object>>((int) (mts.getStates().size() / 0.75 +1));
		
		// maps every state with itself
		for (Object state : mts.getStates()) {
			result.put(state, Collections.singleton(state));
		}
		
		// maps stateToClone with a set of amountNewState new Objects.
		Collection<Object> states = new LinkedList<Object>(); 
		for(int i=0; i<amountNewState; i++) {
			states.add(new Object());
		}		
		newStates.addAll(states);
		result.put(stateToClone, states);
				
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
	
	/**
	 * Returns a new MTS, which is a clone of passed mts. 
	 * @param <S>
	 * @param <A>
	 * @param mts
	 * @return
	 */
	private <S,A> MTS<S,A> quickCloneMTS(MTS<S,A> mts, S state) {
		MTSQuickClone<S,A> result = new MTSQuickClone<S, A>(mts);
		result.deepClone(state);
		return result;
	}
	
		
}
