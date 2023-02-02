package MTSATests.controller;

import java.util.HashSet;
import java.util.Set;

import MTSSynthesis.ar.dc.uba.model.language.SingleSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;

public class ControllerTestsUtils {
	
	public static Set<Symbol> buildSymbolSetFrom(String[] strings) {
		Set<Symbol> result =  new HashSet<Symbol>();
		for (int i = 0; i < strings.length; i++) {
			result.add(new SingleSymbol(strings[i]));
		}
		return result;
	}
	
	public static Set<String> buildStringsSetFrom(String[] strings) {
		Set<String> result = new HashSet<String>();
		for (int i = 0; i < strings.length; i++) {
			result .add(strings[i]);
		}
		return result;
	}


}
