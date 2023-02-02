package MTSSynthesis.ar.dc.uba.synthesis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.SetUtils;
import org.apache.commons.collections15.functors.EqualPredicate;
import org.apache.commons.collections15.functors.NotPredicate;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSSynthesis.ar.dc.uba.model.condition.Condition;
import MTSSynthesis.ar.dc.uba.model.condition.ZetaFunction;
import MTSSynthesis.ar.dc.uba.model.language.Alphabet;
import MTSSynthesis.ar.dc.uba.model.language.Language;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.ar.dc.uba.model.language.Word;
import MTSSynthesis.ar.dc.uba.model.lsc.TriggeredScenario;
import MTSSynthesis.ar.dc.uba.model.structure.SynthesizedState;
import MTSSynthesis.ar.dc.uba.util.SynthesiserHelper;
import MTSSynthesis.ar.dc.uba.util.WordUtils;

/**
 * 
 * Synthesizes a MTS from a modal TriggeredScenario. The MTS satisfies the TriggeredScenario and
 * every MTS that satisfies the TriggeredScenario is a observational refinement of the syntheized one.
 * 
 * @author gsibay
 *
 */
public abstract class MTSFromTriggeredScenarioSynthesiser {

	private SynthesiserHelper synthesiserHelper;
		
	// Mainchart's generated language
	private Language lm;

	private Set<Condition> conditions;

	/**
	 * The scenario's restricted alphabet
	 */
	private Set<Symbol> restrictedSymbols;

	protected SynthesiserHelper getSynthesiserHelper() {
		return synthesiserHelper;
	}

	protected Language getLm() {
		return lm;
	}

	protected Set<Condition> getConditions() {
		return conditions;
	}

	protected Set<Symbol> getRestrictedSymbols() {
		return restrictedSymbols;
	}

	private void init(TriggeredScenario lsc) {
		this.lm = lsc.getMainchart().getGeneratedLanguage();
		
		this.restrictedSymbols = new HashSet<Symbol>(lsc.getAlphabet().getSymbols());
		// the restricted symbols are initiated here and never modified
		this.restrictedSymbols = SetUtils.unmodifiableSet(this.restrictedSymbols);
		
		// initialises the helper
		this.synthesiserHelper = new SynthesiserHelper(lsc);
	}
	
	public MTS<SynthesizedState, Symbol> synthesise(TriggeredScenario triggeredScenario) {
		this.init(triggeredScenario);
		
		// There are no states to process
		Set<SynthesizedState> unprocessedStates = new HashSet<SynthesizedState>();
		
		// create the initial valuation (ZetaFunction)
		ZetaFunction initialZetaFunction = new ZetaFunction(triggeredScenario.getFluents());
		
		SynthesizedState initialState = this.createInitialState(initialZetaFunction);
		
		// Creates the MTS with the initial state
		MTS<SynthesizedState, Symbol> mts = new MTSImpl<SynthesizedState, Symbol>(initialState);
		
		// Add the transitions
		mts.addActions(this.restrictedSymbols);
		if(this.hasTauTransitions()) {
			mts.addAction(Alphabet.TAU);	
		}
		
		unprocessedStates.add(initialState);
		
		// Add transitions for every unprocessed state
		while(!unprocessedStates.isEmpty()) {
			// gets an unprocessed state and removes it (it will be processed)
			Iterator<SynthesizedState> it = unprocessedStates.iterator();
			SynthesizedState anUnprocessedState = it.next();
			it.remove();

			unprocessedStates.addAll(this.processState(mts, anUnprocessedState));

		}
		
		return mts;
	}

	/**
	 * Process the state adding new transitions and states to the mts.
	 * The new states are returned.
	 * @param mts
	 * @param anUnprocessedState
	 * @return
	 */
	protected abstract Set<SynthesizedState> processState(MTS<SynthesizedState, Symbol> mts,
			SynthesizedState anUnprocessedState);

	protected abstract  SynthesizedState createInitialState(ZetaFunction initialZetaFunction);

	/**
	 * Returns true iff the synthesised MTS will have tau transitions. 
	 * @return
	 */
	protected abstract boolean hasTauTransitions();
	
	/**
	 * This process may create new states. The new states must be processed next, so
	 * they're collected and returned as a collection.
	 * @param mts
	 * @param state
	 * @param symbol 
	 * @return
	 */
	protected abstract Collection<SynthesizedState> addTransitions(MTS<SynthesizedState, Symbol> mts,
			SynthesizedState state, Symbol t);
	
	protected void addAsMaybeTransition(SynthesizedState state, Symbol t, SynthesizedState nextState, 
			MTS<SynthesizedState, Symbol> mts, Set<SynthesizedState> newStates) {
		this.addIfNotPresent(nextState, mts, newStates);
		mts.addTransition(state, t, nextState, TransitionType.MAYBE);
	}
	
	protected void addAsRequiredTransition(SynthesizedState state, Symbol t, SynthesizedState nextState, 
			MTS<SynthesizedState, Symbol> mts, Set<SynthesizedState> newStates) {
		this.addIfNotPresent(nextState, mts, newStates);
		mts.addTransition(state, t, nextState, TransitionType.REQUIRED);
	}

	/**
	 * Checks if the nextState is in the MTS states. If it's not present then it
	 * adds it to the MTS and to the newStates collection. If it's not a new
	 * state (it was already an MTS's state) then nothing is done
	 * 
	 * @param nextState
	 * @param mts
	 * @param newStates
	 */
	protected void addIfNotPresent(SynthesizedState nextState, MTS<SynthesizedState, Symbol> mts,
			Collection<SynthesizedState> newStates) {
		if (!(mts.getStates().contains(nextState))) { // It's a new state
			mts.addState(nextState);
			newStates.add(nextState);
		}
	}

	/**
	 * Returns true if the word satisfies the Prechart.
	 * 
	 * @param word
	 * @return
	 */
	protected boolean satisfiesPrechart(Word word) {
		return this.synthesiserHelper.satisfiesPrechart(word); 
	}
	
	protected Word next(Word word, Symbol symbol, ZetaFunction zetaFunction) {
		return this.synthesiserHelper.next(word, symbol, zetaFunction);
	}
	
	protected Set<Word> follows(Set<Word> words, Symbol symbol) {
		return WordUtils.getInstance().follows(words, symbol);
	}
	
	/**
	 * Same as follows but filtering empty words
	 * @param words
	 * @param symbol
	 * @return
	 */
	protected Set<Word> nonEmptyWordFollows(Set<Word> words, Symbol symbol) {
		Set<Word> result = this.follows(words, symbol);
		Predicate<Word> equalsEmptyWordPredicate = new EqualPredicate<Word>(Word.EMPTY_WORD);
		// The empty words are removed
		CollectionUtils.filter(result, NotPredicate.getInstance(equalsEmptyWordPredicate));
		return result;
	}
	
	protected Set<Word> startingWith(Set<Word> words, Symbol symbol) {
		return WordUtils.getInstance().startingWith(words, symbol);
	}
	
	protected Set<Symbol> firsts(Set<Word> words) {
		return WordUtils.getInstance().firsts(words);
	}
}
