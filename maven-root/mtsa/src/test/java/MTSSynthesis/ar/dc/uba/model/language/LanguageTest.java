package MTSSynthesis.ar.dc.uba.model.language;

import static org.junit.Assert.assertEquals;

import MTSSynthesis.ar.dc.uba.parser.AlphabetParser;
import MTSSynthesis.ar.dc.uba.parser.LanguageParser;
import org.junit.Test;

public class LanguageTest {

  /** */
  @Test
  public void testGetAlphabet() {
    String languageStr = "{a|turnOff|turnOn,f|goUp|turnOn,turnOff|turnOn|a}";
    String alphabetStr = "{goUp,turnOff,a,turnOn,f}";

    assertEquals(
        (LanguageParser.getInstance().parseLanguage(languageStr)).getAlphabet(),
        AlphabetParser.getInstance().parseAlphabet(alphabetStr));
  }
}
