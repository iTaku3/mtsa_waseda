package MTSSynthesis.ar.dc.uba.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.ar.dc.uba.model.language.Word;


/**
 * 
 * Utility functions for Word
 * @author gsibay
 *
 */
public class WordUtils {

	private static WordUtils instance = new WordUtils();
	
	public static WordUtils getInstance() {
		return instance;
	}
	
	/**
	 * Return the suffixes of a Word.
	 * The empty word and the word itself are suffixes.
	 * @param word
	 * @return
	 */
	public Set<Word> suffixes(Word word) {
		Set<Word> suffixes = new HashSet<Word>();
		
		// The word is a suffix.
		Word currentSuffix = word;
		suffixes.add(currentSuffix);
		while(!currentSuffix.isEmptyWord()) { // There are more suffixes
			currentSuffix = currentSuffix.getTailWord();
			suffixes.add(currentSuffix);
		}
		return suffixes;
	}
	
	/**
	 * Return the preffixes of a Word.
	 * The empty word and the word itself are preffixes.
	 * @param word
	 * @return
	 */
	public Set<Word> preffixes(Word word) {
		return this.getReverses(this.suffixes(
				word.getReverseWord()));
	}
	
	/**
	 * Get's the reverse for each word in the set
	 * @return
	 */
	public Set<Word> getReverses(Set<Word> words) {
		Set<Word> result = new HashSet<Word>();
		for (Word word : words) {
			result.add(word.getReverseWord());
		}
		return result;
	}
	
	/**
	 * Get's the preffixes for every word in the set
	 * @param words
	 * @return
	 */
	public Set<Word> preffixes(Set<Word> words) {
		Set<Word> result = new HashSet<Word>();
		for (Word word : words) {
			result.addAll(this.preffixes(word));
		}
		return result;
	}
	
	/**
	 * Returns the first symbols of the elements of the set of
	 * words.
	 * The words can not be empties
	 * @return
	 */
	public Set<Symbol> firsts(Set<Word> words) {
		Set<Symbol> firstsSymbol = new HashSet<Symbol>();
		for (Word word : words) {
			firstsSymbol.add(word.getFirst());
		}
		return firstsSymbol;
	}
	
	/**
	 * Returns the set of Word following the start symbol
	 * The original set is not modified (neither the set's words)
	 * @param words
	 * @param startingSymbol
	 * @return
	 */
	public Set<Word> follows(Set<Word> words, Symbol startingSymbol) {
		Set<Word> follows = new HashSet<Word>();
		for (Word word : words) {
			if(word.startsWith(startingSymbol)) {
				follows.add(word.getTailWord());
			}
		}
		return follows;
	}
	
	/**
	 * Returns the set of Word starting with the parameter symbol
	 * @param words
	 * @param startingSymbol
	 * @return
	 */
	public Set<Word> startingWith(Set<Word> words, Symbol startingSymbol) {
		Set<Word> result = new HashSet<Word>();
		for (Word word : words) {
			if(word.startsWith(startingSymbol)) {
				result.add(word);
			}
		}
		return result;
	}
	
	/**
	 * Creates a new Word with the symbol appended to the parameter word
	 * 
	 * @param word
	 * @param symbolToAppend
	 * @return
	 */
	public Word getAppendedSymbolWord(Word word, Symbol symbolToAppend) {
		List<Symbol> newWordSymbols = new LinkedList<Symbol>(word.getSymbols());
		newWordSymbols.add(symbolToAppend);
		return new Word(newWordSymbols);
	}
	
	/**
	 * When using word algorithms for obtaining prefixes, suffixes, etc, 
	 * a result may be an invalid canonical word. Usualy this is
	 * avoided by selecting the words with odd size.
	 * @param input
	 * @param output
	 */
	public void selectOddSizedWords(Iterable<Word> input, Collection<Word> output) {
		Predicate<Word> oddWordSizePredicate = new Predicate<Word>() {

			@Override
			public boolean evaluate(Word word) {
				return (word.length() % 2) != 0;
			}
			
		};

		CollectionUtils.select(input, oddWordSizePredicate, output);
	}
	
}
