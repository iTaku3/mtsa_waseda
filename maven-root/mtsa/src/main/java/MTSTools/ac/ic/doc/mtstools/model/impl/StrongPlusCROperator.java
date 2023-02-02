package MTSTools.ac.ic.doc.mtstools.model.impl;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.POSSIBLE;
import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.REQUIRED;

import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.SemanticType;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.operations.Consistency;

/**
 * This class implements the +CR operator.
 * @author fdario
 *
 */
public class StrongPlusCROperator {

	private Consistency strongConsistency;
	
	public StrongPlusCROperator() {
		this.setStrongConsistency(SemanticType.STRONG.getConsistency());
	}
	
	
	private Consistency getStrongConsistency() {
		return strongConsistency;
	}


	private void setStrongConsistency(Consistency strongConsistency) {
		this.strongConsistency = strongConsistency;
	}




	public <S1,S2,A> MTS<Pair<S1,S2>, A> compose(MTS<S1,A> mtsA, MTS<S2, A> mtsB) {
		// Model A & B must have the same alphabet
		assert(mtsA.getActions().equals(mtsB.getActions())); 
		
		Set<Pair<S1, S2>> consistencyRelation = this.getStrongConsistency().getConsistencyRelation(mtsA, mtsB);
		
		Pair<S1,S2> initialState = Pair.create(mtsA.getInitialState(), mtsB.getInitialState());		
		
		MTS<Pair<S1, S2>, A> result = new MTSImpl<Pair<S1, S2>, A>(initialState);
		result.addStates(consistencyRelation);
		result.addActions(mtsA.getActions());
		
		for(Pair<S1, S2> state: result.getStates()) {
			
			this.processRule(	state,
								mtsA.getTransitions(POSSIBLE).get(state.getFirst()),
								mtsB.getTransitions(POSSIBLE).get(state.getSecond()),
								POSSIBLE,
								result);
			
			this.processRule(	state,
								mtsA.getTransitions(REQUIRED).get(state.getFirst()),
								mtsB.getTransitions(POSSIBLE).get(state.getSecond()),
								REQUIRED,
								result);			
			
			this.processRule(	state,
								mtsA.getTransitions(POSSIBLE).get(state.getFirst()),
								mtsB.getTransitions(REQUIRED).get(state.getSecond()),
								REQUIRED,
								result);
			
		}
		
		return result;
	}

	private <S1,S2,A> void processRule(Pair<S1,S2> startState, BinaryRelation<A, S1> transitionsA, BinaryRelation<A, S2> transitionsB, TransitionType newTranstitionsType, MTS<Pair<S1,S2>, A> mtsCR ) {
		for(Pair<A,S1> transitionA: transitionsA) {
			for(S2 endStateB: transitionsB.getImage(transitionA.getFirst())) {
				Pair<S1,S2> endState = Pair.create(transitionA.getSecond(), endStateB);
				if (mtsCR.getStates().contains(endState)) {
					mtsCR.addTransition(startState, transitionA.getFirst(), endState, newTranstitionsType);
				}
			}
		}		
	}
	
}