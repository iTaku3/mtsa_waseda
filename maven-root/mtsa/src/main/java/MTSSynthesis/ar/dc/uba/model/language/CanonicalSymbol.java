package MTSSynthesis.ar.dc.uba.model.language;

import java.util.Set;

import org.apache.commons.collections15.SetUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A canonical symbol is a simple symbol
 * and the conditions
 * @author gsibay
 *
 */
public class CanonicalSymbol implements Symbol {

	// The simple symbol
	private Symbol symbol;
	
	// The names of the conditions
	private Set<String> conditionsNames;

	/**
	 * The symbol is epsilon. Only conditions
	 * @param conditions
	 */
	public CanonicalSymbol(Set<String> conditionsNames) {
		this.conditionsNames = SetUtils.unmodifiableSet(conditionsNames);
	}
	
	public CanonicalSymbol(Symbol symbol, Set<String> conditionsNames) {
		Validate.notNull(symbol);
		this.symbol = symbol;
		this.conditionsNames = SetUtils.unmodifiableSet(conditionsNames);
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public Set<String> getConditionsNames() {
		return conditionsNames;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Symbol = ").append(this.symbol).append("\n")
			.append("Conditions = ").append(this.conditionsNames);
		return buffer.toString();
	}
	
	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof CanonicalSymbol) {
			CanonicalSymbol canonicalSymbol = (CanonicalSymbol) anObject;

			return new EqualsBuilder().append(this.symbol, canonicalSymbol.getSymbol())
				.append(this.conditionsNames, canonicalSymbol.getConditionsNames()).isEquals();
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return new HashCodeBuilder(17,39).append(this.getSymbol()).append(this.getConditionsNames()).toHashCode();
	}	
}
