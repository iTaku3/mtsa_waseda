package MTSSynthesis.ar.dc.uba.parser;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import MTSSynthesis.ar.dc.uba.model.language.*;

/**
 * 
 * @author gsibay
 *
 */
public class LanguageParserTest {
	
	/**
	 */
	@Test
	public void testParseSingletonLanguageSingletonWord() {
		String languageStr = "{a}";
		
		Set<Word> words = new HashSet<Word>();
		Collections.addAll(words, this.parseWord("a"));
		
		assertEquals(new Language(words),this.parseLanguage(languageStr));
	}

	/**
	 */
	@Test
	public void testParseSingletonLanguageNonSingletonWord() {
		String languageStr = "{a|turnOn|turnOff}";
		
		Set<Word> words = new HashSet<Word>();
		Collections.addAll(words, this.parseWord("a|turnOn|turnOff"));
		
		assertEquals(new Language(words),this.parseLanguage(languageStr));
	}

	/**
	 */
	@Test
	public void testParseLanguageSingletonWord() {
		String languageStr = "{a,turnOff,turnOn,f, goUp}";
		
		Set<Word> words = new HashSet<Word>();
		Collections.addAll(words, this.parseWord("a"), this.parseWord("turnOff"), this.parseWord("turnOn"), this
				.parseWord("f"), this.parseWord("goUp"));
		
		assertEquals(new Language(words),this.parseLanguage(languageStr));
	}

	/**
	 */
	@Test
	public void testParseLanguageNonSingletonWord() {
		String languageStr = "{a|turnOff|turnOn,f|goUp|turnOn,turnOff|turnOn|a}";
		
		Set<Word> words = new HashSet<Word>();
		Collections.addAll(words, this.parseWord("a|turnOff|turnOn"), this.parseWord("f|goUp|turnOn"), this
				.parseWord("turnOff|turnOn|a"));
		
		assertEquals(new Language(words),this.parseLanguage(languageStr));
	}
	
	private Language parseLanguage(String language) {
		return LanguageParser.getInstance().parseLanguage(language);
	}

	private Word parseWord(String word) {
		return WordParser.getInstance().parseWord(word);
	}

}
