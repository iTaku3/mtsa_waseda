package MTSSynthesis.ar.dc.uba.model.language;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.HashCodeBuilder;

import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;

/**
 * An alphabet of symbols
 * @author gsibay
 *
 */
public class Alphabet {

	private Set<Symbol> symbols = new HashSet<Symbol>();
	public static SingleSymbol TAU = new SingleSymbol(MTSConstants.TAU);
	
	public Alphabet(Set<Symbol> symbols) {
		Validate.notEmpty(symbols);
		Validate.isTrue(!symbols.contains(Alphabet.TAU), TAU.toString() + " is a reserved symbols. You should not use it");
		this.symbols.addAll(symbols);
		this.symbols = Collections.unmodifiableSet(this.symbols);
	}
	
	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof Alphabet) {
			Alphabet anAlphabet = (Alphabet) anObject;
			return this.getSymbols().equals(anAlphabet.getSymbols());
		} else {
			return false;
		}
	}
	
	public Set<Symbol> getSymbols() {
		return this.symbols; 
	}
	
	public int hashCode() {
		return new HashCodeBuilder(19,37).append(this.getSymbols()).toHashCode();
	}
	
	public String toString(){
		return this.getSymbols().toString();
	}
}
