package MTSSynthesis.ar.dc.uba.synthesis;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.SetUtils;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSSynthesis.ar.dc.uba.model.condition.ZetaFunction;
import MTSSynthesis.ar.dc.uba.model.language.CanonicalSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.ar.dc.uba.model.language.Word;
import MTSSynthesis.ar.dc.uba.model.structure.Obligations;
import MTSSynthesis.ar.dc.uba.model.structure.ObligationsFromUniversal;
import MTSSynthesis.ar.dc.uba.model.structure.SynthesizedState;

/**
 * Synthesis algorithm. From Universal triggered scenario to MTS 
 * @author gsibay
 *
 */
public class MTSFromUTriggeredScenarioSynthesiser extends MTSFromTriggeredScenarioSynthesiser {

	private static final Set<Word> EMPTY_WORD_SET = new HashSet<Word>();
	
	@Override
	protected Set<SynthesizedState> processState(
			MTS<SynthesizedState, Symbol> mts,
			SynthesizedState anUnprocessedState) {
		Set<SynthesizedState> newStates = new HashSet<SynthesizedState>();
		
		for (Symbol symbol : this.getRestrictedSymbols()) {
			// adds the transitions (if applicable) for the unprocessed state and new states may require to be processed
			newStates.addAll(this.addTransitions(mts, anUnprocessedState, symbol));
		}
		return newStates;
	}

	@Override
	protected SynthesizedState createInitialState(ZetaFunction initialZetaFunction) {

		// Initial state's word is the canonical representation of the empty word epsilon
		Symbol firstSymbol = new CanonicalSymbol(this.getSynthesiserHelper().getConditionsHolding(initialZetaFunction));
		Word initialWord = new Word(Collections.singletonList(firstSymbol));

		Set<Word> requiredPaths;
		Set<Word> possiblePaths;
		if(this.satisfiesPrechart(initialWord)) {
			// Every word in the Mainchart is an obligation
			requiredPaths = this.getLm().getWords();
			possiblePaths = EMPTY_WORD_SET;  
		} else {
			requiredPaths = new HashSet<Word>(); // no obligations
			
			// as there are no required obligations, everything is possible
			possiblePaths = this.createSingletonWordsFromSymbols(this.getRestrictedSymbols());	
		}
		
		return new SynthesizedState(initialWord, 
				new ObligationsFromUniversal(requiredPaths, possiblePaths), 
				initialZetaFunction);
	}
	
