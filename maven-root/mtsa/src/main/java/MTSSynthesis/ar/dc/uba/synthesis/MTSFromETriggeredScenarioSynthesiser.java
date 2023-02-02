package MTSSynthesis.ar.dc.uba.synthesis;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.SetUtils;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSSynthesis.ar.dc.uba.model.condition.ZetaFunction;
import MTSSynthesis.ar.dc.uba.model.language.Alphabet;
import MTSSynthesis.ar.dc.uba.model.language.CanonicalSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.ar.dc.uba.model.language.Word;
import MTSSynthesis.ar.dc.uba.model.lsc.TriggeredScenario;
import MTSSynthesis.ar.dc.uba.model.structure.ObligationsFromExistential;
import MTSSynthesis.ar.dc.uba.model.structure.SynthesizedState;

/**
 * Synthesis algorithm. From Universal triggered scenario to MTS
 * @author gsibay
 *
 */
public class MTSFromETriggeredScenarioSynthesiser extends MTSFromTriggeredScenarioSynthesiser {

	@Override
	public MTS<SynthesizedState, Symbol> synthesise(TriggeredScenario triggeredScenario) {
		// The result of super.synthesise is the synthesised MTS as described in ICSE08 and TSE2010
		MTS<SynthesizedState, Symbol> synthesisedMTS = super.synthesise(triggeredScenario);
		
		// Some taus have to be added to get a complete and correct algorithm under branching semantics (see TSE2010)
		this.tauClosure(synthesisedMTS);
		
		return synthesisedMTS;
	}
	
	private void tauClosure(MTS<SynthesizedState, Symbol> synthesisedModel) {
		for (SynthesizedState state : synthesisedModel.getStates()) {
			this.tauClosureState(state, synthesisedModel);
		}		
	}

	/**
	 * If state can transition through t (t != TAU) to state s2 and there is a TAU? transition to a
	 * state s3, then a t? transition is added from state to s3.
	 * 
	 * state - t -> s2 -> tau? -> s3  ==>  s1 - t? -> s3
	 * 
	 * @param state
	 * @param synthesisedModel
	 */
	private void tauClosureState(SynthesizedState state, MTS<SynthesizedState, Symbol> synthesisedModel) {
		for (Pair<Symbol, SynthesizedState> transition : synthesisedModel.getTransitions(state, TransitionType.POSSIBLE)) {
			
			Symbol t = transition.getFirst();
			if (!t.equals(Alphabet.TAU)) {
			
				SynthesizedState s2 = transition.getSecond();
				for (Pair<Symbol, SynthesizedState> transitionFromStateAfterT : 
					synthesisedModel.getTransitions(s2, TransitionType.POSSIBLE) ) {
					if (transitionFromStateAfterT.getFirst().equals(Alphabet.TAU)) {
						// "t" and then "tau?" can be taken from "state". Then "t?" from
						// "state" to the reached state after "t tau?" is added
						synthesisedModel.addPossible(state, t, transitionFromStateAfterT.getSecond());
					}
				}
				
			}
		}

	}

	@Override
	protected SynthesizedState createInitialState(ZetaFunction initialZetaFunction) {
		// Initial state's word is the operational representation of the empty word epsilon
		Symbol firstSymbol = new CanonicalSymbol(this.getSynthesiserHelper().getConditionsHolding(initialZetaFunction));
		Word initialWord = new Word(Collections.singletonList(firstSymbol));

		Set<Word> requiredPaths = new HashSet<Word>(); // no obligations
		if(this.satisfiesPrechart(initialWord)) {
			// Every word in the Mainchart is an obligation
			requiredPaths = this.getLm().getWords();
		}
				
		return new SynthesizedState(initialWord, new ObligationsFromExistential(requiredPaths), initialZetaFunction);
	}

	@Override
	protected Set<SynthesizedState> processState(MTS<SynthesizedState, Symbol> mts,
			SynthesizedState anUnprocessedState) {
		Set<SynthesizedState> newStates = new HashSet<SynthesizedState>();
		
		// Improved version: Add tau transition first
		// Adds tau transitions (if its the case)
		if(!anUnprocessedState.getObligations().isEmpty()) { // There are obligations in current unprocessed state
			// Add a tau transition
			newStates.addAll(this.addTransitions(mts, anUnprocessedState, Alphabet.TAU));
		}
		
		for (Symbol symbol : this.getRestrictedSymbols()) {
			// adds the transitions for the unprocessed state and new states may require to be processed
			newStates.addAll(this.addTransitions(mts, anUnprocessedState, symbol));
		}
		return newStates;
	}
	
