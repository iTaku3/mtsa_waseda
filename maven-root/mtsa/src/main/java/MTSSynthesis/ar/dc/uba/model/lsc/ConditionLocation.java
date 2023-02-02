package MTSSynthesis.ar.dc.uba.model.lsc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import MTSSynthesis.ar.dc.uba.model.condition.Formula;


/**
 * 
 * @author gsibay
 *
 */
public class ConditionLocation implements Location {

	private String conditionName;
	private Set<String> instances;
	private Formula formula;
	
	public ConditionLocation(String conditionName, Set<String> instances, Formula formula) {
		Validate.notEmpty(conditionName);
		Validate.notEmpty(instances);
		Validate.notNull(formula);
		this.conditionName = conditionName;
		this.instances = Collections.unmodifiableSet(new HashSet<String>(instances));
		this.formula = formula;
	}

	public String toString() {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(this.getConditionName())
				.append(this.getInstances());
		return sBuffer.toString();
	}
	
	public String getConditionName() {
		return conditionName;
	}

	public Set<String> getInstances() {
		return instances;
	}

	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof ConditionLocation) {
			ConditionLocation conditionLocation = (ConditionLocation) anObject;
			return new EqualsBuilder().append(this.getConditionName(),
					conditionLocation.getConditionName()).isEquals();
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return new HashCodeBuilder(13, 33).append(this.getConditionName()).toHashCode();
	}

	@Override
	public String getName(LocationNamingStrategy locationNamingStrategy) {
		return locationNamingStrategy.calculateName(this);
	}

	public Formula getFormula() {
		return formula;
	}
}