	protected Collection<SynthesizedState> addTransitions(MTS<SynthesizedState, Symbol> mts,
			SynthesizedState state, Symbol t) {
		Set<SynthesizedState> newStates = new HashSet<SynthesizedState>();

		Word significativeSuffix = state.getSignificativeSuffix();
		
		// initialise the next zeta function as a copy of the previous one
		ZetaFunction nextZetaFunction = state.getZetaFunction().getCopy();
		
		//next state's significative suffix, and update the zetaFunction
		Word nextSignificativeSuffix = this.next(significativeSuffix, t, nextZetaFunction);

		Set<Word> nextStateMayPaths;
		Set<Word> nextStateReqPaths;
		
		// add the transitions (if applicable)
		
		ObligationsFromUniversal obligations = (ObligationsFromUniversal)state.getObligations();
		Set<Word> reqPaths = obligations.getRequiredPaths();
		Set<Word> possPaths = obligations.getMaybePaths();
		
		Set<Word> reqPathsStartingWithCurrentSymbol = this.startingWith(reqPaths, t);
		Set<Word> posPathsStartingWithCurrentSymbol = this.startingWith(possPaths, t);
		
		SynthesizedState nextState;
		
		// Try to add the required transitions
		if(!reqPathsStartingWithCurrentSymbol.isEmpty()) { // there is a required path starting with the current symbol
			
			for (Word reqPathStartingWithCS : reqPathsStartingWithCurrentSymbol) {
				
				if (this.satisfiesPrechart(nextSignificativeSuffix)) {
					// The prechart holds in the next state. 
					// the required path will be all the mainchart (and nothing else)
					// and the maybe path will be empty

					// Every word in the mainchart is required and only that
					// ----- NEXT STATE REQUIRED PATHS ------
					nextStateReqPaths = this.getLm().getWords();
					// ----- NEXT STATE POSSIBLE PATHS ------
					nextStateMayPaths = SetUtils.EMPTY_SET;

					// add the next state as a required transition
					// create next state obligations with possible and required paths
					Obligations nextStateObligations = new ObligationsFromUniversal(nextStateReqPaths, nextStateMayPaths);
					// Add as required transition
					nextState = new SynthesizedState(nextSignificativeSuffix, nextStateObligations , nextZetaFunction);
					this.addAsRequiredTransition(state, t, nextState, mts, newStates);
				} else {
						
					// ----- NEXT STATE REQUIRED PATHS ------
					// the new required paths will be:
					// the selected required path without the first symbol (if not empty)
					nextStateReqPaths = this.nonEmptyWordFollows(Collections.singleton(reqPathStartingWithCS), t);
					
					// ----- NEXT STATE POSSIBLE PATHS ------
					
					// pos and req paths after current symbol are needed
					Set<Word> mayPathsAfterCurrentSymbol = this.nonEmptyWordFollows(posPathsStartingWithCurrentSymbol, t);
					Set<Word> reqPathsAfterCurrentSymbol = this.nonEmptyWordFollows(reqPathsStartingWithCurrentSymbol, t);
					
					// the next state possible paths are:
					// all possible path after current symbol and, all required paths after current symbol
					// (each one will be required in another branch created by the current "for") after current symbol 
					// but the required path starting with CS being processed. nextStateReqPaths is only one path.
					nextStateMayPaths = new HashSet<Word>(
							CollectionUtils.subtract(CollectionUtils.union(mayPathsAfterCurrentSymbol, reqPathsAfterCurrentSymbol),
									nextStateReqPaths)
					);
					
					// if there are no req obligations then all the restricted symbols are possible
					if(nextStateReqPaths.isEmpty()) {
						nextStateMayPaths = new HashSet<Word>(
								CollectionUtils.union(nextStateMayPaths, this.createSingletonWordsFromSymbols(this.getRestrictedSymbols())));
					}
					
					
					// ------ ADD THE NEXT STATE ------
					// create next state obligations with possible and required paths
					Obligations nextStateObligations = new ObligationsFromUniversal(nextStateReqPaths, nextStateMayPaths);
					
					// Add as required transition
					nextState = new SynthesizedState(nextSignificativeSuffix, nextStateObligations , nextZetaFunction.getCopy());
					this.addAsRequiredTransition(state, t, nextState, mts, newStates);
				}
			}
		}
		
		// Try to add the possible transitions
		if(!posPathsStartingWithCurrentSymbol.isEmpty()) {  // there is a possible path starting with the current symbol 
			
			for (Word possPathStartingWithCS : posPathsStartingWithCurrentSymbol) {
				
				if (this.satisfiesPrechart(nextSignificativeSuffix)) {
					// The prechart holds in the next state. 
					// the required path will be all the mainchart (and nothing else)
					// and the maybe path will be empty

					// Every word in the mainchart is required and only that
					// ----- NEXT STATE REQUIRED PATHS ------
					nextStateReqPaths = this.getLm().getWords();
					// ----- NEXT STATE POSSIBLE PATHS ------
					nextStateMayPaths = SetUtils.EMPTY_SET;

					// add the next state as a maybe transition
					// create next state obligations with possible and required paths
					Obligations nextStateObligations = new ObligationsFromUniversal(nextStateReqPaths, nextStateMayPaths);
					// Add as maybe transition
					nextState = new SynthesizedState(nextSignificativeSuffix, nextStateObligations , nextZetaFunction);
					this.addAsMaybeTransition(state, t, nextState, mts, newStates);
					
				} else { // The prechart does not hold
					
					// ----- NEXT STATE REQUIRED PATHS ------
					// the current possible path will be required in the next state (taking this maybe transition
					// makes the possible path mandatory)
					// the selected possible path without the first symbol (if not empty)
					nextStateReqPaths = this.nonEmptyWordFollows(Collections.singleton(possPathStartingWithCS), t);
					
					
					Set<Word> mayPathsAfterCurrentSymbol = this.nonEmptyWordFollows(posPathsStartingWithCurrentSymbol, t);
					
					// ----- NEXT STATE POSSIBLE PATHS ------
					// the next state possible paths are:
					// all possible path after current symbol except the one being processed that is going
					// to be required (nextStateReqPaths is only one word).
					nextStateMayPaths = new HashSet<Word>(
							CollectionUtils.subtract(mayPathsAfterCurrentSymbol, nextStateReqPaths));
					
					// if there are no req paths in the next state then all the restricted symbols are added as possible
					if(nextStateReqPaths.isEmpty()) {
						nextStateMayPaths = new HashSet<Word>(
								CollectionUtils.union(nextStateMayPaths, this.createSingletonWordsFromSymbols(this.getRestrictedSymbols())));
					}
					
					// ------ ADD THE NEXT STATE ------
					
					// create next state obligations with possible and required paths
					Obligations nextStateObligations = new ObligationsFromUniversal(nextStateReqPaths, nextStateMayPaths);
					
					// Add as possible transition
					nextState = new SynthesizedState(nextSignificativeSuffix, nextStateObligations , nextZetaFunction.getCopy());
					this.addAsMaybeTransition(state, t, nextState, mts, newStates);
					
				}
			}
		}
		return newStates;
	}
	

	/**
	 * Creates a set of words from the set of symbols.
	 * The result contains one word for each symbol. The word
	 * is just the symbol.
	 * @param symbols
	 * @return
	 */
	private Set<Word> createSingletonWordsFromSymbols(Set<Symbol> symbols) {
		Set<Word> result = new HashSet<Word>();
		for (Symbol symbol : symbols) {
			result.add(new Word(Collections.singletonList(symbol)));
		}
		return result;
	}

	@Override
	protected boolean hasTauTransitions() {
		return false;
	}
}
