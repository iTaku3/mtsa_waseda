package MTSSynthesis.ar.dc.uba.model.condition;

/**
 * A negated formula
 * @author gsibay
 *
 */
public class NotFormula implements Formula {

	private Formula formula;
	
	public NotFormula(Formula formula) {
		this.formula = formula;
	}
	
	@Override
	public boolean evaluate(Valuation valuation) {
		return !this.formula.evaluate(valuation);
	}

	public String toString() {
		return "!" + this.formula.toString();
	}
	
	public Formula getFormula() {
		return formula;
	}
}
