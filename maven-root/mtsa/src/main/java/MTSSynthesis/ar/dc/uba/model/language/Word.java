package MTSSynthesis.ar.dc.uba.model.language;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A word is a sequence of Symbol
 * @author gsibay
 *
 */
public class Word {
	
	public static Comparator<Word> WORD_LENGTH_COMPARATOR = new Word.WordLenghtComparator();
	
	/**
	 * Transforms a Word to the set of its Symbols
	 */
	public static Transformer<Word, List<Symbol>> TO_SYMBOLS_TRANSFORMER = new Word.WordToSymbolsTransformer();
	
	@SuppressWarnings("unchecked")
	public static Word EMPTY_WORD = new Word(Collections.EMPTY_LIST);
	
	private List<Symbol> symbols = new LinkedList<Symbol>();
	
	public Word(List<Symbol> symbols) {
		//TODO: should be unmodifiable
		this.symbols.addAll(symbols);
	}
	
	/**
	 * Get't the Word's first symbol
	 * @return
	 */
	public Symbol getFirst() {
		return this.symbols.get(0);
	}
	
	public Symbol getLast() {
		return this.symbols.get(this.symbols.size() - 1);
	}
	
	/**
	 * Returns a new Word that is 
	 * equals to this one without the
	 * first Symbol.
	 * 
	 * @return
	 */
	public Word getTailWord() {
		Validate.isTrue(!this.isEmptyWord());
		
		// To gain a little in performance, the 
		// internal sybols are used to create the new word.
		// The new word creates a new symbol set anyway.
		
		// Save the first symbol
		Symbol firstSymbol = this.symbols.get(0);
		
		// remove the first symbol 
		this.symbols.remove(0);
		
		// creates a new word with the symbols but without the first one
		Word follow = new Word(this.symbols);
		
		// put back the first symbol at the begining
		this.symbols.add(0, firstSymbol);
		return follow;
	}
	
	/**
	 * Returns true if the word starts with the symbol.
	 * If the word is empty returns false (there is no starting symbol)
	 * @param symbol
	 * @return
	 */
	public boolean startsWith(Symbol symbol) {
		if(this.isEmptyWord()) {
			return false;
		} else {
			return this.getFirst().equals(symbol);
		}
	}
	
	/**
	 * Returns a new word that is the reverse of this word
	 * @return
	 */
	public Word getReverseWord() {
		LinkedList<Symbol> reversedSymbols = new LinkedList<Symbol>();
		for (Symbol symbol : this.symbols) {
			//adds last to reverse symbols
			reversedSymbols.addFirst(symbol);
		}
		return new Word(reversedSymbols);
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Symbol symbol : this.symbols) {
			
			buffer.append(symbol).append("|");
		}
		return StringUtils.chomp(buffer.toString(),"|");
	}
	
	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof Word) {
			Word aWord = (Word) anObject;
			return this.getSymbols().equals(aWord.getSymbols());
		} else {
			return false;
		}
	}
	
	public List<Symbol> getSymbols() {
		return Collections.unmodifiableList(this.symbols);
	}
	
	public int hashCode() {
		return new HashCodeBuilder(17,39).append(this.symbols).toHashCode();
	}
	
	/**
	 * Returns the lenght. That is the number of symbols
	 * @return
	 */
	public int length() {
		return this.symbols.size();
	}
	
	/**
	 * Returns true if this is the empty word
	 * @return
	 */
	public boolean isEmptyWord() {
		return this.getSymbols().isEmpty();
	}
	
	private static class WordLenghtComparator implements Comparator<Word> {

		public int compare(Word word1, Word word2) {
			return word1.length() - word2.length();
		}
		
	}
	
	private static class WordToSymbolsTransformer implements Transformer<Word, List<Symbol>> {

		@Override
		public List<Symbol> transform(Word word) {
			return word.getSymbols();
		}
		
	}
}
