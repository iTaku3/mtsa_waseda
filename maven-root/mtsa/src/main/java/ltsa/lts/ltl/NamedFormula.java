package ltsa.lts.ltl;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * This class represents a named Formula. 
 * 
 * @author dipi
 *
 */
public class NamedFormula {

	private FormulaSyntax ltlFormula;
	
	private String name;
	
	public NamedFormula(String name, FormulaSyntax formula) {
		this.setName(name);
		this.setFormula(formula);
	}

	public String getName() {
		return name;
	}
	
	public FormulaSyntax getFormula() {
		return ltlFormula;
	}

	private void setFormula(FormulaSyntax formula) {
		this.ltlFormula = formula;
	}

	private void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name + " = " + this.getFormula().toString();
	}
	
	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof NamedFormula) {
			NamedFormula namedFormula = (NamedFormula) anObject;
			return this.getName().equals(namedFormula.getName());
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return new HashCodeBuilder(89, 97).append(this.getName()).toHashCode();
	}
}