	/* (non-Javadoc)
	 * @see ar.dc.uba.synthesis.MTSFromLSCSynthesizer#addTransitions(ac.ic.doc.mtstools.model.MTS, ar.dc.uba.model.structure.SynthesizedState, ar.dc.uba.model.language.Symbol)
	 */
	@Override
	protected Collection<SynthesizedState> addTransitions(MTS<SynthesizedState, Symbol> mts,
			SynthesizedState state, Symbol t) {
		Collection<SynthesizedState> newStates = new HashSet<SynthesizedState>();

		Word significativeSuffix = state.getSignificativeSuffix();
		
		// initialize the next zeta function as a copy of the previous one
		ZetaFunction nextZetaFunction = state.getZetaFunction().getCopy();
		
		//next state's significative suffix, and update the zetaFunction
		Word nextSignificativeSuffix = this.next(significativeSuffix, t, nextZetaFunction);
		
		Set<Word> newRequiredPaths = new HashSet<Word>(); // No new obligations
		if (this.satisfiesPrechart(nextSignificativeSuffix)) {
			// Every word in the Mainchart is an obligation
			newRequiredPaths = this.getLm().getWords();
		} 
		
		
		// Add the new transitions

		SynthesizedState nextState;

		ObligationsFromExistential obligations = ((ObligationsFromExistential) state.getObligations());
		// Each requiredPath starting with t must be satisfied from this state
		Set<Word> requiredPathsStartingWithCurrentSymbol = this.follows(obligations.getRequiredPaths(), t);
		for (Word requiredPath : requiredPathsStartingWithCurrentSymbol) {
			
			Set<Word> inheritedRequiredPath;
			if(requiredPath.equals(Word.EMPTY_WORD)) { // End of a required path. 
				// There is no obligation's propagation
				inheritedRequiredPath = SetUtils.EMPTY_SET;
			} else {
				inheritedRequiredPath = Collections.singleton(requiredPath);
			}
			
			// The next state's mandatory paths is the union of newMandatoryPaths and oldMandatoryPaths
			Set<Word> nextStateObligations = new HashSet<Word>(CollectionUtils.union(newRequiredPaths, inheritedRequiredPath));
			
			// Add as required transition
			nextState = new SynthesizedState(nextSignificativeSuffix, new ObligationsFromExistential(nextStateObligations), nextZetaFunction.getCopy());
			this.addIfNotPresent(nextState, mts, newStates);
			mts.addTransition(state, t, nextState, TransitionType.REQUIRED);
		}
		
		if (!this.firsts(obligations.getRequiredPaths()).contains(t)) {
			
			// IMPROVED VERSION:
			// If tau and then t can be done to an equal state, do not add it as a transition.
			// Add as maybe transition only if 
			nextState = new SynthesizedState(nextSignificativeSuffix,
					new ObligationsFromExistential(newRequiredPaths), nextZetaFunction.getCopy());
			BinaryRelation<Symbol, SynthesizedState> transitionsFromState = mts.getTransitions(state, TransitionType.MAYBE);
			Iterator<SynthesizedState> statesAfterTauIt = transitionsFromState.getImage(Alphabet.TAU).iterator();
			boolean isRedundantTransition = false;
			while (statesAfterTauIt.hasNext() && !isRedundantTransition) {
				SynthesizedState stateAfterTau = statesAfterTauIt.next();
				if(mts.getTransitions(stateAfterTau, TransitionType.MAYBE).getImage(t).contains(nextState)) {
					isRedundantTransition = true;
				}
			}
			 
			if (!isRedundantTransition){
				this.addIfNotPresent(nextState, mts, newStates);
				// mts.addTransition(state, t, nextState, TransitionType.MAYBE);
				mts.addPossible(state, t, nextState);
			}
		}
		
		return newStates;
	}

	@Override
	protected boolean hasTauTransitions() {
		return true;
	}
}
