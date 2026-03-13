package MTSSynthesis.ar.dc.uba.model.condition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import MTSSynthesis.ar.dc.uba.model.language.Symbol;

/**
 *
 * @author gsibay
 *
 */
public class ZetaFunction implements Valuation {

	/**
	 * The key is the name of the fluent.
	 * The value is the Fluent and the current valuation
	 */
	private Map<String, FluentValuationTuple> fluentWithValuations;
	
	private ZetaFunction() {
		this.fluentWithValuations = new HashMap<String, FluentValuationTuple>();
	}
	
	public ZetaFunction(Set<Fluent> fluents) {
		this.fluentWithValuations = new HashMap<String, FluentValuationTuple>();
		
		for (Fluent fluent : fluents) {
			this.fluentWithValuations.put(fluent.getName(), new FluentValuationTuple(fluent));
		}
	}
	
	/* (non-Javadoc)
	 * @see ar.dc.uba.model.condition.Valuation#getValuation(ar.dc.uba.model.condition.PropositionalVariable)
	 */
	@Override
	public boolean getValuation(PropositionalVariable variable) {
		FluentValuationTuple fluentWithValuation = this.fluentWithValuations.get(variable.getName());
		return fluentWithValuation.getCurrentValue();
	}

	/**
	 * Returns an exact copy of the ZetaFunction
	 * @return
	 */
	public ZetaFunction getCopy() {
		ZetaFunction copy = new ZetaFunction();
		Set<Entry<String, FluentValuationTuple>> entrySet = this.fluentWithValuations.entrySet();
		for (Entry<String, FluentValuationTuple> entry : entrySet) {
			// copy each entry
			copy.fluentWithValuations.put(entry.getKey(), new FluentValuationTuple(entry.getValue()));
		}
		return copy;
	}
	
	public void apply(Symbol symbol) {
		Collection<FluentValuationTuple> fluentValuations = this.fluentWithValuations.values();
		for (FluentValuationTuple fluentValuationTuple : fluentValuations) {
			if(fluentValuationTuple.getFluent().getInitiatingActions().contains(symbol)) {
				// the symbol is a initiation action, the fluent is set to true
				fluentValuationTuple.setCurrentValuation(true);
			} else if (fluentValuationTuple.getFluent().getTerminatingActions().contains(symbol)) {
				// the symbol is a terminating action, the fluent is set to false
				fluentValuationTuple.setCurrentValuation(false);
			}
		}
	}
	
	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof ZetaFunction) {
			ZetaFunction zetaFunction = (ZetaFunction) anObject;
			return new EqualsBuilder()
				.append(this.fluentWithValuations, zetaFunction.fluentWithValuations).isEquals();
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return new HashCodeBuilder(17,39).append(this.fluentWithValuations).toHashCode();
	}	
	
	public String toString() {
		return this.fluentWithValuations.toString();
	}
	
	private class FluentValuationTuple {
		private Fluent fluent;
		private boolean currentValue;
		
		private FluentValuationTuple(FluentValuationTuple tuple) {
			this.fluent = tuple.getFluent();;
			this.currentValue = tuple.getCurrentValue();
		}
		
		public FluentValuationTuple(Fluent fluent) {
			this.fluent = fluent;
			this.currentValue = fluent.isInitialValue();
		}
		
		public boolean getCurrentValue() {
			return this.currentValue;
		}
		
		public void setCurrentValuation(boolean newValuation) {
			this.currentValue = newValuation;
		}
		
		public Fluent getFluent() {
			return this.fluent;
		}

		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append(this.fluent.getName()).append(" = ");
			buffer.append(this.currentValue);
			return buffer.toString();
		}
		
		public boolean equals(Object anObject) {
			if(this == anObject) {
				return true;
			}
			if(anObject instanceof FluentValuationTuple) {
				FluentValuationTuple fluentValuation = (FluentValuationTuple) anObject;
				return new EqualsBuilder().append(this.getFluent(), fluentValuation.getFluent())
					.append(this.getCurrentValue(), fluentValuation.getCurrentValue()).isEquals();
			} else {
				return false;
			}
		}
		
		public int hashCode() {
			return new HashCodeBuilder(17,39).append(this.fluent).append(this.currentValue).toHashCode();
		}		
		
	}
}
