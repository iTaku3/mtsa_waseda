package MTSSynthesis.ar.dc.uba.parser;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;


/**
 * Parses a String representation of a Set of String.
 * 
 * The String representation has the Strings separated by comma and the set is
 * delimited by {}. White spaces are stripped.
 * For example:
 *  
 * {lightOff, engineOn,lightOn}
 * or
 * {a,b,c}
 * or 
 * {a|b|c,   d|c}
 * 
 * @author gsibay
 *
 */
public class SetParser {

	private static SetParser instance = new SetParser();
	
	public static SetParser getInstance() {
		return instance;
	}
	
	/**
	 * Parses the string to a Set of String.
	 * @param set
	 * @return
	 */
	public Set<String> parse(String set){
		Set<String> elementSet = new HashSet<String>();

		// Get's the elements
		String setElements = StringUtils.substringBetween(set, "{", "}");
		
		// The set's elements must be separated by #
		StringTokenizer tokenizer = new StringTokenizer(setElements, ",");
		
		String element;
		while (tokenizer.hasMoreTokens()) {
			// The next token is a symbol.
			element = StringUtils.strip(tokenizer.nextToken());
			Validate.notEmpty(element);
			elementSet.add(element);
		}
		return elementSet;
	}
	
}
