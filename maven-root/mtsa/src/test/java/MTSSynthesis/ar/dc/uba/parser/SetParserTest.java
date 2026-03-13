package MTSSynthesis.ar.dc.uba.parser;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * 
 * @author gsibay
 *
 */
public class SetParserTest {

	/**
	 */
	@Test
	public void testParseEmptySet() {
		String set = "{}";
		assertEquals(new HashSet(),this.parseSet(set));
	}

	/**
	 */
	@Test
	public void testParseSingletonSet() {
		String setStr = "{a}";
		assertEquals(Collections.singleton("a"),this.parseSet(setStr));
	}

	/**
	 */
	@Test
	public void testParseSet() {
		String setStr = "{a,gg,hd,f}";
		Set<String> set = new HashSet<String>();
		Collections.addAll(set, "a", "gg", "hd", "f");
		assertEquals(set,this.parseSet(setStr));
	}
	
	/**
	 * 
	 */
	@Test
	public void testParseMultipleElementsSet() {
		String setStr = "{a,b,a,a,b,c}";
		Set<String> set = new HashSet<String>();
		Collections.addAll(set, "a", "b", "c");
		assertEquals(set,this.parseSet(setStr));
	}
	
	private Set parseSet(String set) {
		return SetParser.getInstance().parse(set);
	}
}
