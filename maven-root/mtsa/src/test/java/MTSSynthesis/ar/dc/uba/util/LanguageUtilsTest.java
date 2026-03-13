package MTSSynthesis.ar.dc.uba.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import MTSSynthesis.ar.dc.uba.model.language.Language;
import MTSSynthesis.ar.dc.uba.parser.LanguageParser;

public class LanguageUtilsTest {

	/**
	 */
	@Test
	public void testConcatenateLanguages() {
		Language l1 = this.parseLanguage("{a|b|c,b|c|d,d|e|f}");
		Language l2 = this.parseLanguage("{z|w|y,r|s|t}");
		
		Language l1OriginalCopy = this.parseLanguage("{a|b|c,b|c|d,d|e|f}");
		Language l2OriginalCopy = this.parseLanguage("{z|w|y,r|s|t}");
		
		// The original language and the copy are the same
		assertEquals(l1, l1OriginalCopy);
		assertEquals(l2, l2OriginalCopy);
		
		// The desired result
		Language desiredResult = this
				.parseLanguage("{d|e|f|z|w|y,d|e|f|r|s|t,b|c|d|z|w|y,b|c|d|r|s|t,a|b|c|r|s|t,a|b|c|z|w|y}");

		assertEquals(this.getLanguageUtils().concatenateLanguages(l1, l2), desiredResult);

		// The original language remains unmodified after the operation
		assertEquals("the original was modified!",l1, l1OriginalCopy);
		assertEquals("the original was modified!",l2, l2OriginalCopy);
	}
	
	private LanguageUtils getLanguageUtils() {
		return LanguageUtils.getInstance();
	}
	
	private Language parseLanguage(String language) {
		return LanguageParser.getInstance().parseLanguage(language);
	}
}
