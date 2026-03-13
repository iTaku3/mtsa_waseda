 package MTSSynthesis.ar.dc.uba.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

import MTSSynthesis.ar.dc.uba.model.condition.Condition;
import MTSSynthesis.ar.dc.uba.model.condition.ZetaFunction;
import MTSSynthesis.ar.dc.uba.model.language.Alphabet;
import MTSSynthesis.ar.dc.uba.model.language.CanonicalSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.ar.dc.uba.model.language.Word;
import MTSSynthesis.ar.dc.uba.model.lsc.TriggeredScenario;

/**
 * A significative suffix calculator.
 * Calculates the significative suffix
 * of words for a fixed Prechart.  
 * 
 * @author gsibay
 *
 */
public class SynthesiserHelper {

	private Predicate<Word> isLinearisationPrefix;
	private int qtyInteractions;
	private Set<Condition> prechartConditions;
	
	
	/**
	 * Builds the calculator from the canonical bounded linearisations.
	 * Each linearisation represents a set of potentially infinite canonical linarisation.
	 * 
	 * @param canonicalBoundedLinearisations
	 */
	public SynthesiserHelper(TriggeredScenario triggeredScenario) {
		this.qtyInteractions = triggeredScenario.getPrechart().getQtyInteractions();
		this.prechartConditions = triggeredScenario.getPrechart().getConditions(); 
		this.isLinearisationPrefix = new IsCanonicalLinearisationPrefixPredicate(triggeredScenario.getPrechart());
	}

	/**
	 * Returns the conditions holding according to the valuation (ZetaFunction)
	 * @return
	 */
	public Set<String> getConditionsHolding(ZetaFunction zetaFunction) {
		Set<Condition> conditionsHolding = new HashSet<Condition>();
		Predicate<Condition> satisfies = new SatisfiesPredicate(zetaFunction);
		
		CollectionUtils.select(this.prechartConditions, satisfies, conditionsHolding);
		
		Transformer<Condition, String> toConditionNameTransformer = new Transformer<Condition, String>() {

			@Override
			public String transform(Condition condition) {
				return condition.getName();
			}
		};
		Set<String> conditionsHoldingNames = new HashSet<String>();
		CollectionUtils.collect(conditionsHolding, toConditionNameTransformer, conditionsHoldingNames);
		return conditionsHoldingNames;
	}
	
	public Predicate<Word> getIsLinearisationPrefixPredicate() {
		return this.isLinearisationPrefix;
	}
	
	/**
	 * Calculates the significative suffix of the parameter word.
	 * It's the maximun (in lenght) suffix of the word that is a preffix of LP.LM
	 * @param word
	 * @param zetaFunction 
	 * @return 
	 */
public Word calculateSignificativeSuffix(Word word, ZetaFunction zetaFunction) {
		
		// calculate the canonical suffixes
		// every suffix has to have an epsilon with the conditions holding as first symbol
		Set<Word> suffixes = new HashSet<Word>();
		
		List<Symbol> symbols = new LinkedList<Symbol>(word.getSymbols());
		
		while(!symbols.isEmpty()) {
			// From the first symbol we get the conditions holding and then we discard it
			Set<String> conditionsHoldingBeforeSymbol = ((CanonicalSymbol)symbols.get(0)).getConditionsNames();
			symbols.remove(0);
			
			// The next suffix first symbol has the conditions holding
			CanonicalSymbol epsilonSymbol = new CanonicalSymbol(conditionsHoldingBeforeSymbol);
			List<Symbol> suffix =  new LinkedList<Symbol>();
			suffix.add(epsilonSymbol);
			suffix.addAll(symbols);
			
			suffixes.add(new Word(suffix));
		}
		
		Collection<Word> sufixesThatAreLinearisationPrefixes = CollectionUtils.select(suffixes, this.isLinearisationPrefix);
		
		if(sufixesThatAreLinearisationPrefixes.isEmpty()) {
			// there is no suffixes that is also a prefix of some linearisation. 
			// return an epsilon symbol with all the conditions holding with the current zeta function
			Symbol symbol = new CanonicalSymbol(this.getConditionsHolding(zetaFunction));
			return new Word(Collections.singletonList(symbol));
		} else {
			// select the suffixes that also are prefixes and then select the maximum.
			return Collections.max(sufixesThatAreLinearisationPrefixes, Word.WORD_LENGTH_COMPARATOR);
		}
	}
	
	/**
	 * Returns a new word that is the next(word, symbol).
	 * If the symbol is Tau it returns a new word equals to the parameter.
	 * The zetaFunction is updated reflecting the occurrence of newSymbol
	 * @param word
	 * @param newSymbol
	 * @return
	 */
	public Word next(Word word, Symbol newSymbol, ZetaFunction zetaFunction) {
		if(Alphabet.TAU.equals(newSymbol)) {
			return new Word(word.getSymbols());
		} else {
			// each canonical word starts with an epsilon canonical symbol
			
			// The valuation zetaFunction changes with the symbol
			zetaFunction.apply(newSymbol);
			
			// Get the canonical representation of the symbol for the new valuation (zetaFunction)
			CanonicalSymbol newCanonicalSymbol = new CanonicalSymbol(newSymbol, this.getConditionsHolding(zetaFunction));
			
			List<Symbol> wordAsSymbols = new LinkedList<Symbol>(word.getSymbols());
			wordAsSymbols.add(newCanonicalSymbol);
			
			Word wordWithNewSymbol = new Word(wordAsSymbols);
			
			return this.calculateSignificativeSuffix(wordWithNewSymbol, zetaFunction);
		}
	}

	
	public boolean satisfiesPrechart(Word canonicalWord) {
		// must be a prefix of the Prechart
		boolean isPrefix = this.isLinearisationPrefix.evaluate(canonicalWord);
		
		// the word must have all the interactions in the diagram
		// A canonical word has one 
		// <epsilon,conditions> as initial symbol and then one for each interaction
		// That's why a full lenght Prechart word will
		// have a lenght of ( this.qtyInteractions ) + 1
		boolean isSizeOfPrechartWord = canonicalWord.length() ==
			( this.qtyInteractions + 1); 
		
		return isPrefix && isSizeOfPrechartWord;
	}
	
	private WordUtils getWordUtils() {
		return WordUtils.getInstance();
	}
	
	private class SatisfiesPredicate implements Predicate<Condition> {

		private ZetaFunction zetaFunction;
		
		public SatisfiesPredicate(ZetaFunction zetaFunction) {
			this.zetaFunction = zetaFunction;
		}
		
		@Override
		public boolean evaluate(Condition condition) {
			return condition.getFormula().evaluate(this.zetaFunction);
		}
		
	}
}
