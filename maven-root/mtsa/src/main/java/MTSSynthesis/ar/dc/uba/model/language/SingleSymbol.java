package MTSSynthesis.ar.dc.uba.model.language;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A Symbol
 * @author gsibay
 *
 */
public class SingleSymbol implements Symbol {

	public String strSymbol;
	
	public SingleSymbol(String strSymbol) {
		this.strSymbol = strSymbol;
	}
	
	public String toString() {
		return this.strSymbol;
	}

	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof SingleSymbol) {
			return this.toString().equals(anObject.toString());
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return new HashCodeBuilder(17,39).append(this.toString()).toHashCode();
	}
}
