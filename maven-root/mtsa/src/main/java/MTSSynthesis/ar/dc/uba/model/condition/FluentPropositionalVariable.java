package MTSSynthesis.ar.dc.uba.model.condition;

/**
 * Default implementation
 * @author gsibay
 *
 */
public class FluentPropositionalVariable implements Formula, PropositionalVariable {

	private Fluent fluent;

	public FluentPropositionalVariable(Fluent fluent) {
		this.fluent = fluent;
	}
	
	@Override
	public boolean evaluate(Valuation valuation) {
		return valuation.getValuation(this);
	}

	/* (non-Javadoc)
	 * @see ar.dc.uba.model.condition.PropositionalVariable#getName()
	 */
	public String getName() {
		return this.fluent.getName();
	}
	
	public String toString() {
		return this.getName();
	}
	
	public Fluent getFluent() {
		return fluent;
	}
	
}
