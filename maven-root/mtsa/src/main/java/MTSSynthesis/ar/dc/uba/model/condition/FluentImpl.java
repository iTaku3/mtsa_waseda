package MTSSynthesis.ar.dc.uba.model.condition;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.HashCodeBuilder;

import MTSSynthesis.ar.dc.uba.model.language.Symbol;

/**
 * Fluent default implementation
 * @author gsibay
 *
 */
public class FluentImpl implements Fluent {

	private String name;
	private Set<Symbol> initiatingActions;
	private Set<Symbol> terminatingActions;
	private boolean initialValue;

	public FluentImpl(String name, Set<Symbol> initiatingActions,
			Set<Symbol> terminatingActions, boolean initialValue) {
		this.name = name;
		this.initiatingActions = initiatingActions;
		this.terminatingActions = terminatingActions;
		this.initialValue = initialValue;
	}
	
	public FluentImpl(String name, Symbol initiatingAction, Symbol terminatingAction, boolean initialValue) {
		this.name = name;
		this.initialValue = initialValue;
		this.initiatingActions = new HashSet<Symbol>();
		this.terminatingActions = new HashSet<Symbol>();
		this.initiatingActions.add(initiatingAction);
		this.terminatingActions.add(terminatingAction);
	}

	@Override
	public Set<Symbol> getInitiatingActions() {
		return this.initiatingActions;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Set<Symbol> getTerminatingActions() {
		return this.terminatingActions;
	}

	@Override
	public boolean isInitialValue() {
		return this.initialValue;
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(this.getName()).append(" = <").
			append(this.getInitiatingActions()).append(" , ").
				append(this.getTerminatingActions()).append(" > initially ").append(this.isInitialValue());
		return result.toString();
	}
	
	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof FluentImpl) {
			FluentImpl fluent = (FluentImpl) anObject;
			return this.getName().equals(fluent.getName());
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return new HashCodeBuilder(19,37).append(this.getName()).toHashCode();
	}

}
