package MTSSynthesis.ar.dc.uba.model.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import MTSSynthesis.ar.dc.uba.parser.WordParser;

/**
 * 
 * @author gsibay
 *
 */
public class WordTest {

	/**
	 */
	@Test
	public void testNotEmptyWord() {
		String wordStr = "a|b|turnOn|turnOff|k|t";
				
		assertFalse(this.parseWord(wordStr).isEmptyWord());
	}
	
	/**
	 */
	@Test
	public void testEmptyWord() {
		String wordStr = "";
				
		assertTrue(this.parseWord(wordStr).isEmptyWord());
	}
	
	/**
	 */
	@Test
	public void testFirst() {
		String wordStr = "turnOn|turnOff|c";
				
		assertEquals(this.parseWord(wordStr).getFirst(), new SingleSymbol("turnOn"));
	}

	/**
	 */
	@Test
	public void testStartsWithTrue() {
		String wordStr = "turnOn|turnOff|c";
				
		assertTrue(this.parseWord(wordStr).startsWith(new SingleSymbol("turnOn")));
	}
	
	/**
	 */
	@Test
	public void testStartsWithFalse() {
		String wordStr = "turnOn|turnOff|c";
		
		assertFalse(this.parseWord(wordStr).startsWith(new SingleSymbol("turnOff")));
	}
	
	/**
	 */
	@Test
	public void testStartsWithTrueSigletonWord() {
		String wordStr = "turnOn";
				
		assertTrue(this.parseWord(wordStr).startsWith(new SingleSymbol("turnOn")));
	}
	
	/**
	 */
	@Test
	public void testStartsWithFalseSingletonWord() {
		String wordStr = "turnOn";
		
		assertFalse(this.parseWord(wordStr).startsWith(new SingleSymbol("turnOff")));
	}
	
	/**
	 */
	@Test
	public void testStartsWithEmptyWord() {
		String wordStr = "";
		
		assertFalse(this.parseWord(wordStr).startsWith(new SingleSymbol("turnOff")));
	}
	
	/**
	 */
	@Test
	public void testTailWord() {
		String wordStr = "turnOn|turnOff|c";
		Word originalWord = this.parseWord(wordStr);
		
		assertEquals(originalWord.getTailWord(), this.parseWord("turnOff|c"));
		
		// The original word remains unmodified
		assertEquals("the original word was modified!",originalWord, this.parseWord(wordStr));
	}
	
	/**
	 */
	@Test
	public void testReverseEmptyWord() {
		String wordStr = "";
		Word originalWord = this.parseWord(wordStr);
		
		assertEquals(originalWord.getReverseWord(), this.parseWord(""));
		
		// The original word remains unmodified
		assertEquals("the original word was modified!",originalWord, this.parseWord(wordStr));
	}

	/**
	 */
	@Test
	public void testReverseNonEmptyWord() {
		String wordStr = "turnOn|turnOff|c";
		Word originalWord = this.parseWord(wordStr);
		
		assertEquals(originalWord.getReverseWord(), this.parseWord("c|turnOff|turnOn"));
		
		// The original word remains unmodified
		assertEquals("the original word was modified!", originalWord, this.parseWord(wordStr));
	}


	/**
	 */
	@Test
	public void testWordLengthComparator() {
		Word ceroSymbolsWord = this.parseWord("");
		Word twoSymbolsWord = this.parseWord("turnOn|turnOff");
		Word anotherTwoSymbolsWord = this.parseWord("turnOff|goUp");
		Word oneSymbolsWord = this.parseWord("abaco");
		Word fiveSymbolsWord = this.parseWord("a|b|c|d|e");
		
		assertTrue(Word.WORD_LENGTH_COMPARATOR.compare(ceroSymbolsWord, twoSymbolsWord) < 0);
		assertTrue(Word.WORD_LENGTH_COMPARATOR.compare(ceroSymbolsWord, fiveSymbolsWord) < 0);
		assertTrue(Word.WORD_LENGTH_COMPARATOR.compare(twoSymbolsWord, anotherTwoSymbolsWord) == 0);
		assertTrue(Word.WORD_LENGTH_COMPARATOR.compare(twoSymbolsWord, oneSymbolsWord) > 0);
		
		List<Word> sortedByLength = new LinkedList<Word>();
		Collections.addAll(sortedByLength, ceroSymbolsWord, oneSymbolsWord, twoSymbolsWord, fiveSymbolsWord);
		
		List<Word> toSort = new LinkedList<Word>();
		Collections.addAll(toSort, oneSymbolsWord, fiveSymbolsWord, twoSymbolsWord, ceroSymbolsWord);
		
		Collections.sort(toSort, Word.WORD_LENGTH_COMPARATOR);
		
		assertEquals(toSort, sortedByLength);
	}

	private Word parseWord(String word) {
		return WordParser.getInstance().parseWord(word);
	}
}
