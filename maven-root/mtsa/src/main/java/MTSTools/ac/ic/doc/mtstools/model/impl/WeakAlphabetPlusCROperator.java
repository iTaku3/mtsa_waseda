package MTSTools.ac.ic.doc.mtstools.model.impl;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.POSSIBLE;
import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.REQUIRED;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.lang.Validate;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.SemanticType;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.operations.Consistency;


public class WeakAlphabetPlusCROperator {
	private Consistency consistency;
	private Set<?> silentActions;
	
	public WeakAlphabetPlusCROperator() {
		this(Collections.emptySet());
	}
	
	public WeakAlphabetPlusCROperator(Set<?> silentActions){
		this.setConsistency(SemanticType.WEAK_ALPHABET.getConsistency(silentActions));
		this.setSilentActions(silentActions);
	}
		
	public Consistency getConsistency() {
		return consistency;
	}

	public void setConsistency(Consistency consistency) {
		this.consistency = consistency;
	}




	@SuppressWarnings("unchecked")
	public <S1,S2,A> MTS<Pair<S1,S2>, A> compose(MTS<S1,A> mtsA, MTS<S2, A> mtsB) {
		Validate.isTrue(!mtsA.getStates().contains(null));
		Validate.isTrue(!mtsB.getStates().contains(null));
		
		Set<A> alphabetAminusB = new HashSet<A>(CollectionUtils.subtract(mtsA.getActions(), mtsB.getActions()));
		alphabetAminusB.removeAll(this.getSilentActions());
		Set<A> alphabetBminusA = new HashSet<A>(CollectionUtils.subtract(mtsB.getActions(), mtsA.getActions()));
		alphabetBminusA.removeAll(this.getSilentActions());
		
		Set<A> alphabet = new HashSet<A>(mtsA.getActions());
		alphabet.addAll(mtsB.getActions());
		alphabet.removeAll(this.getSilentActions());
		if (!this.getSilentActions().isEmpty()) {
			alphabet.add((A) this.getAnySilectAction());
		}
	
		Set<Pair<S1, S2>> consistencyRelation = this.getConsistency().getConsistencyRelation(mtsA, mtsB);
		
		Pair<S1,S2> initialState = Pair.create(mtsA.getInitialState(), mtsB.getInitialState());
		
		Validate.isTrue(consistencyRelation.contains(initialState), "+cr cannot be applied to inconsistent models.");
		
		MTS<Pair<S1, S2>, A> result = new MTSImpl<Pair<S1, S2>, A>(initialState);
		result.addStates(consistencyRelation);
		result.addActions(mtsA.getActions());
		result.addActions(mtsB.getActions());		
		
		ClousurePathBuilder clousure = ClousurePathBuilder.getInstance();
		
		Iterator<List<Pair<A, S1>>> aClousureIt = null;
		Iterator<List<Pair<A, S2>>> bClousureIt = null;
		
		for(Pair<S1, S2> state: result.getStates()) {
			for(A label: alphabet) {
				boolean belongsToB = !alphabetAminusB.contains(label);
				boolean belongsToA = !alphabetBminusA.contains(label);
				if ( belongsToB && belongsToA) {
					// label is a common label
					aClousureIt = clousure.getPathsIterator(mtsA, state.getFirst(), label, POSSIBLE,(Set<A>) this.getSilentActions());
					
					boolean firstIteration=true;
					while(aClousureIt.hasNext() ) {
						// PR rule
						Pair<A,S1> aTransition = this.getNextTransition(aClousureIt);
						for(S2 bPrime : mtsB.getTransitions(state.getSecond(),REQUIRED).getImage(label)) {
							this.applyRule(result, state, label, Pair.create(aTransition.getSecond(), bPrime), REQUIRED);
						}
						
						bClousureIt = clousure.getPathsIterator(mtsB, state.getSecond(), label, POSSIBLE,(Set<A>) this.getSilentActions());	
						while(bClousureIt.hasNext() ) {
							Pair<A,S2> bTransition = this.getNextTransition(bClousureIt);
							if (firstIteration) {
								// 	RP rule
								for(S1 aPrime : mtsA.getTransitions(state.getFirst(), REQUIRED).getImage(label)) {
									this.applyRule(result, state, label, Pair.create(aPrime, bTransition.getSecond()), REQUIRED);
								}
							}							
							// PP rule
							this.applyRule(result, state, label, Pair.create(aTransition.getSecond(), bTransition.getSecond()), POSSIBLE);
						}
						firstIteration=false;
					}
				} else {
					// IP & PI rules
					A aLabel = (A) (belongsToA?label:this.getAnySilectAction());
					A bLabel = (A) (belongsToB?label:this.getAnySilectAction());
					aClousureIt = clousure.getPathsIterator(mtsA, state.getFirst(), aLabel, POSSIBLE,(Set<A>) this.getSilentActions());
					while(aClousureIt.hasNext()) {
						S1 aPrime = getNextTransition(aClousureIt).getSecond();						
					    
						bClousureIt = clousure.getPathsIterator(mtsB, state.getSecond(), bLabel, POSSIBLE,(Set<A>) this.getSilentActions());
						while(bClousureIt.hasNext()) {
							S2 bPrime = getNextTransition(bClousureIt).getSecond();							
							this.applyRule(result, state, label, Pair.create(aPrime, bPrime), REQUIRED);
						}
					}
				}
			}
		}
		result.removeUnreachableStates();		
		this.removeSilentSelfLoops(result);		
		return result;
	}

	@SuppressWarnings("unchecked")
	private <A,S> void removeSilentSelfLoops(MTS<S,A> mts) {
		for(Object label: this.getSilentActions()) {
			for(S state: mts.getStates()) {
				mts.removePossible(state, (A) label, state);
			}
		}
	}
	
	private <A,S> Pair<A, S> getNextTransition( Iterator<List<Pair<A, S>>> pathIterator) {
		if (pathIterator.hasNext()) {
			return pathIterator.next().get(1);
		} else {
			return Pair.create(null, null);
		}
	}
	
	private <S1,S2,A>  void applyRule(MTS<Pair<S1, S2>, A> cr, Pair<S1,S2> from, A label, Pair<S1,S2> to, TransitionType type ) {
		if (cr.getStates().contains(to)) {
			cr.addTransition(from, label, to, type);
		}
	}
	
	private Set<?> getSilentActions() {
		return silentActions;
	}

	private void setSilentActions(Set<?> silentActions) {
		this.silentActions = silentActions;
	}
	
	private Object getAnySilectAction() {
		if (this.getSilentActions().isEmpty()) {
			return null;			
		}
		return this.getSilentActions().iterator().next();
	}
	
}
