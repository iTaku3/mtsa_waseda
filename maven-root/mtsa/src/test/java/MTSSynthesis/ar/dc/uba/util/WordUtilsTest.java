package MTSSynthesis.ar.dc.uba.util;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import MTSSynthesis.ar.dc.uba.model.language.SingleSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.ar.dc.uba.model.language.Word;
import MTSSynthesis.ar.dc.uba.parser.WordParser;

/**
 * 
 * @author gsibay
 *
 */
public class WordUtilsTest {

	/**
	 */
	@Test
	public void testSuffixes() {
		String wordStr = "turnOn|turnOff|c";
		Word originalWord = this.parseWord(wordStr);
		
		// The desired result
		Set<Word> desiredResult = new HashSet<Word>();
		Collections.addAll(desiredResult, this.parseWord(""),
				this.parseWord("c"), this.parseWord("turnOff|c"), this
				.parseWord("turnOn|turnOff|c"));
		
		assertEquals(this.getWordUtils().suffixes(originalWord), desiredResult);
		
		// The original word remains unmodified
		assertEquals("the original was modified!",originalWord,
				this.parseWord(wordStr));
	}
	
	/**
	 */
	@Test
	public void testPreffixes() {
		String wordStr = "goUp|move|gogo|rico";
		Word originalWord = this.parseWord(wordStr);
		
		// The desired result
		Set<Word> desiredResult = new HashSet<Word>();
		Collections.addAll(desiredResult, this.parseWord(""),
				this.parseWord("goUp"), this.parseWord("goUp|move"), this
				.parseWord("goUp|move|gogo"), this.parseWord("goUp|move|gogo|rico"));
		
		assertEquals(this.getWordUtils().preffixes(originalWord), desiredResult);
		
		// The original word remains unmodified
		assertEquals("the original was modified!",originalWord,
				this.parseWord(wordStr));
	}
	
	/**
	 */
	@Test
	public void testFirsts() {
		Set<Word> originalWords = new HashSet<Word>();
		Collections.addAll(originalWords, this.parseWord("goUp|goDown"), this.parseWord("c|a|b"), this.parseWord("turnOff"),
				this.parseWord("goUp|k|li"));

		Set<Word> copyOfOriginal = new HashSet<Word>();
		Collections.addAll(copyOfOriginal, this.parseWord("goUp|goDown"), this.parseWord("c|a|b"), this.parseWord("turnOff"),
				this.parseWord("goUp|k|li"));
		
		// The original word and the copy are the same
		assertEquals(originalWords, copyOfOriginal);
		
		// The desired result
		Set<Symbol> desiredResult = new HashSet<Symbol>();
		Collections.addAll(desiredResult, new SingleSymbol("c"), new SingleSymbol("goUp"), new SingleSymbol("turnOff"));
		
		assertEquals(this.getWordUtils().firsts(originalWords), desiredResult);
		
		// The original word remains unmodified after the operation
		assertEquals("the original was modified!",originalWords, copyOfOriginal);
	}
	
	/**
	 */
	@Test
	public void testFollows() {
		Set<Word> originalWords = new HashSet<Word>();
		Collections.addAll(originalWords, this.parseWord("goUp|goDown"), this.parseWord("c|a|b"), this
				.parseWord("turnOff"), this.parseWord("goUp|k|li"), this.parseWord("goUp"));

		Set<Word> copyOfOriginal = new HashSet<Word>();
		Collections.addAll(copyOfOriginal, this.parseWord("goUp|goDown"), this.parseWord("c|a|b"), this
				.parseWord("turnOff"), this.parseWord("goUp|k|li"), this.parseWord("goUp"));

		// The original word and the copy are the same
		assertEquals(originalWords, copyOfOriginal);

		// The desired result
		Set<Word> desiredResult = new HashSet<Word>();
		Collections.addAll(desiredResult, this.parseWord("goDown"), this.parseWord("k|li"), this.parseWord(""));

		assertEquals(this.getWordUtils().follows(originalWords, new SingleSymbol("goUp")), desiredResult);

		// The original word remains unmodified after the operation
		assertEquals("the original was modified!",originalWords, copyOfOriginal);
	}
	
	private Word parseWord(String word) {
		return WordParser.getInstance().parseWord(word);
	}
	
	private WordUtils getWordUtils() {
		return WordUtils.getInstance();
	}
}
