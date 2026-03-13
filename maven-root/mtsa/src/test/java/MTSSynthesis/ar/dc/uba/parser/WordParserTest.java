package MTSSynthesis.ar.dc.uba.parser;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import MTSSynthesis.ar.dc.uba.model.language.SingleSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.ar.dc.uba.model.language.Word;

/**
 * 
 * @author gsibay
 *
 */
public class WordParserTest {

	/**
	 */
	@Test
	public void testParseSingletonWord() {
		String wordStr = "a";
		
		List<Symbol> word = new LinkedList<Symbol>();
		Collections.addAll(word, new SingleSymbol("a"));
		
		assertEquals(new Word(word),this.parseWord(wordStr));
	}

	/**
	 */
	@Test
	public void testParseWord() {
		String wordStr = "a|b|turnOn|turnOff|k|t";
		
		List<Symbol> word = new LinkedList<Symbol>();
		Collections.addAll(word, new SingleSymbol("a"), new SingleSymbol("b"), new SingleSymbol("turnOn"), new SingleSymbol("turnOff"),
				new SingleSymbol("k"), new SingleSymbol("t"));
		
		assertEquals(new Word(word),this.parseWord(wordStr));
	}
	
	private Word parseWord(String word) {
		return WordParser.getInstance().parseWord(word);
	}

}
