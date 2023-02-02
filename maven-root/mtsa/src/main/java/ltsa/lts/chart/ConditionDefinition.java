package ltsa.lts.chart;

import ltsa.lts.ltl.FormulaSyntax;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * ConditionDefinition
 * The conditions are defined at TriggeredScenario level and
 * can be used in the Prechart.
 * @author gsibay
 *
 */
public class ConditionDefinition {

	// Fluent Propositional Logic Formula
	private FormulaSyntax fplFormula;
	
	private String name;
	
	public ConditionDefinition(String name, FormulaSyntax formula) {
		this.setName(name);
		this.setFplFormula(formula);
	}

	public String getName() {
		return name;
	}
	
	public FormulaSyntax getFplFormula() {
		return fplFormula;
	}

	private void setFplFormula(FormulaSyntax fplFormula) {
		this.fplFormula = fplFormula;
	}

	private void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name + " = " + this.getFplFormula().toString();
	}
	
	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof ConditionDefinition) {
			ConditionDefinition conditionDefinition = (ConditionDefinition) anObject;
			return this.getName().equals(conditionDefinition.getName());
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return new HashCodeBuilder(19,37).append(this.getName()).toHashCode();
	}
}
