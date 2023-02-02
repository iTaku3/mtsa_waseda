package MTSSynthesis.ar.dc.uba.model.condition;

/**
 * 
 * @author gsibay
 *
 */
public class AndFormula extends BinaryFormula {

	public AndFormula(Formula leftFormula, Formula rightFormula) {
		super(leftFormula, rightFormula);
	}

	@Override
	public boolean evaluate(Valuation valuation) {
		return this.getLeftFormula().evaluate(valuation)
				&& this.getRightFormula().evaluate(valuation);
	}

	@Override
	protected String operatorToString() {
		return "&";
	}
}
