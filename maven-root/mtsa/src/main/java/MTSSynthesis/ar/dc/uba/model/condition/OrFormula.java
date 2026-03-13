package MTSSynthesis.ar.dc.uba.model.condition;

/**
 * @author gsibay
 *
 */
public class OrFormula extends BinaryFormula {

	
	public OrFormula(Formula leftFormula, Formula rightFormula) {
		super(leftFormula, rightFormula);
	}

	/* (non-Javadoc)
	 * @see ar.dc.uba.model.condition.Formula#evaluate(ar.dc.uba.model.condition.Valuation)
	 */
	@Override
	public boolean evaluate(Valuation valuation) {
		return this.getLeftFormula().evaluate(valuation)
				|| this.getRightFormula().evaluate(valuation);
	}

	@Override
	protected String operatorToString() {
		return "|";
	}

}
