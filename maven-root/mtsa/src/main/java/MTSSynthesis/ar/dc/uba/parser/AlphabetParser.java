package MTSSynthesis.ar.dc.uba.parser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import MTSSynthesis.ar.dc.uba.model.language.Alphabet;
import MTSSynthesis.ar.dc.uba.model.language.SingleSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;

/**
 * Parses an Alphabet from a String. The alphabet is a 
 * set of string representing Symbols (does not works
 * with conditions).
 * example:
 * {turnOn turnOff goUp goDown}
 * or
 * {a b c f}
 * @author gsibay
 *
 */
public class AlphabetParser {

	private static AlphabetParser instance = new AlphabetParser();
	
	public static AlphabetParser getInstance() {
		return instance;
	}
	
	public Alphabet parseAlphabet(String alphabetStr){
		Set<Symbol> symbols = new HashSet<Symbol>();
		
		// Parse the set to get the set of words (the words are strings)
		Set<String> symbolsStr = SetParser.getInstance().parse(alphabetStr);
		
		// Create each symbol from the string
		Iterator<String> symbolsStrIt = symbolsStr.iterator();
		while (symbolsStrIt.hasNext()) {
			String symbolStr = (String) symbolsStrIt.next();
			// create and add the symbol to the symbols's set
			symbols.add(new SingleSymbol(symbolStr));
		}
		
		return new Alphabet(symbols);
	}

}
