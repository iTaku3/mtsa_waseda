package MTSSynthesis.ar.dc.uba.model.condition;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * 
 * @author gsibay
 *
 */
public class ConditionImpl implements Condition {

	private String name;
	private Formula formula;

	public ConditionImpl(String name, Formula formula) {
		Validate.notNull(formula);
		Validate.notNull(name);
		this.formula = formula;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Formula getFormula() {
		return formula;
	}
	
	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public String toString() {
		return this.getName() + " : " + this.getFormula().toString(); 
	}
	
	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof ConditionImpl) {
			ConditionImpl condition = (ConditionImpl) anObject;
			return this.getName().equals(condition.getName());
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return new HashCodeBuilder(19,37).append(this.getName()).toHashCode();
	}
}
