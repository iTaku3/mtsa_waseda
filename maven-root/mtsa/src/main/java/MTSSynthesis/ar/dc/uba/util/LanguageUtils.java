package MTSSynthesis.ar.dc.uba.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import MTSSynthesis.ar.dc.uba.model.language.Language;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.ar.dc.uba.model.language.Word;

/**
 * Utility functions for Languages
 * @author gsibay
 *
 */
public class LanguageUtils {

	private static LanguageUtils instance = new LanguageUtils();
	
	public static LanguageUtils getInstance() {
		return instance;
	}
	
	/**
	 * Concatenates two languages and generates a new one where
	 * wz is in the result iff w is in l1 and z is in l2
	 * @param l1
	 * @param l2
	 */
	public Language concatenateLanguages(Language l1, Language l2) {
		Set<Word> languageWords = new HashSet<Word>();
		
		Set<Word> wordsL1 = l1.getWords();
		Set<Word> wordsL2 = l2.getWords();
		
		List<Symbol> newWordSymbols;
		for (Word wordL1 : wordsL1) {
			for (Word wordL2 : wordsL2) {
				// The new word has the symbols of the first word, and then ths symbols of the second one.
				newWordSymbols = new LinkedList<Symbol>(wordL1.getSymbols());
				newWordSymbols.addAll(wordL2.getSymbols());
				
				// creates the word with the concatenated symbols and adds it to the language's words
				languageWords.add(new Word(newWordSymbols));
			}
		}
		return new Language(languageWords);
	}
}
