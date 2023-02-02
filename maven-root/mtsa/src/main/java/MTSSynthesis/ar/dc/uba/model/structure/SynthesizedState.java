package MTSSynthesis.ar.dc.uba.model.structure;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import MTSSynthesis.ar.dc.uba.model.condition.ZetaFunction;
import MTSSynthesis.ar.dc.uba.model.language.Word;

/**
 * A state is a tuple.
 * It has the significative suffix and the obligations.
 * @author gsibay
 *
 */
public class SynthesizedState {

	private Word significativeSuffix;
	private Obligations obligations;
	
	/**
	 * The valuation
	 */
	private ZetaFunction zetaFunction;
	
	public SynthesizedState(Word significativeSuffix, Obligations obligations, ZetaFunction zetaFunction) {
		this.significativeSuffix = significativeSuffix;
		this.obligations = obligations;
		this.zetaFunction = zetaFunction.getCopy();
	}
	
	public Word getSignificativeSuffix() {
		return this.significativeSuffix;
	}
	
	public Obligations getObligations() {
		return this.obligations;
	}

	public ZetaFunction getZetaFunction() {
		return this.zetaFunction;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<").append(this.getSignificativeSuffix()).append(";")
			.append(this.getObligations()).append(this.zetaFunction).append(">");
		return buffer.toString();
	}
	
	
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (anObject instanceof SynthesizedState) {
			SynthesizedState state = (SynthesizedState) anObject;
			return (new EqualsBuilder()).append(this.getSignificativeSuffix(), state.getSignificativeSuffix()).append(
					this.getObligations(), state.getObligations()).append(this.getZetaFunction(), state.getZetaFunction()).isEquals();
		} else {
			return false;
		}
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 39).append(this.getSignificativeSuffix()).append(this.getObligations()).append(this.getZetaFunction())
				.toHashCode();
	}
}
